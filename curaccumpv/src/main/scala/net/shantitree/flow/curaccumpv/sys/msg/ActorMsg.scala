package net.shantitree.flow.curaccumpv.sys.msg

import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.curaccumpv.model.CurAccumPVLogVW
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.sys.lib.biz.PV

case class NewSales(codes: Iterable[String])
case class ReGenerateAll(comPeriod: Option[Int])
case object InitialCheckForNewSales
case class DeleteAll(comPeriod: Int)

case object StartMon
case object StopMon

private[curaccumpv] case class AggregateReceivers(comPeriod: Int, saleOrders: Iterable[SaleOrderHeaderVW], log: CurAccumPVLogVW, regenerateAllFlag: Boolean=false)
private[curaccumpv] case class UpdateAccumPV(comPeriod: Int, receivers: Iterable[(String, (BzNodeVW, PV))], log: CurAccumPVLogVW, regenerateAllFlag: Boolean)

