package net.shantitree.flow.sys.lib.lang

/**
 * Created by bkkoo on 14/11/2557.
 */
object Box {
  def apply(value: Int):Integer = value.asInstanceOf[Integer]
  val True = Boolean.box(true)
  val False = Boolean.box(false)
}
