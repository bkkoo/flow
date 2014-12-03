package net.shantitree.flow.sys.lib.msg

import akka.actor.ActorRef

case class Subscribe(topic: String, subscriber: ActorRef)
