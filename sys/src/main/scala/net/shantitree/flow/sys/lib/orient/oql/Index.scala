package net.shantitree.flow.sys.lib.orient.oql

import net.shantitree.flow.sys.lib.model.{Model, ModelDef}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil
import IndexUtil._

case class Index[M <: Model](key: String*)(implicit modelDef: ModelDef[M]) {
  
  override def toString = {
    val idxName = idxNameFor(key:_*)(modelDef)
    s"index:$idxName"
  }

}
