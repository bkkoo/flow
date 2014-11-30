package net.shantitree.flow.gaia.qry.base

import net.shantitree.flow.slick.qry.param._
import net.shantitree.flow.slick.qry.{BaseQry, QryCondition}
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.gaia.model.Tables.Tblmemberhis

object PeriodicAccumulatedPVBase extends QryCondition[PeriodicAccumulatedPVBase] {

  def apply() = new PeriodicAccumulatedPVBase {}
  def apply(q: PeriodicAccumulatedPVBase#Q) = new PeriodicAccumulatedPVBase { override lazy val query = q}

  implicit val c1 = condition[YearAndMonth](p => {
    query.filter { m => m.szdatayear === p.year && m.szdatamonth === p.month}
  })

  implicit val c2 = condition[CreatedDuration](p => {
    val (from, until) = p.rangeTimestamp
    query.filter { m => m.dtcreate >= from && m.dtcreate < until }
  })

}
trait PeriodicAccumulatedPVBase extends BaseQry {
  type Q = Query[Tblmemberhis, Tblmemberhis#TableElementType, Seq]
  lazy val query = Tblmemberhis.to[Seq]
}
