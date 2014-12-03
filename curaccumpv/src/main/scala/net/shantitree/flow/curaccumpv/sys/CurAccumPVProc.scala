package net.shantitree.flow.curaccumpv.sys

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.curaccumpv.model.CurAccumPVLogVW
import net.shantitree.flow.base.saleorder.sys.SaleOrderBL
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.sys.biz.PV
import net.shantitree.flow.sys.lib.orient.oql.operator.Between
import BzNodeExt._

object CurAccumPVProc {
  import net.shantitree.flow.sys.lib.orient.oql.operator.Relational._


  def qryUnprocessedSales(comPeriod: Int)(implicit g: OrientGraph): Vector[SaleOrderHeaderVW] = {
    val lastCode = LoggingUtil.findLastLog(comPeriod).map(_.to_code)
    val itr = SaleOrderBL.qryOnComPeriod(comPeriod, lastCode).scalaIterator()
    if (itr.hasNext) { itr.toVector } else { Vector() }
  }

  def qryPendingProcessSales()(implicit g: OrientGraph): TraversableOnce[(Integer, TraversableOnce[SaleOrderHeaderVW])] = {
    for { pending <- LoggingUtil.findPending() } yield {
      pending.com_period -> SaleOrderBL.getSaleOrders( Between { pending.from_code } and { pending.to_code } )
    }
  }

  private [curaccumpv] def updateAccumPV(comPeriod:Int, receivers: Traversable[(String, (BzNodeVW, PV))], log: CurAccumPVLogVW, regenerateAllFlag: Boolean)(implicit g: OrientGraph): Traversable[(String, BzNodeVW, PV)] = {
    log.setToSuccess()
    if (regenerateAllFlag) {
      CurAccumPVDML.deleteAllOnComPeriod(comPeriod)
    }
    for {
      (code, (bzNode, pv)) <- receivers
    } yield {
      bzNode
        .curAccumPV(comPeriod)
        .map(_.setPV(pv))
        .getOrElse(CurAccumPVDML.addNew(bzNode, comPeriod, pv))
      (code, bzNode, pv)
    }
  }
}
