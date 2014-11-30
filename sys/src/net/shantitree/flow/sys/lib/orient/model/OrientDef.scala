package net.shantitree.flow.sys.lib.orient.model

case class OrientDef (
  oClassName: String,
  oSuperClassName:Option[String] = None,
  optClusterName:Option[String] = None
)
