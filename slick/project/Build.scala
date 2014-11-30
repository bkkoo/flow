import sbt._
import Keys._

object FlowSlick extends Build {
  lazy val flowSlick = Project(id = "flow-slick", base = file("."))
}
