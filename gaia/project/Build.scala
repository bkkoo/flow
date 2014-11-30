import sbt._
import Keys._

object FlowGaia extends Build {
  lazy val flowSys = ProjectRef(file("../sys"), "flow-sys")
  lazy val flowSlick = ProjectRef(file("../slick"), "flow-slick")
  lazy val flowDbSync = ProjectRef(file("../dbsync"), "flow-dbsync")
  lazy val flowBase = ProjectRef(file("../base"), "flow-base")
  lazy val flowGaia = Project(id = "flow-gaia", base = file(".")) dependsOn(flowSys, flowSlick, flowDbSync, flowBase)
}
