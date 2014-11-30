package net.shantitree.flow.sys.lib.lang

/**
 * Created by bkkoo on 18/10/2557.
 */
trait Aspect[R] {
  def before(): Unit
  def after(r: R): Unit
}
