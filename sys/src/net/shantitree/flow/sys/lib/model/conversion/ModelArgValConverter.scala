package net.shantitree.flow.sys.lib.model.conversion

import scala.reflect.runtime.universe._
import net.shantitree.flow.sys.lib.lang.RuTypeDefinition._
import net.shantitree.flow.sys.lib.lang.TypeDefaultValDefinition._

object ModelArgValConverter {
  type Converter = PartialFunction[(String, Any, Type), Any]
  def apply(customConverter: Converter)  = new ModelArgValConverter {
    lazy val convert = customConverter orElse default
  }
  def apply() = new ModelArgValConverter {
    lazy val convert = default
  }
}

trait ModelArgValConverter {
  
 import ModelArgValConverter.Converter
  
  protected val toAny:Converter = {
    case (_, Some(value), _) => value
    case (_, None, tpe) => defaultValueOf(tpe)
    case (_, null, tpe) => defaultValueOf(tpe)
    case (_, value, _) => value
  }

  protected val toOption:Converter = {
    case (_, value, tpe) if tpe <:< OptionTpe =>
      value match {
        case null => None
        case Some(_) => value
        case None => value
        case _ => Some(value)
      }
  }

  protected val toOptionNotDataVal:Converter = {
    case (_, value, tpe) if tpe <:< OptionNotDataValTpe =>
      value match {
        case null => NoneNotDataVal
        case SomeNotDataVal(_) => value
        case NoneNotDataVal => value
        case _ => SomeNotDataVal(value)
      }
  }

  protected val toBigDecimal:Converter = {
    case (_, value, tpe) if tpe =:= BigDecimalTpe =>
      value match {
        case null => defaultValueOf(tpe)
        case v if v.isInstanceOf[Double] => BigDecimal(v.asInstanceOf[Double]).bigDecimal
        case v if v.isInstanceOf[Float] => BigDecimal(v.asInstanceOf[Float].toDouble).bigDecimal
        case v if v.isInstanceOf[String] => BigDecimal(v.asInstanceOf[String]).bigDecimal
      }
  }

  val convert: Converter
  lazy val default = toOption orElse toBigDecimal orElse toOptionNotDataVal orElse toAny
}
