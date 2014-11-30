package net.shantitree.flow.slick.qry

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._

trait SelectQry {
  val select: Query[_, _ <: Product, Seq]
}

