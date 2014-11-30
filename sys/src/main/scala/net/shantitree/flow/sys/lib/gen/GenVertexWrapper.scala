package net.shantitree.flow.sys.lib.gen

object GenVertexWrapper extends TGenVertexWrapper with TGenConfig {
  val fileName = "VertexWrapper"
  override val imports = List(
    "com.tinkerpop.blueprints.Vertex",
    "com.tinkerpop.blueprints.impls.orient.OrientVertex"
  )
  override val importsInMainPackage = List(
    "model._",
    "lib.model.Model"
  )
}
