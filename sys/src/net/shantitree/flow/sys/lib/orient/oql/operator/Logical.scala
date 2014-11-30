package net.shantitree.flow.sys.lib.orient.oql.operator

import net.shantitree.flow.sys.lib.orient.oql.Criteria


object Logical {
  implicit def criteriaToLogical(criteria: Criteria): Logical = new Logical(criteria)
}
class Logical(criteria: Criteria) {
  def and(rightCriteria: Criteria) = toCriteria("and", rightCriteria)
  def or(rightCriteria: Criteria) = toCriteria("or", rightCriteria)
  protected def toCriteria(op: String, rightCriteria: Criteria) = new Criteria { val clause = s"${criteria.clause} $op ${rightCriteria.clause}" }
}
