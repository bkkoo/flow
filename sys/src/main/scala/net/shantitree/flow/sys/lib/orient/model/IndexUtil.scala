package net.shantitree.flow.sys.lib.orient.model

import com.orientechnologies.common.listener.OProgressListener
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE
import com.orientechnologies.orient.core.record.impl.ODocument
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}

object IndexUtil {
  protected val idxSep = "__"

  def apply[M <: Model](oClassUtil: OClassUtil[M])(implicit modelDef: ModelDef[M]) = new IndexUtil[M](oClassUtil)
  def idxNameFor[M <: Model](key: String*)(implicit modelDef: ModelDef[M]) = modelDef.odef.oClassName + s".${key.mkString(idxSep)}"
  def idxNameFor(oClassName: String, key: String*) = oClassName + s".${key.mkString(idxSep)}"

}

class IndexUtil[M <: Model](oClassUtil: OClassUtil[M])(implicit modelDef: ModelDef[M]) {

  lazy val oClass = oClassUtil.oClass

  protected def fullIdxName(iName:String) = modelDef.odef.oClassName + "." + iName


  def create(iName: String, iType: INDEX_TYPE, fields: String*) = {
    if (!isIndexExist(iName)) oClass.createIndex(fullIdxName(iName), iType, fields:_*)
  }

  def create(iName: String, iType: String, fields: String*) = {
    if (!isIndexExist(iName)) oClass.createIndex(fullIdxName(iName), iType, fields:_*)
  }

  def create(iName:String, iType:INDEX_TYPE, iProgressListener:OProgressListener, fields: String*) = {
    if (!isIndexExist(iName)) oClass.createIndex(fullIdxName(iName), iType, iProgressListener, fields:_*)
  }

  def create(iName:String, iType:String, iProgressListener:OProgressListener, metaData: ODocument, fields: String*) = {
    if (!isIndexExist(iName)) oClass.createIndex(fullIdxName(iName), iType, iProgressListener, metaData, fields:_*)
  }

  def create(iName:String, iType:String, iProgressListener:OProgressListener, metaData: ODocument, algorithm: String, fields: String*) = {
    if (!isIndexExist(iName)) oClass.createIndex(fullIdxName(iName), iType, iProgressListener, metaData, algorithm, fields:_*)
  }

  def isIndexExist(iName:String) = {
    oClass.getClassIndex(fullIdxName(iName)) != null
  }

  def indexForeach(iType:INDEX_TYPE, fields: String*)  = {
    fields map { f=> create(f, iType, f)}
  }

  def indexFor(iType:INDEX_TYPE, field: String) = {
    create(field, iType, field)
  }
  
  def compose(iType: INDEX_TYPE, fields: String*) = {
    val iName = fields.mkString(IndexUtil.idxSep)
    create(iName, iType, fields:_*)
  }

}
