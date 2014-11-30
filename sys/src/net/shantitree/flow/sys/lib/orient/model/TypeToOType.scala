package net.shantitree.flow.sys.lib.orient.model


import com.orientechnologies.orient.core.metadata.schema.OType
import scala.reflect.runtime.universe._
import definitions._
import net.shantitree.flow.sys.lib.lang.RuTypeDefinition._

object TypeToOType {
  type Converter = PartialFunction[(String, (Type, TermSymbol)), OType]

  def apply(customConverter:Converter) = new TypeToOType {
    val convert = customConverter orElse default
  }

  def apply() = new TypeToOType {
    val convert = default
  }
}

trait TypeToOType {

  import TypeToOType.Converter

  lazy val rtMirror = runtimeMirror(getClass.getClassLoader)

  val convert:Converter

  val default:Converter = {
    case (name, (sig, _)) if sig =:= IntTpe => getOType(classOf[Integer], name)
    case (name, (sig, _)) if sig <:< OptionTpe => getOTypeInBox(sig, name)
    case (name, (sig, _)) => getOType(sig, name)
  }


  def getOTypeInBox(boxTpe: Type, fieldName: String): OType = {
    val clazz = rtMirror.runtimeClass(boxTpe.typeArgs.head.typeSymbol.asClass)
    getOType(clazz, fieldName)
  }

  def getOType(tpe: Type , fieldName: String): OType = {
    val clazz = rtMirror.runtimeClass(tpe.typeSymbol.asClass)
    getOType(clazz, fieldName)
  }

  def getOType(clazz: Class[_] , fieldName: String=""): OType = {
    OType.getTypeByClass(clazz) match {
      case null =>
        throw new RuntimeException(s"Can't find 'OType' for '${clazz.getName}' of field '$fieldName'")
      case t => t
    }
  }

}
