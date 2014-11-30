package net.shantitree.flow.gaia.qry.base

import net.shantitree.flow.gaia.model.Tables.Tblproduct
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.slick.qry.{QryCondition, BaseQry}

object ProductBase extends QryCondition[ProductBase] {
  def apply() = new ProductBase {}
  def apply(q: ProductBase#Q) = new ProductBase { override lazy val query = q }
}

trait ProductBase extends BaseQry {
  type Q = Query[Tblproduct, Tblproduct#TableElementType, Seq]
  lazy val query = Tblproduct.to[Seq]

}
