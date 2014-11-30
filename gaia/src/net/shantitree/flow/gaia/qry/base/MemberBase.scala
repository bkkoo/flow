package net.shantitree.flow.gaia.qry.base

import net.shantitree.flow.slick.qry.param.{InCodes, CodeRange, EqCode, CreatedDuration}
import net.shantitree.flow.gaia.model.Tables.Tblmember
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.slick.qry.{QryCondition, BaseQry}

import scala.slick.lifted.Query

object MemberBase extends QryCondition[MemberBase]{

  def apply() = new MemberBase {}
  def apply(q: MemberBase#Q) = new MemberBase { override lazy val query = q}

  implicit val c1 = condition[CreatedDuration](p => {
    val (from, until) = p.rangeTimestamp
    query.filter { m => m.dtcreate >= from && m.dtcreate < until }
  })

  implicit val c2 = condition[EqCode](p => {
    query.filter{ m=> m.cdmember === p.code }
  })

  implicit val c3 = condition[CodeRange](p => {
    val from = p.from
    val to = p.to
    query.filter{m => m.cdmember >= from && m.cdmember <= to}
  })

  implicit val c4 = condition[InCodes](p => {
    query.filter { m => m.cdmember inSetBind p.codes}
  })

}

trait MemberBase extends BaseQry {
  type Q = Query[Tblmember, Tblmember#TableElementType, Seq]
  lazy val query = Tblmember.to[Seq]
}
