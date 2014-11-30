package net.shantitree.flow.base.saleorder.app

import java.lang.{Iterable => JIterable}

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.{Direction, Vertex}
import net.shantitree.flow.base.bznet.app.{SeedNode, BzNetDML}
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.biz.lib.{PV, TDML}
import net.shantitree.flow.sys.constant.EdgeLabelConst
import net.shantitree.flow.base.partner.sys.{UnknownPartner, PartnerDML}
import net.shantitree.flow.base.partner.model.PartnerVW
import net.shantitree.flow.base.saleorder.model._
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

 object SaleOrderDML extends TDML[SaleOrder] with SaleOrderBaseLogic {

  private val e = EdgeLabelConst
  private val f = SaleOrderHeaderField


  def addNew(model: SaleOrder)(implicit g: OrientGraph):(SaleOrderHeaderVW, Iterable[SaleOrderItemVW]) = {
    val header = model.header
    val items = model.items
    val vwHeader = g.addV(SaleOrderHeaderVW, header)
    val vwItems = items.map { item =>
      joinItem(g.addV(SaleOrderItemVW,item), vwHeader)._1
    }
    linkBuyer(vwHeader)
    linkBenefit(vwHeader)
    (vwHeader, vwItems)
  }

  def joinItem(item: Vertex, header: Vertex)(implicit g: OrientGraph): (Vertex, Vertex) = {
    header.addEdge(e.Item, item)
    (item, header)
  }

  def joinItem(item: SaleOrderItemVW, header: SaleOrderHeaderVW)(implicit g: OrientGraph): (SaleOrderItemVW, SaleOrderHeaderVW) = {
    joinItem(item.v, header.v)
    (item, header)
  }

  def joinItem(item: SaleOrderItemVW)(implicit g: OrientGraph): Try[(SaleOrderItemVW, SaleOrderHeaderVW)] = {
    g.findV(SaleOrderHeaderVW)(f.code -> item.order_code )
      .map { vw => Success(joinItem(item, vw)) }
      .getOrElse {
        Failure(new RuntimeException(s"Can't find sale order header code '${item.order_code}' for item '${item.v.getId}' "))
      }
  }

  def linkBuyer(header: SaleOrderHeaderVW)(implicit g: OrientGraph): (SaleOrderHeaderVW, PartnerVW) = {
    if (header.partner_code == "" || header.partner_code == null) {
      findBuyerByMemberCode(header) match {
        case Some(vwBuyer) =>
          vwBuyer.v.addEdge(e.Buy, header.v)
          header.set_partner_code(vwBuyer.code)
          (header, vwBuyer)
        case None =>
          linkToUnknownBuyer(header)
      }
    } else {
      findBuyer(header) match {
        case Some(vwBuyer) =>
          vwBuyer.v.addEdge(e.Buy, header.v)
          (header, vwBuyer)
        case None =>
          linkToUnknownBuyer(header)

      }
    }
  }

  def linkToUnknownBuyer(header: SaleOrderHeaderVW)(implicit g: OrientGraph): (SaleOrderHeaderVW, PartnerVW) = {
    val vwBuyer = UnknownPartner.get()
    vwBuyer.v.addEdge(e.Buy, header.v)
    (header, vwBuyer)
  }

  def linkOrphanBenefit(header: SaleOrderHeaderVW)(implicit g: OrientGraph):(SaleOrderHeaderVW, BzNodeVW) = {
    val vwSeedNode = SeedNode.get()
    header.v.addEdge(e.Benefit, vwSeedNode.v)
    (header, vwSeedNode)
  }

  def finalizeOrphanBenefits()(implicit g: OrientGraph):Unit = {

    val vwSeedNode = SeedNode.get()

    vwSeedNode.v.getEdges(Direction.IN, e.Benefit).asScala.foreach { edge =>

      val vwHeader = SaleOrderHeaderVW(edge.getVertex(Direction.OUT))
      val (_, vwBNode) = linkBenefit(vwHeader)

      if (vwBNode.member_code != vwSeedNode.member_code) {
        // found BzNode that has right for this benefit so remove it from SeedNode
        edge.remove()
      }
    }
  }

  def finalizeOrphanBuyers()(implicit g: OrientGraph):Unit = {
    val vwUnknown = UnknownPartner.get()
    vwUnknown.v.getEdges(Direction.OUT, e.Buy).asScala.foreach { edge =>
      val vwHeader = SaleOrderHeaderVW(edge.getVertex(Direction.IN))
      val (_, vwPartner) = linkBuyer(vwHeader)
      if (vwUnknown.member_code != vwPartner.member_code) {
        // found Buyer so remove it from Unknown
        edge.remove()
      }

    }
  }

  def linkBenefit(header: SaleOrderHeaderVW)(implicit g: OrientGraph): (SaleOrderHeaderVW, BzNodeVW) = {
    BzNetDML.findBzNode(header.member_code) match {
      case Some(vwNode) =>
        header.v.addEdge(e.Benefit, vwNode.v)
        (header, vwNode)
      case None =>
        linkOrphanBenefit(header)
    }
  }

}
