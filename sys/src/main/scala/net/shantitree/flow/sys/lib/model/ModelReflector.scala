package net.shantitree.flow.sys.lib.model

import net.shantitree.flow.sys.lib.lang.{TagInfo, ConstructorReflector}
import net.shantitree.flow.sys.lib.model.conversion.{DefaultDataValConverter, DataValConverter, DefaultDataFieldFilter, DataFieldFilter}
import scala.reflect.runtime.universe._
import scala.util.Try

case class ModelReflector[M <: TModel](tagInfo: TagInfo[M], dataValConverter: DataValConverter = DefaultDataValConverter, dataFieldFilter: DataFieldFilter = DefaultDataFieldFilter)
  extends ConstructorReflector[M]  {

  lazy val modelName = rtMirror.runtimeClass(tagInfo.ttag.tpe).getSimpleName

  lazy val dataFieldsSigTerm = {
    val ctorDataFieldsSigTerm = ctorParamsSigTerm.filter(dataFieldFilter.filter)
    val ctorParamNames = ctorParamsSigTerm.map(_._1).toSet
    val additionalFieldsSigTerm = tagInfo.ctag.runtimeClass
      .getDeclaredFields
      .map(_.getName)
      .filterNot(ctorParamNames)
      .map { name => Try {
          val termSymbol = tagInfo.ttag.tpe.decl(TermName(name))
          (name, (termSymbol.typeSignature, termSymbol.asTerm))
      }}
      .filter(_.isSuccess)
      .map(_.get)
      .filter(dataFieldFilter.filter)

    ctorDataFieldsSigTerm ++ additionalFieldsSigTerm
  }

  def dataFieldMirrorOf(model: M): List[(String, (FieldMirror, Type))] = {
    dataFieldsSigTerm.map { case (name, (sig, term)) =>
      (name , (instanceMirrorOf(model).reflectField(term), sig))
    }
  }

  def dataFieldMethodMirrorOf(model: M):List[(String, (MethodMirror, Type))] = {
    dataFieldsSigTerm.map { case (name, (sig, term)) =>
      (name, (instanceMirrorOf(model).reflectMethod(term.asMethod), sig))
    }
  }

  def dataValOf(model: M): List[(String, Any)] = {
    dataFieldMethodMirrorOf(model).map { case (name, (methodMirror, sig)) =>
      dataValConverter.convert((name, (methodMirror(), sig)))
    }
  }

}
