package net.shantitree.flow.sys.lib.model.conversion

import net.shantitree.flow.sys.lib.model.conversion.TGetArgValByName.OptAltGetter

object GetArgValByName {

  def apply[T](obj: T)(get: String => Option[Any]):TGetArgValByName[T] = new GetArgValByNameNotAlt(obj, get)
  def apply[T](obj: T, optAlt: OptAltGetter)(get: String => Option[Any]): TGetArgValByName[T] = {
    optAlt match {
      case Some(alt) =>
        new GetArgValByNameWithAlt(obj, alt, get)
      case None =>
        new GetArgValByNameNotAlt(obj, get)
    }
  }
}
