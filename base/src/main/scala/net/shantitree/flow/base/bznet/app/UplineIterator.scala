package net.shantitree.flow.base.bznet.app

import com.tinkerpop.blueprints.Vertex
import net.shantitree.flow.sys.constant.EdgeLabelConst
import net.shantitree.flow.sys.lib.orient.graph.VertexHelper._

class UplineIterator(val vDownline: Vertex) extends Iterator[Vertex] {
  val E = EdgeLabelConst

  var curNode: Vertex = vDownline
  var cacheUpNode: Option[Vertex] = None

  def hasNext = {
    cacheUpNode = findSponsor(curNode)
    cacheUpNode.nonEmpty
  }

  def next() = {
    curNode = cacheUpNode.getOrElse {
      findSponsor(curNode).getOrElse { throw new NoSuchElementException("next on empty iterator!") }
    }
    cacheUpNode = None
    curNode
  }

  def findSponsor(vBzNode: Vertex):Option[Vertex] = {
    vBzNode.in1(E.Branch)
  }

}
