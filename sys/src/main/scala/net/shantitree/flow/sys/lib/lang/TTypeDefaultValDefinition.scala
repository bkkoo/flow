package net.shantitree.flow.sys.lib.lang

import scala.reflect.runtime.universe._
import scala.reflect.runtime.universe.definitions._

trait TTypeDefaultValDefinition {

  def defaultValueDefinition:Set[(Type, Any)]

  def primitiveDefaultValueDefinition:Set[(Type, Any)] = Set(
    DoubleTpe -> 0.0,
    FloatTpe -> 0,
    LongTpe -> 0,
    ShortTpe -> 0,
    IntTpe -> 0,
    ByteTpe -> 0,
    BooleanTpe -> false
  )

  lazy val tpeDefaultValueMap: Map[String, Any] = {
    (primitiveDefaultValueDefinition.toList ++ defaultValueDefinition.toList)
      .map { p=>
        p._1.typeSymbol.asClass.fullName -> p._2
      }
      .toMap
  }

  def defaultValueOf(tpe: Type) = {
    tpeDefaultValueMap.get(tpe.typeSymbol.asClass.fullName) match {
      case Some(v) => v
      case None =>
        val fullName = tpe.typeSymbol.asClass.fullName
        throw new RuntimeException(s"Can't find default value for type '$fullName'")
    }
  }


}
