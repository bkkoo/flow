package net.shantitree.flow.slick.qry

import net.shantitree.flow.slick.qry.param.{All, Param}

object Condition {

  def apply[P <: Param, B <: BaseQry]($: P=>B) = {
    new Condition[P, B] {
      def apply(param: P) = $(param)
    }
  }

  implicit def pure[B <: BaseQry](implicit base: B) = {
    new Condition[All, B] {
      def apply(param: All) = base
    }
  }
}

trait Condition[P <: Param, B <: BaseQry] extends ((P)=>B)
