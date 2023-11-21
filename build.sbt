import scala.Boolean

ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "3.2.2"

val scalaTestVersion = "3.2.11"
val typeSafeConfigVersion = "1.4.2"
val logbackVersion = "1.2.10"
val sfl4sVersion = "2.0.0-alpha5"
val graphVizVersion = "0.18.1"
val netBuddyVersion = "1.14.4"
val catsVersion = "2.9.0"
val apacheCommonsVersion = "2.13.0"
val jGraphTlibVersion = "1.5.2"
val scalaParCollVersion = "1.0.4"
val akkaVersion = "2.8.0"
val akkaHTTPVersion = "10.5.0"

lazy val commonDependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHTTPVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHTTPVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.scala-lang.modules" %% "scala-parallel-collections" % scalaParCollVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalatestplus" %% "mockito-4-2" % "3.2.12.0-RC2" % Test,
  "com.typesafe" % "config" % typeSafeConfigVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion excludeAll(
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.slf4j.impl")
  ),
  "net.bytebuddy" % "byte-buddy" % netBuddyVersion,
  "org.graphstream" % "gs-core" % "2.0",
  "org.yaml" % "snakeyaml" % "2.0",
  "org.mockito" % "mockito-core" % "5.2.0" % Test,
  "org.apache.mrunit" % "mrunit" % "1.1.0" % Test,
).map(_.exclude("org.slf4j", "*"))

lazy val root = (project in file("."))
  .settings(
    name := "GraphGame"
  )

Compile / run / mainClass := Some("app.Main")

assembly / assemblyJarName := "graph-game.jar"

assembly / mainClass := Some("app.Main")

unmanagedBase := baseDirectory.value / "src" / "main" / "resources" / "lib"


libraryDependencies ++= commonDependencies

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "reference.conf"         => MergeStrategy.concat
  case _                        => MergeStrategy.first
}