import sbt._
import Keys._

object FlowSys extends Build {
  lazy val flowSys = Project(id = "flow-sys", base = file("."))
}
