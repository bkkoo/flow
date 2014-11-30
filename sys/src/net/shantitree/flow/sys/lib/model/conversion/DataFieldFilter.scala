package net.shantitree.flow.sys.lib.model.conversion

import scala.reflect.runtime.universe.{typeOf, Type, TermSymbol}

object DataFieldFilter {

  type Filter = PartialFunction[(String, (Type, TermSymbol)), Boolean]

  def apply(customFilter: Filter) = new DataFieldFilter {
    val filter = customFilter orElse default
  }

  def apply() = new DataFieldFilter {
    val filter = default
  }

}

trait DataFieldFilter {

  import DataFieldFilter.Filter

  val notDataValTpe = typeOf[OptionNotDataVal[_]]

  private val is = true

  private val isNot = false

  val filter:Filter

  val default:Filter = {
    case (_, (tpe, _)) if tpe <:< notDataValTpe => isNot
    case ("rid", _) => isNot
    case _ => is
  }

}
