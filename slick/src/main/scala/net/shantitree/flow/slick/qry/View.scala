package net.shantitree.flow.slick.qry

import net.shantitree.flow.slick.qry.param.Param

import scala.slick.lifted.Query

trait View[Q <: BaseQry] {
  def apply(base: Q) = query(base.query)
  def query(base: Q#Q): Query[_, _ <: Product, Seq]
  def query()(implicit base: Q): Query[_, _ <: Product, Seq] = query(base.query)
  def query[P <: Param](param: P)(implicit filter: Condition[P, Q]): Query[_, _ <: Product, Seq] = {
    query(filter(param).query)
  }
}


