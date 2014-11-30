package net.shantitree.flow.sys.lib.model

import net.shantitree.flow.sys.lib.orient.model.{TypeToOType, OrientDef, IndexUtil}

trait ModelDef[M<: Model] extends TModelDef[M] {

  implicit val singleModelDef = this

  val typeToOType = TypeToOType()
  val odef = OrientDef(ctag.runtimeClass.getSimpleName)

  def createIndexes(indexUtil: IndexUtil[M]) {}

}
