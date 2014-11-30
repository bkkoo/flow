package net.shantitree.flow.sys.lib.model

import net.shantitree.flow.sys.lib.lang.TagInfo
import net.shantitree.flow.sys.lib.model.conversion._

trait TModelDef[M <: TModel] extends TagInfo[M]{

  implicit val modelDef = this
  protected lazy val dataFieldFilter:DataFieldFilter = DefaultDataFieldFilter
  protected lazy val dataValConverter:DataValConverter = DefaultDataValConverter
  lazy val reflector:ModelReflector[M] = ModelReflector(this, dataValConverter, dataFieldFilter)
  val argValConverter:ModelArgValConverter = DefaultModelArgValConverter

}
