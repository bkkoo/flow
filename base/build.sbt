name := """flow-base"""

version := "0.5"

scalaVersion := "2.11.4"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "OSS Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-nop" % "1.6.4"
  ,"org.scalatest" %% "scalatest" % "2.1.6" % "test"
  ,"com.orientechnologies" % "orient-commons" % "2.0-M1"
  ,"com.orientechnologies" % "orientdb-client" % "2.0-M3"
  ,"com.orientechnologies" % "orientdb-core" % "2.0-M3"
  ,"com.orientechnologies" % "orientdb-graphdb" % "2.0-M3"
  ,"joda-time" % "joda-time" % "2.5"
  ,"org.joda" % "joda-convert" % "1.7"
)


fork := true

javaOptions += "-Xmx2G"

parallelExecution := true


