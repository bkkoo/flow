
package net.shantitree.flow.sys.lib.model

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientVertex

import scala.collection.JavaConverters._
import java.lang.{Long => JLong}
import java.lang.{Double => JDouble}
import java.lang.{Float => JFloat}
import java.lang.{Boolean => JBoolean}
import java.util.Date
import java.math.{BigDecimal => JBigDecimal }

trait VWrap[M <: Model, VW <: TVertexWrapper[M]] extends (Vertex=>VW) {
  def apply(v: Vertex): VW
}

trait TVertexWrapper[M <: Model] extends TVertexWrapperBase
trait TVertexWrapperBase {

  val v: Vertex
  def rid = v.getId
  lazy val oV = v.asInstanceOf[OrientVertex]
  lazy val propertyKeys = v.getPropertyKeys.asScala.toSet

  def save() = oV.save()

  def get[T](name:String):T = v.getProperty[T](name)
  def set[T](name:String, value: T):Unit = {
    value match {
      case null =>
        oV.getRecord.field(name, null: Any)
        oV.save()
      case _ =>
        v.setProperty(name, value)
    }
  }

  def getString(name:String):String = v.getProperty[String](name)
  def getInt(name:String):Integer = v.getProperty[Integer](name)
  def getLong(name:String):JLong = v.getProperty[JLong](name)
  def getBoolean(name: String): JBoolean = v.getProperty[JBoolean](name)
  def getDouble(name: String): JDouble = v.getProperty[JDouble](name)
  def getFloat(name: String): JFloat = v.getProperty[JFloat](name)
  def getBigDecimal(name: String):BigDecimal = v.getProperty[JBigDecimal](name)
  def getDate(name: String):Date = v.getProperty[Date](name)

  def setString(name: String, value: String) = set[String](name, value)
  def setInt(name:String, value: Integer) = set[Integer](name, value)
  def setLong(name:String, value: JLong) = set[JLong](name, value)
  def setBoolean(name: String, value: JBoolean) = set[JBoolean](name, value)
  def setDouble(name: String, value: JDouble) = set[JDouble](name, value)
  def setFloat(name: String, value: JFloat) = set[JFloat](name, value)
  def setBigDecimal(name: String, value: BigDecimal) = set[JBigDecimal](name, value.bigDecimal)
  def setDate(name: String, value:Date) = set[Date](name, value)

  def setProperties(pairs: (String, Any)*): Unit =  {
    oV.setProperties[OrientVertex](pairs.toMap.asJava)
    oV.save()
  }
}
