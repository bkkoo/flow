package net.shantitree.flow.sys

import net.shantitree.flow.sys.graph.GraphSession
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}
import net.shantitree.flow.sys.lib.orient.model.OClassUtil

object ModelDefToOClassUtil {
  implicit def toOClassUtil[M<:Model](o: ModelDef[M]) = new OClassUtil(GraphSession.factory)(o)
}
