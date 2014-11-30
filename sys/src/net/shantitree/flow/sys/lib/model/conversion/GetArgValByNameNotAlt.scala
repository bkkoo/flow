package net.shantitree.flow.sys.lib.model.conversion

class GetArgValByNameNotAlt[T](val obj:T, val get: String=>Option[Any]) extends TGetArgValByName[T] {
  def apply(name: String): Any = {
    get(name) match {
      case Some(value) => value
      case None => null
    }
  }
}
