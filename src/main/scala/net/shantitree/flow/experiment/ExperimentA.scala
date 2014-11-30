package net.shantitree.flow.experiment

import com.google.inject.Guice
import net.shantitree.flow.base.bznet.model.PeriodicAccumulatedPV
import net.shantitree.flow.gaia.qry.base.{MemberBase, A0001Base}
import net.shantitree.flow.gaia.qry.view.{PeriodicAccumulatedPVView, SaleOrderHeaderView, AcpPositionView}
import net.shantitree.flow.gaia.sync.job.NewPeriodicAcmPV
import net.shantitree.flow.gaia.sync.module.SyncModule
import net.shantitree.flow.sys.lib.module.{AkkaModule, ConfigModule}
import net.shantitree.flow.sys.lib.orient.graph.{VertexHelper, GraphHelper}
import net.shantitree.flow.dbsync.msg.job.PullRequest
import net.shantitree.flow.module.FlowModule
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.codingwell.scalaguice.InjectorExtensions._
import net.shantitree.flow.slick.qry.param.{YearAndMonth, CreatedDuration, EqCode}
import org.joda.time.DateTime
import net.shantitree.flow.sys.lib.model.conversion.ModelCtorArgs._

object ExperimentA {

  def apply() = {
    //InitDb()
    val injector = Guice.createInjector(
      new ConfigModule(),
      new AkkaModule(),
      new FlowModule(),
      new SyncModule()
    )

    implicit val db = injector.instance[Database]

    val query = PeriodicAccumulatedPVView.query(YearAndMonth("2006", "12"))
    val result = db.withSession { implicit s => query.list }
    val models = NewPeriodicAcmPV.modelConverter.convert(result)
    val limit10 = models.take(10)
    println(limit10)

  }

}

