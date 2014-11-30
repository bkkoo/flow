import sbt._
import Keys._

object FlowCurAccumPV extends Build {
  lazy val flowSys = ProjectRef(file("../sys"), "flow-sys")
  lazy val flowBase = ProjectRef(file("../base"), "flow-base")
  lazy val flowGaia = Project(id = "flow-curaccumpv", base = file(".")) dependsOn(flowBase, flowSys)

}
