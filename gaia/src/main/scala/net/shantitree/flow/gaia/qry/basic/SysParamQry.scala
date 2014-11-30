package net.shantitree.flow.gaia.qry.basic

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.gaia.model.Tables.{Tblsysparameterdata, Tblsysparameterdatad}

object SysParamQry {

  type SysParamTbl = (Tblsysparameterdata, Tblsysparameterdatad)
  type SysParamTblElem = (Tblsysparameterdata#TableElementType, Tblsysparameterdatad#TableElementType)

  def select(keyGroup: String, key: String, lang: String="en")(implicit db: Database) = {
    val query = getQuery(keyGroup, key, lang)
    db withSession { implicit session => query.list}
  }

  lazy val partialQuery = {
    Tblsysparameterdata
      .innerJoin(Tblsysparameterdatad)
      .on { (s, d) => s.uid === d.uidsysparameterdata }
  }

  val getQuery = (keyGroup: String, key: String, lang: String) => {
    partialQuery
      .filter { p => p._1.szleading === keyGroup && p._1.szkey === key && p._2.cdlanguage === lang}
      .map { p => (p._1.szkey, p._2.szdesc)}
  }

  def apply() = partialQuery
}
