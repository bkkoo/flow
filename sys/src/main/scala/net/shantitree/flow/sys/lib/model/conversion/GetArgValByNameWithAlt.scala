package net.shantitree.flow.sys.lib.model.conversion

import net.shantitree.flow.sys.lib.model.conversion.TGetArgValByName.AltGetter

class GetArgValByNameWithAlt[T](val obj:T, val alt:AltGetter, val get: (String)=>Option[Any]) extends TGetArgValByName[T] {
  def apply(name: String): Any = {
    get(name) match {
      case Some(value) => value
      case None => alt(name)
    }
  }
}
