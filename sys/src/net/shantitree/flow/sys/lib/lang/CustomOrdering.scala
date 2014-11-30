package net.shantitree.flow.sys.lib.lang

object CustomOrdering {

  trait JavaDateTimeOrdering extends Ordering[java.util.Date] {
    def compare(x: java.util.Date, y: java.util.Date) =
      x.compareTo(y)
  }

  implicit object JavaDateTime extends JavaDateTimeOrdering

}
