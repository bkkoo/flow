package net.shantitree.flow.base.bznet.sys

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.constant.MemberPositionConst
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.base.partner.model.{Partner, PartnerField, PartnerVW}
import net.shantitree.flow.sys.lib.DML
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._

object MemberDML extends DML[Partner] {
  val F = PartnerField

  def addNew(model: Partner)(implicit g: OrientGraph):(PartnerVW, BzNodeVW) = {
    val memberModel = model.copy(is_member = true)
    val vwPartner = g.addV(PartnerVW, memberModel)
    val vwBNode = BzNetDML.addNew(vwPartner)

    if (MemberPositionConst.isMemberPosition(model.position.get)) {
      BzNetDML.updatePosition(vwBNode, model.position.get)
    }

    vwPartner -> vwBNode
  }

  def addAndJoinNetwork(model: Partner)(implicit g: OrientGraph):(PartnerVW, BzNodeVW) = {
    val (vwPartner, vwNode) = addNew(model)
    BzNetDML.joinNetwork(vwNode)
    (vwPartner, vwNode)
  }

  def addNew(models: List[Partner])(implicit g: OrientGraph): List[(String, (PartnerVW, BzNodeVW))] = {

    val pointers = models.map { p => p.member_code.get -> addNew(p) }
    val idx = pointers.toMap

    pointers.map { case (member_code, (vwPartner, vwNode)) =>
      //finding sponsor using current new data as index
      idx.get(vwNode.sponsor_code) match {
        case Some((_, vwSponsorNode)) =>
          BzNetDML.joinNode(vwNode, vwSponsorNode)
        case None =>
          //join network
          BzNetDML.joinNetwork(vwNode)
      }

      (member_code, (vwPartner , vwNode))
    }
  }

}
