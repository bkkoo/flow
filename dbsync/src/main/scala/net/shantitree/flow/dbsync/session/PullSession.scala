package net.shantitree.flow.dbsync.session

import akka.actor.Actor
import com.google.inject.Inject
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.dbsync.msg.job._
import net.shantitree.flow.sys.module.NamedActor

object PullSession extends NamedActor {
  val actorName = "PullSession"
}
class PullSession @Inject() (db: Database) extends Actor {

  implicit val $db = db

  def receive = {
    case s@PullRequest(puller, slog) => try {
      sender ! PulledDatum(puller.run(), slog)
    } catch {
      case e: Throwable =>
        sender ! FailOnPulling(e, slog)
    }
  }

}
