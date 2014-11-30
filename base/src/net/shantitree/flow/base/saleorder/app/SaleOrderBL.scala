package net.shantitree.flow.base.saleorder.app

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.{OrientVertex, OrientGraph}
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.sys.lib.lang.JIterableMapper
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import net.shantitree.flow.sys.lib.orient.graph.VertexHelper._
import net.shantitree.flow.sys.lib.orient.oql.operator.BetweenThisAndThat
import net.shantitree.flow.sys.lib.orient.oql.operator.Logical._
import net.shantitree.flow.sys.lib.orient.oql.operator.Relational._

object SaleOrderBL extends SaleOrderBaseLogic {

  def qryOnComPeriod(comPeriod: Integer, afterCode: Option[String]=None)(implicit g: OrientGraph): JIterableMapper[OrientVertex, SaleOrderHeaderVW] = {
    afterCode.map { code =>
      g.qryV(SaleOrderHeaderVW){ (Fh.com_period === comPeriod) and ( Fh.code >> code ) }
    }.getOrElse {
      g.qryV(SaleOrderHeaderVW){ Fh.com_period === comPeriod }
    }
  }

  def getBzNode(vSaleOrderHeader: Vertex)(implicit g: OrientGraph): Option[Vertex] = {
    vSaleOrderHeader.out1(E.Benefit)
  }

  def getBzNode(vwSaleOrderHeader: SaleOrderHeaderVW)(implicit g: OrientGraph): Option[BzNodeVW] = {
    vwSaleOrderHeader.v.out1(E.Benefit).map(BzNodeVW)
  }

  def getSaleOrders(codes: TraversableOnce[String])(implicit g: OrientGraph):Iterable[SaleOrderHeaderVW] = {
    val result = for {
      code <- codes
      optSaleOrder = g.findV(SaleOrderHeaderVW)(Fh.code -> code) if optSaleOrder.nonEmpty
      saleOrder = optSaleOrder.get
    } yield {
      saleOrder
    }
    result.toIterable
  }

  def getSaleOrders(between: BetweenThisAndThat[String])(implicit g: OrientGraph): TraversableOnce[SaleOrderHeaderVW] = {
    g.qryV(SaleOrderHeaderVW)(between).scalaIterator()
  }

}
