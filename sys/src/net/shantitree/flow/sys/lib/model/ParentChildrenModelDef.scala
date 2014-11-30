package net.shantitree.flow.sys.lib.model

import net.shantitree.flow.sys.lib.model.conversion.PureModelArgValConverter

trait ParentChildrenModelDef[M <: ParentChildrenModel[_<: Model, _<: Model]] extends TModelDef[M] {

  implicit val parentChildrenModelDef = this

  val masterKey: String
  val foreignKey: String
  val parentFieldName: String
  val childrenFieldName: String

  override val argValConverter = PureModelArgValConverter

}
