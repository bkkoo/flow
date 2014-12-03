name := """sys"""

artifactName := {(sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
    "net.shantitree.flow." + artifact.name + "-" + module.revision + "_" + sv.binary  +"." + artifact.extension
}

version := "0.5"

scalaVersion := "2.11.4"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "OSS Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

lazy val sys = project in file(".")

lazy val orientId = "com.orientechnologies"

lazy val orientVer = "2.0-M3"

lazy val akkaId = "com.typesafe.akka"

lazy val akkaVer = "2.3.7"


libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-nop" % "1.6.4"
  ,"org.scalatest" %% "scalatest" % "2.1.6" % "test"
  ,"org.scala-lang" % "scala-reflect" % "2.11.1"
  ,akkaId % "akka-actor_2.11" % akkaVer
  ,"com.google.inject" % "guice" % "4.0-beta5"
  ,"net.codingwell" %% "scala-guice" % "4.0.0-beta5"
  ,orientId % "orient-commons" % "2.0-M1"
  ,orientId % "orientdb-client" % orientVer
  ,orientId % "orientdb-core" % orientVer
  ,orientId % "orientdb-graphdb" % orientVer
  ,"joda-time" % "joda-time" % "2.5"
  ,"org.joda" % "joda-convert" % "1.7"
)


fork := true

javaOptions += "-Xmx2G"

parallelExecution := true




