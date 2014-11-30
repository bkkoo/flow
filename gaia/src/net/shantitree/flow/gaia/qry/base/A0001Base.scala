package net.shantitree.flow.gaia.qry.base

import net.shantitree.flow.gaia.qry.basic.{PositionQuery, MemberDataJournalTypeQuery}
import net.shantitree.flow.slick.qry.param._
import net.shantitree.flow.slick.qry.{QryCondition, BaseQry}
import net.shantitree.flow.gaia.model.Tables.Tbla0001
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.gaia.qry.basic.SysParamQry.{SysParamTbl, SysParamTblElem}

trait A0001QryCondition[BQ <: A0001Base] extends QryCondition[BQ]{

  def apply(): BQ
  def apply(q: BQ#Q): BQ

  implicit val c1 = condition[EqCode]{ p =>
    query.filter{ case ((m, _),_) => m.cdmember === p.code }
  }

  implicit val c2 = condition[CodeRange]{ p =>
    query.filter{ case ((m, _),_) =>  m.cdmember >= p.from && m.cdmember <= p.to }
  }

  implicit val c3 = condition[InCodes]{ p =>
    query.filter { case ((m, _),_) => m.cdmember inSetBind p.codes }
  }
  implicit val c4 = condition[CreatedDuration]{ p =>
    val (from, until) = p.rangeTimestamp
    query.filter { case ((m, _),_) => m.dtcreate >= from && m.dtcreate < until}
  }

}

trait A0001Base extends BaseQry {

  type Q = Query[
    ( ( Tbla0001, SysParamTbl ) , SysParamTbl ), ( ( Tbla0001#TableElementType, SysParamTblElem ), SysParamTblElem ) ,
    Seq
  ]

  // MEM1100 -> Member Data -> Action -> Alteration Type Column In GAIA UI
  val statusJournalTypes: List[String]

  lazy val journalTypeQuery =  statusJournalTypes match {
    case x::Nil =>
      MemberDataJournalTypeQuery.equal(x)
    case x::xs =>
      MemberDataJournalTypeQuery.in(statusJournalTypes)
    case Nil =>
      MemberDataJournalTypeQuery()
  }


  lazy val query = Tbla0001
    .to[Seq]
    .innerJoin( PositionQuery() )
    .on { (t, tt) => tt._1.szkey === t.cdpersg }
    .innerJoin(journalTypeQuery)
    .on { (t_tt, tt) => t_tt._1.cdsub0001 === tt._1.szkey }
}
