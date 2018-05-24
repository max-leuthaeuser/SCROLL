val scalatestVersion = "3.2.0-SNAP10"
val chocoVersion = "4.0.6"
val slf4jVersion = "1.7.25"
val guavaVersion = "25.0-jre"

lazy val commonSettings = Seq(
  scalaVersion := dottyLatestNightlyBuild.get, // "0.8.0-RC1"
  version := "1.5",
  mainClass := None,
  libraryDependencies ++= Seq(
    "com.google.guava" % "guava" % guavaVersion,
    "org.choco-solver" % "choco-solver" % chocoVersion,
    "org.slf4j" % "slf4j-simple" % slf4jVersion,
    "ch.epfl.lamp" % "scala-reflect" % scalaVersion.value,
  ),
  javacOptions in Compile ++= Seq("-source", "1.8", "-target", "1.8"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:dynamics,reflectiveCalls,postfixOps,implicitConversions",
    "-unchecked",
    "-target:jvm-1.8")
)

lazy val root = (project in file(".")).settings(
  name := "SCROLLRoot"
).aggregate(core, tests, examples)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).settings(name := "SCROLL")

lazy val examples = (project in file("examples")).
  settings(commonSettings: _*).dependsOn(core)

lazy val tests = (project in file("tests")).
  settings(commonSettings: _*).
  settings(
    testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Suite"))),
    libraryDependencies += ("org.scalatest" %% "scalatest" % scalatestVersion % "test").withDottyCompat(scalaVersion.value)
  ).dependsOn(core, examples)
