package net.shantitree.flow.dbsync.module

import akka.actor.{Actor, ActorRef}
import com.google.inject._
import com.typesafe.config.Config
import name.Named
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.dbsync.job.SyncJobConfig
import net.shantitree.flow.dbsync.session.{GraphSessionExec, DbSessionExec}

trait TSyncModule { this: ScalaModule =>

  val syncJobConfigPath: String

  def syncModuleConfigure(): Unit = {

    bind[Actor].annotatedWithName("DbSessionExec").to[DbSessionExec]
    bind[ActorRef].annotatedWithName("DbSessionExec").toProvider[DbSessionExecProvider].asEagerSingleton()

    bind[Actor].annotatedWithName("GraphSessionExec").to[GraphSessionExec]
    bind[ActorRef].annotatedWithName("GraphSessionExec").toProvider[GraphSessionExecProvider].asEagerSingleton()

  }

  @Provides @Named("SyncJobConfig")
  def getSyncJobConfig(@Inject() config: Config):Config = {
    try {
      config.getConfig(syncJobConfigPath)
    } catch { case e:Exception =>
      throw new RuntimeException(s"Can't get sync job configuration on path '$syncJobConfigPath'. Following error occur: ${e.getStackTrace}")
    }
  }

}
