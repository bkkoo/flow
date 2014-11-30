package net.shantitree.flow.dbsync.session

import akka.actor.ActorRef

case class JobRunnerRef(name: String, actorRef: ActorRef)
