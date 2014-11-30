package net.shantitree.flow.module

import akka.actor.Actor
import com.google.inject._
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.biz.lib.JapinComPeriodUtil
import net.shantitree.flow.curaccumpv.app.CurAccumPVApp
import net.shantitree.flow.sys.lib.orient.graph.TGraphSession
import net.shantitree.flow.sys.{JapinComPeriodUtil, ComPeriodUtil, DataChangeEventBus}
import net.shantitree.flow.sys.graph.GraphSession

object FlowModule {
  lazy val mainDataChangeEventBus = new DataChangeEventBus
}

class FlowModule extends AbstractModule with ScalaModule {
  import FlowModule._

  override def configure(): Unit = {

    bind[TGraphSession].toInstance(GraphSession)
    bind[ComPeriodUtil].toInstance(JapinComPeriodUtil)
    bind[Actor].annotatedWithName(CurAccumPVApp.controllerName).to[CurAccumPVApp.Controller]

  }

  @Provides
  def provideDataChangeEventBus(): DataChangeEventBus = {
    mainDataChangeEventBus
  }

}





