package net.shantitree.flow.dbsync.module

import akka.actor.Actor
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.dbsync.AppController
import net.shantitree.flow.dbsync.session.{SyncSessionController, PostSession, PullSession}
import net.shantitree.flow.slick.module.TSqlDbModule

trait TSyncModule extends ScalaModule with TSqlDbModule {

  val sqlDbConfigPath = "sync.db"

  def syncModuleConfigure(): Unit = {

    bind[Actor].annotatedWithName(PullSession.actorName).to[PullSession]
    bind[Actor].annotatedWithName(PostSession.actorName).to[PostSession]
    bind[Actor].annotatedWithName(SyncSessionController.actorName).to[SyncSessionController]
    bind[Actor].annotatedWithName(AppController.actorName).to[AppController]

  }

}
