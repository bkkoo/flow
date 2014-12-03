package net.shantitree.flow.dbsync

import akka.actor.Actor
import net.shantitree.flow.dbsync.session.{SyncSessionController, PostSession, PullSession}
import net.shantitree.flow.sys.lib.actor.GuiceActorUtil
import net.shantitree.flow.sys.lib.msg.{Stop, Start}
import net.shantitree.flow.sys.module.NamedActor

object DbSyncController extends NamedActor {
  val actorName = "DbSyncController"
}

class DbSyncController extends Actor with GuiceActorUtil {

  val pullSession = createActor(PullSession)
  val postSession = createActor(PostSession)
  val syncSessionController = createActor(SyncSessionController)

  override def preStart():Unit = {
  }

  def receive  = {

    case Start =>
      syncSessionController ! Start

    case Stop =>
      syncSessionController ! Stop
  }

}
