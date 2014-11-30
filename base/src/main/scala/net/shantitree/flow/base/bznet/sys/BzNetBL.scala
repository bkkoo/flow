package net.shantitree.flow.base.bznet.sys

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.base.biz.constant.EdgeLabelConst
import net.shantitree.flow.sys.lib.orient.graph.VertexHelper._
import net.shantitree.flow.sys.graph.GraphSession

object BzNetBL {
  val E = EdgeLabelConst
  val session = GraphSession

  def getUplines(vDownline: Vertex)(implicit g: OrientGraph): Iterator[Vertex] = {
    new UplineIterator(vDownline)
  }

  def getUplines(vwDownline: BzNodeVW)(implicit g:OrientGraph):Iterator[BzNodeVW] = {
    getUplines(vwDownline.v).map(BzNodeVW)
  }

  def getChain(vCurNode: Vertex)(implicit g: OrientGraph): Iterator[Vertex] = {
    new ChainIterator(vCurNode)
  }

  def getChain(vwCurNode: BzNodeVW)(implicit g: OrientGraph): Iterator[BzNodeVW] = {
    getChain(vwCurNode.v).map(BzNodeVW)
  }

  def findSponsor(vBzNode: Vertex)(implicit g: OrientGraph):Option[Vertex] = {
    vBzNode.in1(E.Branch)
  }

}
