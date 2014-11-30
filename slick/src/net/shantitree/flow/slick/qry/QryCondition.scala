package net.shantitree.flow.slick.qry

import net.shantitree.flow.slick.qry.param.Param

/**
 * Created by bkkoo on 15/10/2557.
 */
trait QryCondition[B <: BaseQry] extends (()=>B) {

  implicit val baseQry: B = apply()
  val query = baseQry.query

  def apply(q: B#Q): B

  protected def condition[P <: Param](where: P => B#Q) = {
    Condition[P, B] { param =>
      apply(where(param))
    }
  }
}
