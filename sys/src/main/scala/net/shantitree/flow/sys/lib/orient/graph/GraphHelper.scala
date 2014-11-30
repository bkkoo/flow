package net.shantitree.flow.sys.lib.orient.graph

import java.lang.{Iterable => JIterable}
import com.orientechnologies.orient.core.sql.OCommandSQL
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.{OrientVertex, OrientBaseGraph}
import net.shantitree.flow.sys.lib.lang.JIterableMapper
import net.shantitree.flow.sys.lib.model.{VWrap, TVertexWrapper, Model, ModelDef}
import VertexHelper._
import net.shantitree.flow.sys.lib.orient.oql.{Projection, Criteria}

object GraphHelper {
  implicit def toGraphHelper[G <: OrientBaseGraph](g: G) = new GraphHelper(g)
}
class GraphHelper[G <: OrientBaseGraph](g: G) {

  def vertices(oClassName: String, criteria: (String, AnyRef)*): JIterable[Vertex] = {
    criteria.toList match {
      case (propertyName, value)::Nil =>
        g.getVertices(s"$oClassName.$propertyName", value)
      case xs =>
        val where = xs.unzip
        g.getVertices(oClassName, where._1.toArray, where._2.toArray)
    }
  }

  def vertices[M <: Model](criteria: (String, AnyRef)*)(implicit md: ModelDef[M]): JIterable[Vertex] = {
    vertices(md.odef.oClassName, criteria:_*)
  }

  def vertices(oClassName: String): JIterable[Vertex] = {
    g.getVerticesOfClass(oClassName)
  }

  def vertices[M <: Model](implicit md: ModelDef[M]): JIterable[Vertex] = {
    vertices(md.odef.oClassName)
  }

  def vertices[M <: Model, VW <: TVertexWrapper[M]](wrapper: VWrap[M, VW])(implicit md: ModelDef[M]): JIterableMapper[Vertex, VW] = {
    val iterable = vertices(md.odef.oClassName)
    JIterableMapper(iterable)(wrapper)
  }

  def vertices[M <: Model, VW <: TVertexWrapper[M]](wrapper: VWrap[M, VW], criteria: (String, AnyRef)*)(implicit md: ModelDef[M]): JIterableMapper[Vertex, VW] = {
    val iterable = vertices(md.odef.oClassName, criteria:_*)
    JIterableMapper(iterable)(wrapper)
  }

  def findV[M <: Model, VW <: TVertexWrapper[M]](wrapper: VWrap[M, VW])(criteria: (String, AnyRef)*)(implicit md: ModelDef[M]): Option[VW] = {
    findV[M](criteria:_*).map(wrapper)
  }

  def findV[M <: Model](criteria: (String, AnyRef)*)(implicit md: ModelDef[M]): Option[Vertex] = {
    val itr = vertices(md.odef.oClassName, criteria:_*).iterator
    if (itr.hasNext) {
      Some(itr.next())
    } else {
      None
    }
  }

  def saveAsV[M <: Model](model: M)(implicit md: ModelDef[M]): Vertex = {
    model.rid
      .map {id => getV(id).getOrElse { addV(model) } }
      .getOrElse {addV(model) }
  }

  def getV[VW <: TVertexWrapper[_<: Model]](wrapper: VWrap[_<: Model, VW])(rid: AnyRef): Option[VW] = {
    getV(rid).map(wrapper)
  }

  def getV(rid: AnyRef): Option[Vertex] = {
    val v = g.getVertex(rid)
    if (v == null) {
      None
    } else {
      Some(v)
    }
  }

  def qryV[M <: Model](criteria: Criteria)(implicit md: ModelDef[M]): JIterable[OrientVertex] = {
    val oql = s"select from ${md.odef.oClassName} where ${criteria.clause}"
    exec[JIterable[OrientVertex]](oql)
  }

  def qryV[M <: Model](projections: Projection*)(criteria: Criteria)(implicit md: ModelDef[M]): JIterable[OrientVertex] = {
    val oql = s"select ${projections.map(_.str).mkString(",")} from ${md.odef.oClassName} where ${criteria.clause}"
    exec[JIterable[OrientVertex]](oql)
  }
  def qryV[M <: Model](projections: String, criteria: Criteria)(implicit md: ModelDef[M]): JIterable[OrientVertex] = {
    val oql = s"select ${projections.mkString(",")} from ${md.odef.oClassName} where ${criteria.clause}"
    exec[JIterable[OrientVertex]](oql)
  }

  def qryV[M <: Model, VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW] )(criteria: Criteria)(implicit md: ModelDef[M]): JIterableMapper[OrientVertex, VW] = {
    val iterable = qryV[M](criteria)
    JIterableMapper(iterable)(wrapper)
  }

  def qryV(oql:String): JIterable[OrientVertex] = {
    exec[JIterable[OrientVertex]](oql)
  }

  def delete[M <: Model](criteria: Criteria)(implicit md:ModelDef[M]): Integer = {
    delete[M](Some(criteria))
  }

  def delete[M <: Model]()(implicit md:ModelDef[M]): Integer = {
    delete[M](None)
  }

  def delete[M <: Model](criteria: Option[Criteria])(implicit md:ModelDef[M]): Integer = {
    val oql = s"delete from ${md.odef.oClassName}" + criteria.map { c => s" where ${c.clause}" } .getOrElse("")
    val result = exec[JIterable[OrientVertex]](oql).iterator()
    if (result.hasNext) { result.next().getProperty[Integer]("value") } else { 0 }
  }

  def exec[RET](cmdString: String): RET = {
    g.command(new OCommandSQL(cmdString)).execute[RET]()
  }

  def addV[M <: Model](model: M)(implicit md: ModelDef[M]): OrientVertex = {
    val oClassName = md.odef.oClassName
    val v = g.addVertex(oClassName, null)
    v.mergeFrom(model)
    v.save()
    v
  }

  def addV[M <: Model](implicit md: ModelDef[M]):OrientVertex = {
    g.addVertex(md.odef.oClassName, null)
  }

  def addV[M <: Model, VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW] )(implicit md: ModelDef[M]): VW = {
    wrapper(addV[M])
  }

  def addV[M <: Model, VW <: TVertexWrapper[M]](wrapper: VWrap[M,VW], model: M)(implicit md: ModelDef[M]): VW = {
    wrapper(addV(model))
  }
}
