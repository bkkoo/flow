package net.shantitree.flow.slick.qry

import scala.slick.lifted.Query

/**
 * Created by bkkoo on 14/10/2557.
 */
trait BaseQry {
  type Q <: Query[_, _, Seq]
  val query: Q
}
