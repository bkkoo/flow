package net.shantitree.flow.sys.lib.eventbus

import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}

object SimpleBus {
  case class Subscribe(topic: Symbol, subscriber: ActorRef)
  case class Subscribed(topic: Symbol, eventBusId: Symbol)
  case class Unsubscribe(subscriber:ActorRef, from: Symbol)
  case class Unsubscribed(from: Symbol, eventBusId: Symbol)
  case class Publish(topic: Symbol, payload: Any)
  case class Event(topic: Symbol, payload: Any)
}

class SimpleBus  extends EventBus with LookupClassification {

  /**
   * Publishes the payload of the SimpleEventMsg when the topic of the
   * envelope equals the String specified when subscribing.
   */

  type Event = SimpleBus.Event
  type Classifier = Symbol
  type Subscriber = ActorRef

  // is used for extracting the classifier from the incoming events
  override protected def classify(event: Event): Classifier = event.topic

  /* will be invoked for each event for all subscribers which registered themselves
   for the event’s classifier */
  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event.payload
  }
  /* must define a full order over the subscribers, expressed as expected from
   `java.lang.Comparable.compare` */
  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int = a.compareTo(b)

  /* determines the initial size of the index data structure
   used internally (i.e. the expected number of different classifiers) */
  override protected def mapSize: Int = 128
}
