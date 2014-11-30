package net.shantitree.flow.experiment

import akka.actor.ActorSystem
import com.google.inject.Guice
import net.shantitree.flow.base.biz.JapinComPeriodUtil
import net.shantitree.flow.biz.lib.PV
import net.shantitree.flow.curaccumpv.app.CurAccumPVApp
import net.shantitree.flow.curaccumpv.app.msg.ReGenerateAll
import net.shantitree.flow.base.saleorder.sys.SaleOrderBL
import net.shantitree.flow.sys.lib.DataChangeEventBus
import net.shantitree.flow.sys.lib.module.{AkkaModule, ConfigModule}
import net.shantitree.flow.module.FlowModule
import net.codingwell.scalaguice.InjectorExtensions._
import net.shantitree.flow.sys.graph.GraphSession

object ExperimentC {
  def apply(): Unit = {
    val injector = Guice.createInjector(
       new ConfigModule()
      ,new AkkaModule()
      ,new FlowModule()
    )

    val system = injector.instance[ActorSystem]

    val bus = new DataChangeEventBus

    val ctrl = system.actorOf(CurAccumPVApp.props(bus, GraphSession, JapinComPeriodUtil))

    ctrl ! ReGenerateAll(Some(20140101))

  }
}
