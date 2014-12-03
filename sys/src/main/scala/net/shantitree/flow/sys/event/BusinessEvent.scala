package net.shantitree.flow.sys.event

import akka.actor.{Actor, ActorLogging}
import net.shantitree.flow.sys.lib.eventbus.SimpleBus._
import net.shantitree.flow.sys.lib.eventbus.{IdEventBus, SimpleBus}
import net.shantitree.flow.sys.module.NamedActor

import scala.concurrent.Future

object BusinessEvent extends NamedActor with IdEventBus {
  val actorName = "BusinessEvent"
  lazy val eventBus = new SimpleBus
  lazy val eventBusId = 'BusinessEvent
}

class BusinessEvent extends Actor with ActorLogging {
  import net.shantitree.flow.sys.event.BusinessEvent._

  implicit val ec = context.dispatcher

  def receive = {
    case Subscribe(topic, subscriber) =>
      eventBus.subscribe(subscriber, topic)
      sender() ! Subscribed(topic, eventBusId)

    case Unsubscribe(subscriber, from) =>
      eventBus.unsubscribe(subscriber, from)
      sender() ! Unsubscribed(from, eventBusId)

    case Publish(topic, payload) =>
      Future { eventBus.publish(Event(topic, payload)) }
  }
}
