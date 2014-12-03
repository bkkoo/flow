package net.shantitree.flow.dbsync

import akka.actor.{ActorRef, Actor}
import com.google.inject.Inject
import com.google.inject.name.Named
import net.shantitree.flow.dbsync.session.{SyncSessionController, PostSession, PullSession}
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.sys.lib.module.NamedActor
import net.shantitree.flow.sys.lib.msg.{Stop, Start}

object AppController extends NamedActor {
  val actorName = "AppController"
}

class AppController extends Actor {
  import context._

  val pullSession:ActorRef = actorOf(Gak(system).props(PullSession.actorName), name = PullSession.actorName)
  val postSession:ActorRef = actorOf(Gak(system).props(PostSession.actorName), name = PostSession.actorName)
  val syncSessionController:ActorRef = actorOf(Gak(system).props(SyncSessionController.actorName), name = SyncSessionController.actorName)

  override def preStart():Unit = {
  }

  def receive  = {

    case Start =>
      syncSessionController ! Start

    case Stop =>
      syncSessionController ! Stop
  }

}
