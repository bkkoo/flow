package net.shantitree.flow.gaia.qry.base

import net.shantitree.flow.slick.qry.param.{InCodes, CodeRange, EqCode, CreatedDuration}
import net.shantitree.flow.gaia.model.Tables.Tblordermaster
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.slick.qry.{QryCondition, BaseQry}

import scala.slick.lifted.Query

object SaleOrderHeaderBase extends QryCondition[SaleOrderHeaderBase]{

  def apply() = new SaleOrderHeaderBase {}
  def apply(q: SaleOrderHeaderBase#Q) = new SaleOrderHeaderBase { override lazy val query = q}

  implicit val c1 = condition[EqCode](p => {
    query.filter{ m => m.cdorderno === p.code }
  })

  implicit val c2 = condition[CreatedDuration](p => {
    val (from, until) = p.rangeTimestamp
    query.filter{ m => m.dtbusinessdate >= from && m.dtbusinessdate < until }
  })

  implicit val c3 = condition[CodeRange](p=> {
    query.filter{m => m.cdorderno >= p.from && m.cdorderno <= p.to}
  })

  implicit val c4 = condition[InCodes](p=> {
    query.filter { m => m.cdorderno inSetBind p.codes}
  })

}

trait SaleOrderHeaderBase extends BaseQry {
  type Q = Query[Tblordermaster, Tblordermaster#TableElementType, Seq]
  lazy val query = Tblordermaster.to[Seq]
}
