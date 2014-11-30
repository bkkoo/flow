package net.shantitree.flow.sys.lib.orient.graph

import com.tinkerpop.blueprints.{Edge, Direction, Vertex}
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import net.shantitree.flow.sys.lib.lang.JIterableMapper
import net.shantitree.flow.sys.lib.model.conversion.ModelCtorArgs._
import net.shantitree.flow.sys.lib.model.{VWrap, TVertexWrapper, Model, ModelDef}
import scala.collection.JavaConverters._
import scala.language.implicitConversions
import java.lang.{Iterable => JIterable}

object VertexHelper {
  implicit def toVertexHelper[V <: Vertex](v: V): VertexHelper[V] = new VertexHelper(v)
}

class VertexHelper[V <: Vertex](v: V) {
  
  protected def dataValOf[M <: Model](model: M)(implicit modelDef: ModelDef[M]) = {
    modelDef.reflector.dataValOf(model)
  }

  def toModel[M <: Model](implicit modelDef: ModelDef[M]) = {
    from(v.asInstanceOf[Vertex] , modelDef).createModel()
  }
  
  lazy val orientV:OrientVertex = v.asInstanceOf[OrientVertex]
  lazy val record = orientV.getRecord
  lazy val schemaClass = record.getSchemaClass
  lazy val oClassName =  record.getClassName
  lazy val declaredProperties = schemaClass.declaredProperties.asScala
  lazy val declaredPropertyKeys = declaredProperties.map(_.getName).toSet
  
  def getPropertyKeys = { v.getPropertyKeys.asScala.toSet[String] }

  def setPropertiesFrom[M <: Model](model: M)(implicit modelDef: ModelDef[M]):V  = {
    setPropertiesByItrPair {
      if (oClassName == modelDef.odef.oClassName) { 
        dataValOf(model) 
      } else {
        dataValOf(model).filter{ p=>declaredPropertyKeys(p._1)}
      } 
    }
  }

  def wrp[W](wrpFn: Vertex => W): W = wrpFn(v)

  def in1(eLabel: String): Option[Vertex] = {
    v1(Direction.IN, eLabel)
  }

  def in1[M <: Model ,VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW])(eLabel: String): Option[VW] = {
    v1(Direction.IN, eLabel).map(wrapper)
  }

  def out1(eLabel: String): Option[Vertex] = {
    v1(Direction.OUT, eLabel)
  }

  def out1[M <: Model ,VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW])(eLabel: String): Option[VW] = {
    v1(Direction.OUT, eLabel).map(wrapper)
  }

  def v1[M <: Model ,VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW])(direction: Direction, eLabel:String ): Option[VW] = {
    v1(direction, eLabel).map(wrapper)
  }

  def v1(direction: Direction, eLabel:String ): Option[Vertex] = {
    val itr = v.getVertices(direction, eLabel).iterator
    if (itr.hasNext) { Some(itr.next()) } else { None }
  }

  def vertices(direction: Direction, eLabel:String ): JIterable[Vertex] = {
    v.getVertices(direction, eLabel)
  }

  def vertices[M <: Model ,VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW])(direction: Direction, eLabel:String ): JIterableMapper[Vertex, VW] = {
    val iterable = v.getVertices(direction, eLabel)
    JIterableMapper(iterable)(wrapper)
  }

  def out(eLabel: String): JIterable[Vertex] = {
    v.getVertices(Direction.OUT, eLabel)
  }

  def out[M <: Model ,VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW])(eLabel: String): JIterableMapper[Vertex, VW] = {
    val iterable = v.getVertices(Direction.OUT, eLabel)
    JIterableMapper(iterable)(wrapper)
  }

  def in(eLabel: String): JIterable[Vertex] = {
    v.getVertices(Direction.IN, eLabel)
  }

  def in[M <: Model ,VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW])(eLabel: String): JIterableMapper[Vertex, VW] = {
    val iterable = v.getVertices(Direction.IN, eLabel)
    JIterableMapper(iterable)(wrapper)
  }

  def updateFrom[M <: Model](model: M)(implicit modelDef: ModelDef[M]):V  = {
    val propertyKeys = getPropertyKeys
    setPropertiesByItrPair {
      dataValOf(model).filter{ p=>propertyKeys(p._1) }
    }
  }
  
  def mergeFrom[M <: Model](model: M)(implicit modelDef: ModelDef[M]):V  = {
    setPropertiesByItrPair(dataValOf(model))
  }

  def setPropertiesByItrPair(pairs: Iterable[(String, Any)]): V = {
    val (notNullPairs, nullPairs) = pairs.partition {
      case (_, null) => false
      case (_, None) => false
      case _ => true
    }
    orientV.setProperties[OrientVertex](notNullPairs.toMap.asJava)
    nullPairs.foreach { p => orientV.getRecord.field(p._1, null:Any)  }
    orientV.save()
    v
  }

  def setPropertiesByPairs(pairs: (String, Any)*): V = {
    setPropertiesByItrPair(pairs)
  }

  def edges(direction: Direction, labels: String*): Iterable[Edge] = {
    v.getEdges(direction, labels:_*).asScala
  }

  def findEdge(direction: Direction, label: String): Option[Edge] = {
    v.getEdges(direction, label).asScala.toList match {
      case Nil => None
      case e::es => Some(e)
    }
  }

  def removeEdges(direction: Direction, labels: String*): Unit = {
    v.getEdges(direction, labels:_*).asScala.foreach { e => e.remove() }
  }

  def removeInEdges(labels: String*): Unit = {
    removeEdges(Direction.IN, labels:_*)
  }

  def removeOutEdges(labels: String*): Unit = {
    removeEdges(Direction.OUT, labels:_*)
  }

  def setNull(propertyName: String): Unit = {
    v match {
      case ov:OrientVertex =>
        ov.getRecord.field(propertyName, null:Any)
      case _ =>
        throw new NullPointerException("Can't set vertex property to null if vertex is not type of OrientVertex!")
    }
  }

}
