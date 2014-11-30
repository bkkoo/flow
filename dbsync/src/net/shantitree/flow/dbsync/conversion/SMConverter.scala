package net.shantitree.flow.dbsync.conversion

import net.shantitree.flow.sys.lib.model.{ModelDef, Model}

import net.shantitree.flow.sys.lib.model.conversion.ModelCtorArgs._
import net.shantitree.flow.sys.lib.lang.ProductConverter._

object SMConverter {
  def apply[M <: Model](modelDef: ModelDef[M]): TSMConverter[M] = new SMConverter[M](modelDef)
  def apply[M <: Model](modelDef: ModelDef[M], normalizer: DataNormalizer): TSMConverter[M] = new SMConverterWithNormalizer[M](normalizer, modelDef)
}

trait TSMConverter[M <: Model] extends TModelConverter[M] {
  val modelDef: ModelDef[M]
  def convertPRow(pRow: Product): M = from(pRow, modelDef).createModel()
}
class SMConverter[M <: Model](val modelDef: ModelDef[M]) extends TSMConverter[M]
class SMConverterWithNormalizer[M <: Model](val normalizer: DataNormalizer, val modelDef: ModelDef[M]) extends TSMConverter[M] {
  override def convertPRow(pRow: Product):M =  {
    val mRow = pRow.toMap
    from(normalizer.apply(mRow), modelDef, mRow).createModel()
  }
}

