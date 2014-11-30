package net.shantitree.flow.gaia.qry.base

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.slick.qry.param.{InCodes, CodeRange, EqCode}
import net.shantitree.flow.gaia.qry.basic.ProductUomQuery
import net.shantitree.flow.gaia.model.Tables.{Tblorderdetail, Tblproduct}
import net.shantitree.flow.slick.qry.{QryCondition, BaseQry}
import net.shantitree.flow.gaia.qry.basic.SysParamQry.{SysParamTblElem, SysParamTbl}

object SaleOrderItemBase extends QryCondition[SaleOrderItemBase]{

  def apply() = new SaleOrderItemBase {}
  def apply(q: SaleOrderItemBase#Q) = new SaleOrderItemBase { override lazy val query = q}

  implicit val c1 = condition[EqCode]{ p =>
    query.filter{ case ((m, _),_) => m.cdorderno === p.code }
  }

  implicit val c2 = condition[CodeRange]{ p =>
    query.filter{ case ((m, _),_) =>  m.cdorderno >= p.from && m.cdorderno <= p.to }
  }

  implicit val c3 = condition[InCodes]{ p =>
    query.filter { case ((m, _),_) => m.cdorderno inSetBind p.codes }
  }

}

trait SaleOrderItemBase extends BaseQry {
  type Q = Query[
    ((Tblorderdetail, SysParamTbl ), Tblproduct),
    ((Tblorderdetail#TableElementType, SysParamTblElem), Tblproduct#TableElementType),
    Seq
    ]

  lazy val query = Tblorderdetail
    .to[Seq]
    .leftJoin(ProductUomQuery())
    .on{ (t, tt) => tt._1.szkey === t.cdunitsale }
    .leftJoin(ProductBase().query)
    .on{ (t_tt, t) => t_tt._1.szprodid === t.szprodid }

}
