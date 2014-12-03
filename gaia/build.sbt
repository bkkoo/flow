import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

packageArchetype.akka_application


lazy val gaia = project in file (".") dependsOn(sys, base, slick, dbsync, gaiaModel)

lazy val sys = ProjectRef(file("../sys"), "sys")

lazy val dbsync = ProjectRef(file("../dbsync"), "dbsync")

lazy val slick = ProjectRef(file("../slick"), "slick")

lazy val gaiaModel = ProjectRef(file("../gaia-model"), "gaiaModel")

lazy val base = ProjectRef(file("../base"), "base")

name := """gaia"""

artifactName := {(sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
    "net.shantitree.flow." + artifact.name + "-" + module.revision + "_" + sv.binary  +"." + artifact.extension
}

version := "0.5"

scalaVersion := "2.11.4"

mainClass in Compile := Some("net.shantitree.flow.gaia.sync.GaiaSyncKernel")

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "OSS Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-nop" % "1.6.4"
  ,"org.scalatest" %% "scalatest" % "2.1.6" % "test"
  ,"com.typesafe.akka" % "akka-actor_2.11" % "2.3.7"
  ,"com.typesafe.akka" % "akka-kernel_2.11" % "2.3.7"
  ,"com.typesafe.akka" % "akka-remote_2.11" % "2.3.7"
  ,"com.google.inject" % "guice" % "4.0-beta5"
  ,"net.codingwell" %% "scala-guice" % "4.0.0-beta5"
  ,"joda-time" % "joda-time" % "2.5"
  ,"org.joda" % "joda-convert" % "1.7"
)

