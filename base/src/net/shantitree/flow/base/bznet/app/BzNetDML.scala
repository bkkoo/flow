package net.shantitree.flow.base.bznet.app

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.constant.{BNodeCodeConst, MemberPositionConst}
import net.shantitree.flow.base.bznet.model.{BzNode, BzNodeField, BzNodeVW}
import net.shantitree.flow.biz.lib.DML
import net.shantitree.flow.sys.constant.EdgeLabelConst
import net.shantitree.flow.base.partner.model.PartnerVW
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import net.shantitree.flow.sys.lib.orient.graph.VertexHelper._

import scala.util.{Failure, Success, Try}

object BzNetDML extends DML[BzNode] {

  val F = BzNodeField
  val E = EdgeLabelConst
  val Pos = MemberPositionConst
  val NodeCode = BNodeCodeConst

  def addNew(partner: PartnerVW)(implicit g: OrientGraph): BzNodeVW  = {
    val vPartner = partner.v
    val vwNode = g.findV(BzNodeVW)(F.member_code -> partner.member_code).getOrElse {
      addNew(vPartner.toModel[BzNode])
    }

    vPartner.addEdge(E.Owns, vwNode.v)
    vwNode
  }

  def addNew(model: BzNode)(implicit g: OrientGraph):BzNodeVW = g.addV(BzNodeVW, model)
  def addAndJoinNetwork(model: BzNode)(implicit g: OrientGraph):(BzNodeVW, BzNodeVW) = joinNetwork(addNew(model))
  def joinNetwork(vwNode: BzNodeVW)(implicit g: OrientGraph): (BzNodeVW, BzNodeVW) = {
    findSponsor(vwNode) match {
      case Some(vwSponsor) => joinNode(vwNode, vwSponsor)
      case None => adoptOrphan(vwNode)
    }
  }
  def joinNode(vwSponseeNode: BzNodeVW, vwSponsorNode: BzNodeVW)(implicit g: OrientGraph): (BzNodeVW, BzNodeVW) = {
    vwSponsorNode.v.addEdge(E.Branch, vwSponseeNode.v)
    vwSponseeNode -> vwSponsorNode
  }

  def adoptOrphan(vwOrphanNode: BzNodeVW)(implicit g: OrientGraph):(BzNodeVW, BzNodeVW) = SeedNode.adoptOrphan(vwOrphanNode)
  def findAdoptedOrphans()(implicit g: OrientGraph): Iterable[Vertex] =  SeedNode.getAdoptedOrphans()
  def findSponsor(vwSponsee: BzNodeVW)(implicit g: OrientGraph): Option[BzNodeVW] = findSponsor(vwSponsee.sponsor_code)
  def findSponsor(sponsorCode: String)(implicit g: OrientGraph): Option[BzNodeVW] = g.findV(BzNodeVW)(F.member_code -> sponsorCode)
  def sponsorAllOrphans()(implicit g: OrientGraph): Iterable[Try[(BzNodeVW, BzNodeVW)]] =  sponsorOrphans(findAdoptedOrphans())
  def sponsorOrphans(vertices: Iterable[Vertex])(implicit g: OrientGraph): Iterable[Try[(BzNodeVW, BzNodeVW)]] = {
    vertices.map { v => sponsorOrphan(BzNodeVW(v))}
  }

  def sponsorOrphan(vwOrphanNode: BzNodeVW)(implicit g: OrientGraph): Try[(BzNodeVW, BzNodeVW)] = {
    findSponsor(vwOrphanNode) match {
      case Some(vwSponsor) =>
        joinNode(vwOrphanNode, vwSponsor)
        SeedNode.unAdoptOrphans(vwOrphanNode)
        Success((vwOrphanNode, vwSponsor))
      case None =>
        Failure(new RuntimeException(
          s"Can't find sponsor for orphan node '${vwOrphanNode.rid}'" +
            s" with member_code = '${vwOrphanNode.member_code}' and sponsor_code = '${vwOrphanNode.sponsor_code}'"
        ))
    }
  }

  def finalizeOrphans()(implicit g: OrientGraph): Unit = {
    val vwOrphans = findAdoptedOrphans().map(BzNodeVW)
    val idx = vwOrphans
      .map(_.sponsor_code)
      .toSet[String]
      .map { code => findSponsor(code) match {
        case Some(vw) =>
          code -> vw
        case None =>
          val vw = addNew(BzNode( member_code = code, sponsor_code = NodeCode.SeedCode, position = Pos.UnknownPos ))
          joinNode(vw, SeedNode.get())
          code -> vw
      }}
      .toMap

    vwOrphans.foreach { orphan =>
      val sponsor = idx.get(orphan.sponsor_code).get
      joinNode(orphan, sponsor)
      SeedNode.unAdoptOrphans(orphan)
    }
  }

  def getBzNode(code: String)(implicit g: OrientGraph): BzNodeVW = {
    findBzNode(code).getOrElse {
      throw new BzNodeNotFoundException(s"Can't find bzNode '$code'!")
    }
  }

  def findBzNode(code: String)(implicit g: OrientGraph): Option[BzNodeVW] = g.findV[BzNode](F.member_code -> code).map(BzNodeVW)

  def updatePosition(vwBNode: BzNodeVW, position: Int)(implicit g: OrientGraph): BzNodeVW = {
    require(MemberPositionConst.isPosition(position))
    vwBNode.set_position(position)
    vwBNode
  }

}
