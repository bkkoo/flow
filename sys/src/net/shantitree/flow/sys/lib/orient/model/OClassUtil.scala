package net.shantitree.flow.sys.lib.orient.model

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}

import scala.collection.JavaConverters._

class OClassUtil[M <: Model](graphFactory: OrientGraphFactory)(implicit modelDef: ModelDef[M]) {

  val odef = modelDef.odef

  lazy val oClass = create()
  lazy val schema = graphFactory.getDatabase.getMetadata.getSchema
  lazy val properties = oClass.declaredProperties.asScala
  lazy val dataFieldsSigTerm = modelDef.reflector.dataFieldsSigTerm
  lazy val typeToOType = modelDef.typeToOType

  def create() = {
    val oSuperClass =  odef.oSuperClassName match {
      case Some(n) => schema.getClass(n)
      case None => schema.getClass("V")
    }
    val oClass = schema.getOrCreateClass(odef.oClassName, oSuperClass)

    /*
      Updates generates "holes" at Storage level because rarely the new record
      fits perfectly the size of the previous one. Holes are free spaces between data.
      Holes are recycled but an excessive number of small holes it's the same as having
      a highly defraged File System: space is wasted (because small holes can't be easily recycled)
      and performance degrades when the database growth.
      ** Oversize **
      If you know you will update certain type of records, create a class for them
      and set the Oversize (default is 0) to 2 or more.
     */

    oClass.setOverSize(2)
    /*--------------------------------------------------*/
    oClass

  }


  def createProperties() = {

    val exist = properties.map(_.getName).toSet

    dataFieldsSigTerm
      .filter { case (name, _) => !exist(name) }
      .foreach { case arg@(name, (sig, _)) =>
        oClass.createProperty(name, typeToOType.convert(arg))
      }
  }

  def init() = {
    create()
    createProperties()
    modelDef.createIndexes(IndexUtil(this))
  }
}
