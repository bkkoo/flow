import sbt._
import Keys._

object FlowBase extends Build {
  lazy val flowSys = ProjectRef(file("../sys"), "flow-sys")
  lazy val flowBase = Project(id = "flow-base", base = file(".")) dependsOn(flowSys)
}
