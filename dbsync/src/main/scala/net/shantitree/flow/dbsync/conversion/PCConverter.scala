package net.shantitree.flow.dbsync.conversion

import net.shantitree.flow.sys.lib.model.{ParentChildrenModelDef, ModelDef, Model, ParentChildrenModel}
import net.shantitree.flow.sys.lib.model.conversion.ModelCtorArgs._

object PCConverter {
  def apply[PM<: Model, CM <:Model, PC<: ParentChildrenModel[PM, CM]](modelDef: ParentChildrenModelDef[PC], parentModelDef: ModelDef[PM], childModelDef: ModelDef[CM]) = {
    new PCConverter(modelDef, parentModelDef, childModelDef)
  }

  def apply[PM<: Model, CM <:Model, PC<: ParentChildrenModel[PM, CM]](modelDef: ParentChildrenModelDef[PC], parentModelDef: ModelDef[PM], childModelDef: ModelDef[CM], normalizer: CompoundDataNormalizer) = {
    new PCConverterWithNormalizer(modelDef, parentModelDef, childModelDef, normalizer)
  }
}

class PCConverter[PM<: Model, CM <:Model, PC<: ParentChildrenModel[PM, CM]]
(val modelDef: ParentChildrenModelDef[PC], val parentModelDef: ModelDef[PM], val childModelDef: ModelDef[CM])
  extends TPCConverter[PM, CM, PC]{
  protected def toModel[M <: Model](modelName: String, mRow: Map[String, Any], modelDef: ModelDef[M]): M = {
    from(mRow, modelDef).createModel()
  }
}

class PCConverterWithNormalizer[PM<: Model, CM <:Model, PC<: ParentChildrenModel[PM, CM]]
(val modelDef: ParentChildrenModelDef[PC], val parentModelDef: ModelDef[PM], val childModelDef: ModelDef[CM], val normalizer: CompoundDataNormalizer)
  extends TPCConverter[PM, CM, PC]{
  protected def toModel[M <: Model](modelName: String, mRow: Map[String, Any], modelDef: ModelDef[M]): M = {
    val normalizedMRow = normalizer.apply(modelName, mRow)
    if (normalizedMRow == mRow) {
      from(mRow, modelDef).createModel()
    } else {
      from(normalizedMRow, modelDef, mRow).createModel()
    }
  }
}


trait TPCConverter[PM<: Model, CM <:Model, PC<: ParentChildrenModel[PM, CM]] extends TModelConverter[PC]{

  val modelDef: ParentChildrenModelDef[PC]
  val parentModelDef: ModelDef[PM]
  val childModelDef: ModelDef[CM]

  protected def toModel[M <: Model](modelName:String, mRow: Map[String, Any], modelDef: ModelDef[M]):M

  def convertPRow(row:Product) = {
    val parentFieldName = modelDef.parentFieldName
    val childrenFieldName = modelDef.childrenFieldName
    require(row.productArity == 2)
    val parent = try {
      row.productElement(0).asInstanceOf[Map[String, Any]]
    }
    catch {
      case e: Exception =>
        throw new RuntimeException(
          s"Following exception has occurred '${e.getMessage}' during conversion from `parent` value to Map[String, Any] in ParentChildrenJob")
    }
    val children = try {
      row.productElement(1).asInstanceOf[List[Map[String, Any]]]
    } catch {
      case e: Exception =>
        throw new RuntimeException(
          s"Following exception has occurred '${e.getMessage}' during conversion from `children` value to List[Map[String, Any]] in ParentChildrenJob")
    }
    val parentModel = toModel("parent", parent, parentModelDef)
    val childrenModel = children.map { child => toModel("child", child, childModelDef) }
    from(Map[String, Any](parentFieldName->parentModel, childrenFieldName->childrenModel), modelDef).createModel()
  }
}
