package net.shantitree.flow.sys.lib.model.conversion

import net.shantitree.flow.sys.lib.model.{ModelConversionException, TModel, TModelDef}
import scala.reflect.runtime.universe.{MethodMirror}


object ModelCtorArgs {

  import net.shantitree.flow.sys.lib.model.conversion.TGetArgValByName.{GetGetter, Getter}

  def from[M <: TModel, V](value:V, modelDef: TModelDef[M])(implicit getGetter: GetGetter[V]):ModelCtorArgs[M] = {
    apply(getGetter(value, None), modelDef)
  }

  def from[M <: TModel, V](value: V, instance: M)(implicit modelDef: TModelDef[M], getAltGetter: GetGetter[M], getGetter: GetGetter[V]): ModelCtorArgs[M] = {
    val altGetter = getAltGetter(instance, None)
    apply(getGetter(value, Some(altGetter)), modelDef)
  }

  def from[M <: TModel, V, A <: AnyRef](value: V, modelDef: TModelDef[M], alt: A)(implicit getAltGetter: GetGetter[A], getGetter: GetGetter[V]): ModelCtorArgs[M] = {
    val altGetter = getAltGetter(alt, None)
    apply(getGetter(value, Some(altGetter)), modelDef)
  }


  def apply[M <: TModel](getArgVal: Getter[_], modelDef: TModelDef[M]): ModelCtorArgs[M] = {
    val reflector = modelDef.reflector
    val convert = modelDef.argValConverter.convert
    new ModelCtorArgs[M] {
      lazy val modelName = modelDef.reflector.modelName
      lazy val ctor = reflector.ctor
      lazy val args = reflector.ctorParamsSigTerm.map { case (name, (sig, term)) =>
        convert((name, getArgVal(name), sig))
      }
    }
  }
}

trait ModelCtorArgs[M <: TModel] {

  val modelName: String
  val args: List[Any]
  
  protected val ctor: MethodMirror

  def createModel():M = {
    try {
      ctor(args:_*).asInstanceOf[M]
    } catch {
      case e:Exception =>
        throw ModelConversionException(
          s"Can't creating new model '$modelName' with args = " +
            s"${args.mkString(",")}", e)
    }
  }
}