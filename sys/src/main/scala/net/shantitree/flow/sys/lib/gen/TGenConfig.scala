package net.shantitree.flow.sys.lib.gen

import net.shantitree.flow.sys.lib.orient.graph.TGraphFactoryProvider

trait TGenConfig { this: TGenModelFieldName =>

  lazy val factory = factoryProvider.get()
  lazy val factoryProvider = new TGraphFactoryProvider {
    val dbConfigPath = "flow.db"
  }
  val mainPackage = "net.shantitree.flow"
  val packagePath = "sys.gen"
  val genOClasses = Set(
    "Partner",
    "SaleOrderHeader",
    "SaleOrderItem",
    "BzNode"
  )


}
