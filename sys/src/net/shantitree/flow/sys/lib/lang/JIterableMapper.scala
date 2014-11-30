package net.shantitree.flow.sys.lib.lang

import java.lang.{Iterable => JIterable}
import java.util.{Iterator => JIterator}

case class JIterableMapper[T, R](iterable: JIterable[T])(mapper: T=>R) extends JIterable[R]  {

  def iterator() = new JIterator[R] {

    val itr = iterable.iterator()
    def hasNext = itr.hasNext
    def next() = mapper(itr.next())
    def remove(): Unit = {
      throw new UnsupportedOperationException("Remove operation is not supported by this iterator!")
    }
  }

  def scalaIterator() = new Iterator[R] {
    val itr = iterable.iterator()
    def hasNext = itr.hasNext
    def next() = mapper(itr.next())
  }

}
