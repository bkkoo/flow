name := """flow-slick"""

version := "0.5"

scalaVersion := "2.11.4"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "OSS Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-nop" % "1.6.4"
  ,"org.scalatest" %% "scalatest" % "2.1.6" % "test"
  ,"net.sourceforge.jtds" % "jtds" % "1.3.1"
  ,"com.typesafe.slick" %% "slick" % "2.1.0"
  ,"com.typesafe.slick" %% "slick-extensions" % "2.1.0"
  ,"com.google.inject" % "guice" % "4.0-beta5"
  ,"net.codingwell" %% "scala-guice" % "4.0.0-beta5"
  ,"joda-time" % "joda-time" % "2.5"
  ,"org.joda" % "joda-convert" % "1.7"
)


fork := true

javaOptions += "-Xmx2G"

parallelExecution := true


