package net.shantitree.flow.curaccumpv.app

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.curaccumpv.model.{CurAccumPVLogStatus, CurAccumPVLogVW, CurAccumPVLog, CurAccumPVLogField}
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.sys.DML
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import net.shantitree.flow.sys.lib.orient.oql.Projection._
import net.shantitree.flow.sys.lib.orient.oql.operator.Relational._
import net.shantitree.flow.sys.lib.orient.oql.operator.Logical._

object LoggingUtil extends DML[CurAccumPVLog] {

  val F = CurAccumPVLogField

  def findLastLog(comPeriod: Integer)(implicit g: OrientGraph): Option[CurAccumPVLogVW] = {
    import F._
    val sale_order_code = "sale_order_code"
    val itr = g.qryV[CurAccumPVLog]( Max(to_code).as(sale_order_code) )( com_period === comPeriod ).iterator()
    if (itr.hasNext) {
      val lastSaleOrderCode = itr.next().getProperty[String](sale_order_code)
      g.findV(CurAccumPVLogVW)(com_period -> comPeriod, to_code -> lastSaleOrderCode)
    } else {
      None
    }
  }

  def findPending()(implicit g: OrientGraph):TraversableOnce[CurAccumPVLogVW] = {
    import CurAccumPVLogStatus._
    g.qryV(CurAccumPVLogVW)( F.status === Failure or F.status === Processing  ).scalaIterator()
  }

  private[app] def recordLog(log: CurAccumPVLog)(implicit g: OrientGraph): CurAccumPVLogVW = {
    g.addV(CurAccumPVLogVW, log)
  }

  private[app] def recordProcess(comPeriod:Integer, saleOrders: Vector[SaleOrderHeaderVW])(implicit g: OrientGraph) : Option[CurAccumPVLogVW] = {
    if (saleOrders.nonEmpty) {
      val fromCode = saleOrders.minBy(_.code).code
      val toCode = saleOrders.maxBy(_.code).code
      val log = CurAccumPVLog( com_period = comPeriod ,from_code = fromCode ,to_code = toCode, status = CurAccumPVLogStatus.Processing )
      Some(recordLog(log))
    } else {
      None
    }
  }

}

