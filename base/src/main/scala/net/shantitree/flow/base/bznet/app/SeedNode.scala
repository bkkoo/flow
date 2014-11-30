package net.shantitree.flow.base.bznet.app

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.constant.{BNodeCodeConst, MemberPositionConst}
import net.shantitree.flow.base.bznet.model.{BzNode, BzNodeField, BzNodeVW}
import net.shantitree.flow.sys.constant.EdgeLabelConst
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import net.shantitree.flow.sys.lib.orient.graph.VertexHelper._
import BNodeCodeConst._
import MemberPositionConst._
import org.joda.time.DateTime

import scala.collection.JavaConverters._

object SeedNode {

  private val F = BzNodeField
  private val E = EdgeLabelConst

  private def vwSeedNode(implicit g: OrientGraph) = {
    getV() match {
      case Some(v) => BzNodeVW(v)
      case None => throw new RuntimeException("Seed node not found!")
    }
  }

  def get()(implicit g: OrientGraph) = vwSeedNode

  def adoptOrphan(vwOrphanNode: BzNodeVW)(implicit g: OrientGraph):(BzNodeVW, BzNodeVW) = {
    vwSeedNode.v.addEdge(E.Adopts, vwOrphanNode.v)
    vwOrphanNode -> vwSeedNode
  }

  def getAdoptedOrphans()(implicit g: OrientGraph): Iterable[Vertex] = {
    vwSeedNode.v.out(E.Adopts).asScala
  }

  def getV()(implicit g: OrientGraph) = g.findV[BzNode](F.member_code->SeedCode)

  def init()(implicit g: OrientGraph) = {
    getV() match {
      case None =>
        g.addV(BzNode(
          rid = None,
          member_code = SeedCode,
          sponsor_code = SeedCode,
          position = SeedPos,
          created_at = new DateTime("1975-08-26T05:30:00").toDate
        ))
      case Some(_) =>
    }
  }


  def unAdoptOrphans(vwOrphanNode: BzNodeVW)(implicit g: OrientGraph): BzNodeVW = {
    vwOrphanNode.v.removeInEdges(E.Adopts)
    vwOrphanNode
  }

}
