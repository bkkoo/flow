package net.shantitree.flow.sys.lib.actor

import akka.actor.{Actor, ActorRef}
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.sys.module.NamedActor

trait GuiceActorUtil { this: Actor =>

  def createActor(actorName: String):ActorRef = {
    context.actorOf(Gak(context.system).props(actorName), name = actorName)
  }

  def createActor(namedActor: NamedActor):ActorRef = {
    createActor(namedActor.actorName)
  }

}
