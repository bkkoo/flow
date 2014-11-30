package net.shantitree.flow.base.bznet.sys

import com.tinkerpop.blueprints.Vertex

class ChainIterator(val vCurNode: Vertex) extends UplineIterator(vCurNode) {
  var isInitial = true
  override def hasNext = if (isInitial) { true } else { super.hasNext }
  override def next() = if (isInitial) { isInitial=false; vCurNode } else { super.next() }
}
