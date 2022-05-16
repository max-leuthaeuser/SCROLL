val lib = Dependencies

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := lib.v.scalaVersion

addCommandAlias("format", ";scalafmtAll;scalafmtSbt")

lazy val noPublishSettings = Seq(publish := {}, publishLocal := {}, publishArtifact := false)

lazy val root = (project in file("."))
  .settings(name := "SCROLLRoot", noPublishSettings)
  .aggregate(core, tests, examples)

lazy val commonSettings = Seq(
  version := "3.0",
  resolvers ++= Seq(
    "Sonatype OSS Snapshots".at("https://oss.sonatype.org/content/repositories/snapshots"),
    "Sonatype OSS Releases".at("https://oss.sonatype.org/content/repositories/releases")
  ),
  libraryDependencies ++= lib.coreDependencies,
  dependencyOverrides ++= lib.coreDependenciesOverrides,
  Compile / javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  scalacOptions ++= Seq(
    // Emit warning and location for usages of deprecated APIs:
    "-deprecation",
    // Emit warning and location for usages of features that should be imported explicitly:
    "-feature",
    // Allow direct or indirect subclasses of scala.Dynamic:
    "-language:dynamics",
    // Allow reflective access to members of structural types:
    "-language:reflectiveCalls",
    // Allow postfix operator notation:
    "-language:postfixOps",
    // Allow definition of implicit functions called views:
    "-language:implicitConversions",
    // Enable additional warnings where generated code depends on assumptions:
    "-unchecked"
  ),
  updateOptions       := updateOptions.value.withCachedResolution(true),
  historyPath         := Option((LocalRootProject / target).value / ".history"),
  Global / cancelable := true,
  Global / logLevel := {
    if (insideCI.value) Level.Error else Level.Info
  },
  initialize ~= { _ =>
    val ansi = System.getProperty("sbt.log.noformat", "false") != "true"
    if (ansi) System.setProperty("scala.color", "true")
  }
)

lazy val core = project.settings(
  commonSettings,
  Compile / run / mainClass := None,
  name                      := "SCROLL",
  scalacOptions += "-Xfatal-warnings",
  organization           := "com.github.max-leuthaeuser",
  publishTo              := sonatypePublishToBundle.value,
  publishMavenStyle      := true,
  Test / publishArtifact := false,
  scmInfo := Some(
    ScmInfo(url("https://github.com/max-leuthaeuser/SCROLL"), "scm:git:github.com/max-leuthaeuser/SCROLL.git")
  ),
  homepage := Some(url("https://github.com/max-leuthaeuser/SCROLL")),
  licenses := List("LGPL 3.0 license" -> url("http://www.opensource.org/licenses/lgpl-3.0.html")),
  developers := List(
    Developer(
      "max-leuthaeuser",
      "Max Leuthaeuser",
      "max.leuthaeuser@tu-dresden.de",
      url("https://wwwdb.inf.tu-dresden.de/rosi/investigators/doctoral-students/")
    )
  ),
  pomIncludeRepository := { _ =>
    false
  }
)

lazy val examples = project.settings(commonSettings).dependsOn(core)

lazy val tests = project
  .settings(
    commonSettings,
    Test / fork := true,
    Test / testOptions := Seq(
      Tests.Argument(
        TestFrameworks.ScalaTest,
        // F: show full stack traces
        // S: show short stack traces
        // D: show duration for each test
        // I: print "reminders" of failed and canceled tests at the end of the summary,
        //    eliminating the need to scroll and search to find failed or canceled tests.
        //    replace with G (or T) to show reminders with full (or short) stack traces
        // K: exclude canceled tests from reminder
        "-oDI",
        // Periodic notification of slowpokes (tests that have been running longer than 30s)
        "-W",
        "30",
        "30"
      )
    ),
    libraryDependencies ++= lib.testDependencies
  )
  .dependsOn(core, examples)

lazy val benchmark = project
  .settings(commonSettings, Jmh / run / mainClass := Option("scroll.benchmarks.RunnerApp"))
  .enablePlugins(JmhPlugin)
  .dependsOn(core)
