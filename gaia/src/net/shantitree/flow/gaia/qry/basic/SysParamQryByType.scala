package net.shantitree.flow.gaia.qry.basic

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._

trait SysParamQryByType {
  val keyType: String
  val getQuery = SysParamQry.getQuery(keyType, _: String, _: String)
  lazy val partialQuery = SysParamQry.partialQuery.filter { p => p._1.szleading === keyType && p._2.cdlanguage === "en"}
  def select(key: String, lang: String="en")(implicit db: Database) = {
    getQuery(key, lang)
  }
  def apply() = partialQuery
}
