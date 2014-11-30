package net.shantitree.flow.sys.lib.model.conversion

case class SomeNotDataVal[+T](value: T) extends OptionNotDataVal[T] {
  def get: T = value
}

case object NoneNotDataVal extends OptionNotDataVal[Nothing] {
  def get = throw new NoSuchElementException("NoneNotDataVal.get")
}

trait OptionNotDataVal[+T] {
  def get: T
  
}
