package net.shantitree.flow.sys.lib.orient.oql.operator

import net.shantitree.flow.sys.lib.orient.oql.Criteria


case class Between[T](`this`: T)(implicit operandVal: OperandVal[T]) {
  def and(that: T): BetweenThisAndThat[T] = BetweenThisAndThat(`this`, that)
}
case class BetweenThisAndThat[T](`this`: T, that: T)(implicit operandVal: OperandVal[T]) extends Criteria {
  val clause = s"between ${operandVal.getValue("between", `this`)} and ${operandVal.getValue("and", that)}}"
}


