package net.shantitree.flow.gaia.qry.basic

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._

object MemberDataJournalTypeQuery extends SysParamQryByType {
  // MEM1100 -> Member Data -> Action -> Alteration Type Column In GAIA UI
  val keyType = "cdSub0001"

  def equal(journalType: String) = partialQuery.filter { p =>  p._1.szkey === journalType }
  def in(journalTypes: List[String]) = partialQuery.filter { p =>  p._1.szkey inSetBind journalTypes }
}
