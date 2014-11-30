package net.shantitree.flow.sys.lib.lang

import scala.reflect.runtime.universe._
import scala.reflect.ClassTag

class TagInfo[A:TypeTag:ClassTag] {
  val ttag = implicitly[TypeTag[A]]
  val ctag =  implicitly[ClassTag[A]]
}
