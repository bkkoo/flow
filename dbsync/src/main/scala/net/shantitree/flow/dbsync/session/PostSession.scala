package net.shantitree.flow.dbsync.session

import akka.actor.Actor
import com.google.inject.Inject
import net.shantitree.flow.sys.lib.orient.graph.TGraphSession
import net.shantitree.flow.dbsync.model.SyncLogUtil
import net.shantitree.flow.dbsync.msg.job._
import net.shantitree.flow.sys.module.NamedActor

import scala.util.Try

object PostSession extends NamedActor {
  val actorName = "PostSession"
}

class PostSession @Inject() (val session: TGraphSession)  extends Actor {

  def receive = {

    case PostRequest(runner, updater, slog) =>
      try {
         session.tx { implicit g =>
          val result = updater.run()
          val successLog = SyncLogUtil.recordLogSuccess(slog)
          runner.actorRef ! UpdateFinished(result, successLog)
        }
      } catch {
        case e:Throwable =>
          runner.actorRef ! FailOnUpdating(e, updater, slog)
      }

    case e:ExecInGraphSession =>
      e.runner.actorRef ! ExecInGraphSessionResult(e.slog, Try{
        session.tx { g => e.fn(g) }
      })

  }

}
