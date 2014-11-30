package net.shantitree.flow.curaccumpv.app.msg

import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.curaccumpv.model.CurAccumPVLogVW
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.sys.PV

case class NewSales(codes: Iterable[String])
case class ReGenerateAll(comPeriod: Option[Int])
case object InitialCheckForNewSales
case class DeleteAll(comPeriod: Int)

case object StartMon
case object StopMon

private[app] case class AggregateReceivers(comPeriod: Int, saleOrders: Iterable[SaleOrderHeaderVW], log: CurAccumPVLogVW, regenerateAllFlag: Boolean=false)
private[app] case class UpdateAccumPV(comPeriod: Int, receivers: Iterable[(String, (BzNodeVW, PV))], log: CurAccumPVLogVW, regenerateAllFlag: Boolean)

