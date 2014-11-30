package net.shantitree.flow

import akka.actor.ActorSystem
import com.google.inject.Guice
import net.shantitree.flow.experiment.{ExperimentC, ExperimentA}
import net.shantitree.flow.gaia.sync.module.SyncModule
import net.shantitree.flow.sys.lib.module.{AkkaModule, ConfigModule}
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.dbsync.msg.job.Start
import net.shantitree.flow.module.FlowModule
import net.codingwell.scalaguice.InjectorExtensions._
import net.shantitree.flow.sys.InitDb

object Main {

  def main(args: Array[String]): Unit = {
    InitDb()
    //runGaiaSync()
    args match {
      case Array("sync") => runGaiaSync()
      case Array("exp", "A") => ExperimentA()
      case Array("exp", "C") => ExperimentC()
      case _ =>
    }
  }

  def runGaiaSync(): Unit = {
    val injector = Guice.createInjector(
      new ConfigModule(),
      new AkkaModule(),
      new FlowModule(),
      new SyncModule()
    )

    val system = injector.instance[ActorSystem]
    val jobCtrl = system.actorOf(Gak(system).props("SyncSessionCtrl"), name = "SyncSessionCtrl")

    jobCtrl ! Start

  }

}
