import sbt._
import Keys._

object FlowDbSync extends Build {
  lazy val flowSys = ProjectRef(file("../sys"), "flow-sys")
  lazy val flowSlick = ProjectRef(file("../slick"), "flow-slick")
  lazy val flowSync = Project(id = "flow-dbsync", base = file(".")) dependsOn(flowSys, flowSlick)
}
