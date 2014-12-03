package net.shantitree.flow.sys.lib.actor

import akka.actor.{ActorRef, ActorSystem}
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.sys.module.NamedActor

trait GuiceAkkaKernelUtil {
  val system: ActorSystem

  def createActor(actorName: String): ActorRef = {
    system.actorOf(Gak(system).props(actorName), name = actorName)
  }

  def createActor(namedActor: NamedActor): ActorRef = {
    createActor(namedActor.actorName)
  }
}
