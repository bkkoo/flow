package net.shantitree.flow.sys.lib.orient.model

import net.shantitree.flow.sys.graph.GraphSession
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}

object ModelDefToOClassUtil {
  implicit def toOClassUtil[M<:Model](o: ModelDef[M]) = new OClassUtil(GraphSession.factory)(o)
}
