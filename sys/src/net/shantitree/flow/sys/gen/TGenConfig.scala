package net.shantitree.flow.sys.gen

import net.shantitree.flow.sys.graph.GraphFactoryProvider

trait TGenConfig { this: TGenModelFieldName =>

  val factory = GraphFactoryProvider.get()
  val mainPackage = "net.shantitree.flow"
  val packagePath = "sys.gen"
  val genOClasses = Set(
    "Partner",
    "SaleOrderHeader",
    "SaleOrderItem",
    "BzNode"
  )


}
