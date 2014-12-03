package net.shantitree.flow.dbsync

import akka.actor.Actor
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.dbsync.session.{PostSession, PullSession, SyncSessionController}
import net.shantitree.flow.slick.SqlDbModule

trait DbSyncModule extends ScalaModule with SqlDbModule {

  val sqlDbConfigPath = "sync.db"

  def syncModuleConfigure(): Unit = {

    bind[Actor].annotatedWithName(PullSession.actorName).to[PullSession]
    bind[Actor].annotatedWithName(PostSession.actorName).to[PostSession]
    bind[Actor].annotatedWithName(SyncSessionController.actorName).to[SyncSessionController]
    bind[Actor].annotatedWithName(DbSyncController.actorName).to[DbSyncController]

  }

}
