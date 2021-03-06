name := """gaia-model"""

artifactName := {(sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
    "net.shantitree.flow." + artifact.name + "-" + module.revision + "_" + sv.binary  +"." + artifact.extension
}

version := "1.0"

scalaVersion := "2.11.4"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
)

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.typesafe.slick" %% "slick-extensions" % "2.1.0"
)

parallelExecution := true

lazy val gaiaModel = project in file(".")
