package net.shantitree.flow.base.saleorder.sys

import com.tinkerpop.blueprints.{Direction, Vertex}
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.biz.constant.EdgeLabelConst
import net.shantitree.flow.base.partner.sys.PartnerDML
import net.shantitree.flow.base.partner.model.PartnerVW
import net.shantitree.flow.base.saleorder.model.{SaleOrderItemField, SaleOrderHeaderField, SaleOrderHeaderVW}
import java.lang.{Iterable => JIterable}
import net.shantitree.flow.sys.biz.PV

import scala.collection.JavaConverters._

trait SaleOrderBaseLogic {

  protected val E = EdgeLabelConst
  protected val Fh = SaleOrderHeaderField
  protected val Fi = SaleOrderItemField

  def findBuyerByMemberCode(code: String)(implicit g: OrientGraph): Option[PartnerVW] = PartnerDML.findByMemberCode(code)
  def findBuyerByMemberCode(vwHeader: SaleOrderHeaderVW)(implicit g: OrientGraph): Option[PartnerVW] = PartnerDML.findByMemberCode(vwHeader.member_code)
  def findBuyer(vwHeader: SaleOrderHeaderVW)(implicit g: OrientGraph): Option[PartnerVW] = PartnerDML.findByCode(vwHeader.partner_code)
  def getItems(vwHeader: SaleOrderHeaderVW)(implicit g: OrientGraph): JIterable[Vertex] = {
    vwHeader.v.getVertices(Direction.OUT, E.Item)
  }

  def getPV(vwHeader: SaleOrderHeaderVW)(implicit g: OrientGraph): PV = {
    val itr = getItems(vwHeader).iterator().asScala
    PV.sum(itr)
  }

}
