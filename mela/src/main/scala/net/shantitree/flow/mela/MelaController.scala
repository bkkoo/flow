package net.shantitree.flow.mela

import akka.actor.{Actor, ActorLogging}
import net.shantitree.flow.sys.event.BusinessEvent
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.sys.module.NamedActor

object MelaController extends NamedActor {
  val actorName = "AppController"
}

class MelaController extends Actor with ActorLogging {
  import context._

  val businessEvent = actorOf(Gak(system).props(BusinessEvent.actorName), name = BusinessEvent.actorName)

  def receive = {
    case _ =>
  }

}
