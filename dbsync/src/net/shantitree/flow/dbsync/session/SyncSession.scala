package net.shantitree.flow.dbsync.session

import java.util.UUID

import akka.actor.ActorRef

case class SyncSession(runners: Set[JobRunnerRef], ctrl: ActorRef, id: UUID=UUID.randomUUID())
