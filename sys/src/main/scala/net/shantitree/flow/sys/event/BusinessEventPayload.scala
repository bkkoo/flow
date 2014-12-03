package net.shantitree.flow.sys.event

object BusinessEventPayload {
  case class RidOfNewSales(rids: List[AnyRef])
  case class CodeOfNewSales(codes: List[String])
}
