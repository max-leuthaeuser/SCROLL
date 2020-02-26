val lib = Dependencies
val linting = Linting

val utf8 = java.nio.charset.StandardCharsets.UTF_8.toString

// enables experimental Turbo mode with ClassLoader layering from sbt 1.3
ThisBuild / turbo := true

ThisBuild / scalaVersion := lib.v.scalaVersion

lazy val noPublishSettings =
  Seq(publish := {}, publishLocal := {}, publishArtifact := false)

lazy val root = (project in file(".")).
  settings(
    name := "SCROLLRoot",
    noPublishSettings
  ).
  aggregate(core, tests, examples)

lazy val commonSettings = Seq(
  version := "2.0",
  mainClass := None,
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
  ),
  libraryDependencies ++= lib.coreDependencies,
  dependencyOverrides ++= lib.coreDependenciesOverrides,
  javacOptions in Compile ++= Seq("-source", "1.8", "-target", "1.8"),
  scalacOptions ++= Seq(
    "-encoding", utf8,
    "-deprecation",                       // Emit warning and location for usages of deprecated APIs.
    "-feature",                           // Emit warning and location for usages of features that should be imported explicitly.
    "-language:dynamics",                 // Allow direct or indirect subclasses of scala.Dynamic.
    "-language:reflectiveCalls",          // Allow reflective access to members of structural types.
    "-language:postfixOps",               // Allow postfix operator notation.
    "-language:implicitConversions",      // Allow definition of implicit functions called views.
    "-unchecked",                         // Enable additional warnings where generated code depends on assumptions.
    "-target:jvm-" + lib.v.jvm),
  coverageExcludedPackages := "<empty>;scroll\\.benchmarks\\..*;scroll\\.examples\\.currency",
  updateOptions := updateOptions.value.withCachedResolution(true),
  historyPath := Option(target.in(LocalRootProject).value / ".history"),
  cleanKeepFiles := cleanKeepFiles.value filterNot { file =>
    file.getPath.endsWith(".history")
  },
  cancelable in Global := true,
  logLevel in Global := {
    if (insideCI.value) Level.Error else Level.Info
  },
  showSuccess := true,
  showTiming := true,
  initialize ~= { _ =>
    val ansi = System.getProperty("sbt.log.noformat", "false") != "true"
    if (ansi) System.setProperty("scala.color", "true")
  }
)

lazy val core = project.
  settings(
    commonSettings,
    linting.staticAnalysis,
    name := "SCROLL",
    scalacOptions ++= Seq(
      "-explaintypes",                     // Explain type errors in more detail.
      "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
      "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
      "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
      "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
      "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
      "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
      "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
      "-Xlint:option-implicit",            // Option.apply used implicit view.
      "-Xlint:package-object-classes",     // Class or object defined in package object.
      "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
      "-Ywarn-dead-code",                  // Warn when dead code is identified.
      "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
      "-Xlint:nullary-override",           // Warn when non-nullary def f() overrides nullary def f.
      "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
      "-Ywarn-numeric-widen",              // Warn when numerics are widened.
      "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.
      "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
      "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
      "-Ywarn-unused:locals",              // Warn if a local definition is unused.
      "-Ywarn-unused:params",              // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates"             // Warn if a private member is unused.
    ),
    organization := "com.github.max-leuthaeuser",
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) {
        Option("snapshots" at nexus + "content/repositories/snapshots")
      }
      else {
        Option("releases" at nexus + "service/local/staging/deploy/maven2")
      }
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra :=
      <url>https://github.com/max-leuthaeuser/SCROLL</url>
        <licenses>
          <license>
            <name>LGPL 3.0 license</name>
            <url>http://www.opensource.org/licenses/lgpl-3.0.html</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <connection>scm:git:github.com/max-leuthaeuser/SCROLL.git</connection>
          <developerConnection>scm:git:git@github.com:max-leuthaeuser/SCROLL.git</developerConnection>
          <url>github.com/max-leuthaeuser/SCROLL</url>
        </scm>
        <developers>
          <developer>
            <id>max-leuthaeuser</id>
            <name>Max Leuthaeuser</name>
            <url>https://wwwdb.inf.tu-dresden.de/rosi/investigators/doctoral-students/</url>
          </developer>
        </developers>
  )

lazy val examples = project.
  settings(commonSettings).
  dependsOn(core)

lazy val tests = project.
  settings(
    commonSettings,
    fork in Test := true,
    testOptions in Test := Seq(
      Tests.Argument(TestFrameworks.ScalaTest
        // F: show full stack traces
        // S: show short stack traces
        // D: show duration for each test
        // I: print "reminders" of failed and canceled tests at the end of the summary,
        //    eliminating the need to scroll and search to find failed or canceled tests.
        //    replace with G (or T) to show reminders with full (or short) stack traces
        // K: exclude canceled tests from reminder
        , "-oDI"
        // Periodic notification of slowpokes (tests that have been running longer than 30s)
        , "-W", "30", "30"
      )
    ), libraryDependencies ++= lib.testDependencies
  ).
  dependsOn(core, examples)

lazy val benchmark = project.
  settings(
    commonSettings,
    mainClass in(Jmh, run) := Option("scroll.benchmarks.RunnerApp")
  ).
  enablePlugins(JmhPlugin).
  dependsOn(core)
