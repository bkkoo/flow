package net.shantitree.flow.sys.lib.model.conversion

import com.tinkerpop.blueprints.Vertex
import net.shantitree.flow.sys.lib.model.{TModel, TModelDef}

object TGetArgValByName {
  
  type AltGetter = TGetArgValByName[_]
  type OptAltGetter = Option[AltGetter]
  type Getter[T] = TGetArgValByName[T]
  type GetGetter[T] = (T, OptAltGetter)=>TGetArgValByName[T]

  implicit def g1[M <: TModel](model:M, alt:OptAltGetter)(implicit modelDef: TModelDef[M]): Getter[M] = {
    val mirrors = modelDef.reflector.fieldMirrorsOf(model).toMap
    GetArgValByName(model, alt){ name =>
      mirrors.get(name).map { case (fieldMirror, _) => fieldMirror.get }
    }
  }

  import scala.collection.JavaConverters._

  implicit def g2(v: Vertex, alt: OptAltGetter): Getter[Vertex] = {

    lazy val properties = v.getPropertyKeys.asScala.toSet

    GetArgValByName(v, alt) {
      case "rid" => Some(v.getId)
      case name =>
        v.getProperty[Any](name) match {
          case null if !properties(name) => None
          case value => Some(value)
        }
    }
  }

  implicit def g3(m: Map[String, Any], alt: OptAltGetter): Getter[Map[String, Any]] = {
    GetArgValByName(m, alt){ name => m.get(name) }
  }

  import net.shantitree.flow.sys.lib.lang.ProductConverter._

  implicit def g4(p:Product, alt: OptAltGetter): Getter[Product] = {
    val obj = toMap(p)
    GetArgValByName(p, alt){ name => obj.get(name) }
  }


}

trait TGetArgValByName[T] {
  val obj:T
  val get:(String)=>Option[Any]
  def apply(name: String): Any 
}







