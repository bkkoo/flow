package net.shantitree.flow.dbsync.module

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.{Provider, Inject}
import net.shantitree.flow.sys.lib.guice.Gak

class DbSessionExecProvider @Inject() (system: ActorSystem) extends Provider[ActorRef] {
  override def get() = system.actorOf(Gak(system).props("DbSessionExec"))
}
