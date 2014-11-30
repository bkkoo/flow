package net.shantitree.flow.sys.lib.model.conversion

import scala.reflect.runtime.universe.Type

object DataValConverter {
  type Converter = PartialFunction[(String, (Any, Type)), (String, Any)]

  def apply(customConverter: Converter) = new DataValConverter {
    val convert =customConverter orElse default
  }

  def apply() = new DataValConverter {
    val convert = default
  }

}

trait DataValConverter {
  import DataValConverter.Converter

  val convert:Converter
  val default:Converter = {
    case (k, (Some(v),_)) => (k, v)
    case (k, (None,_)) => (k, null)
    case (k, (v,_)) => (k, v)
  }

}
