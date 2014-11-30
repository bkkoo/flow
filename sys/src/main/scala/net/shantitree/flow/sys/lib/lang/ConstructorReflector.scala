package net.shantitree.flow.sys.lib.lang

import scala.reflect.runtime.universe._

object tonstructorReflector {

  def apply[A](t: TagInfo[A]) = new ConstructorReflector[A] { val tagInfo = t}
}

trait ConstructorReflector[A] {

  val tagInfo: TagInfo[A]

  lazy val tpe = tagInfo.ttag.tpe
  lazy val rtMirror = runtimeMirror(getClass.getClassLoader)
  lazy val ctorParams = ctorMethodSymbol.paramLists.flatten
  lazy val ctorMethodSymbol = {
    val s = tpe.decl(termNames.CONSTRUCTOR)
    try {
      s.asMethod
    } catch {
      case e:Exception =>
        s.asTerm.alternatives.head.asMethod
    }
  }

  lazy val ctor = {
    rtMirror.reflectClass(tpe.typeSymbol.asClass).reflectConstructor(ctorMethodSymbol)
  }
  lazy val ctorParamsSigTerm =  {
    ctorParams.map {  s =>
      val name = s.name.toString
      (name, (s.typeSignature , tpe.decl(TermName(name)).asTerm))
    }
  }

  lazy val className = rtMirror.runtimeClass(tagInfo.ttag.tpe).getName

  def fieldMirrorsOf(instance: A):List[(String, (FieldMirror, Type))] = {
    ctorParamsSigTerm.map { case (name, (sig, term)) =>
      (name , (instanceMirrorOf(instance).reflectField(term), sig))
    }
  }

  def fieldMethodMirrorsOf(instance: A):List[(String, (MethodMirror, Type))] = {
    ctorParamsSigTerm.map { case (name, (sig, term)) =>
      (name, (instanceMirrorOf(instance).reflectMethod(term.asMethod), sig))
    }
  }

  def instanceMirrorOf(instance: A): InstanceMirror = {
    rtMirror.reflect(instance)(tagInfo.ctag)
  }

}
