val akkaVersion = "2.4.0"
val shapelessVersion = "2.2.3"
val kiamaVersion = "1.8.0"
val jgraphTVersion = "0.9.1"
val scalameterVersion = "0.8-SNAPSHOT"
val scalatestVersion = "2.2.1"
val chocoVersion = "3.3.1"
val slf4jVersion = "1.7.12"
val contVersion = "1.0.2"
val macrosVersion = "2.0.1"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  version := "0.9.6",
  logBuffered := false,
  mainClass := None,
  autoCompilerPlugins := true,
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
  ),
  dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value, // fix for SORM
  addCompilerPlugin("org.scalamacros" % "paradise" % macrosVersion cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "com.googlecode.kiama" %% "kiama" % kiamaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.chuusai" %% "shapeless" % shapelessVersion,
    "org.jgrapht" % "jgrapht-core" % jgraphTVersion,
    "org.choco-solver" % "choco-solver" % chocoVersion,
    "org.slf4j" % "slf4j-simple" % slf4jVersion,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang.plugins" %% "scala-continuations-library" % contVersion,
    "org.eclipse.emf" % "org.eclipse.emf.common" % "2.10.1",
    "org.eclipse.emf" % "org.eclipse.emf.ecore" % "2.10.1",
    "org.eclipse.uml2" % "org.eclipse.uml2.uml" % "3.1.0.v201006071150"
  ),
  javacOptions in Compile ++= Seq("-source", "1.8", "-target", "1.8"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:dynamics",
    "-language:reflectiveCalls",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-unchecked",
    "-target:jvm-1.8")
)

lazy val root = (project in file(".")).settings(
  name := "SCROLLRoot"
).aggregate(core, tests, examples, benchmarks, persistence, macros)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "SCROLL",
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-Xlint",
      "-Xlint:-missing-interpolator",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture",
      "-Ywarn-unused-import"),
    organization := "com.github.max-leuthaeuser",
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
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

lazy val persistence = (project in file("persistence")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.mchange" % "c3p0" % "0.9.2-pre5",
      "com.github.nikita-volkov" % "embrace" % "0.1.4",
      "com.github.nikita-volkov" % "sext" % "0.2.4",
      "joda-time" % "joda-time" % "2.1",
      "org.joda" % "joda-convert" % "1.2",
      "com.google.guava" % "guava" % "13.0.1",
      "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.1.0",
      "postgresql" % "postgresql" % "9.1-901.jdbc4",
      "org.hsqldb" % "hsqldb" % "2.2.8",
      "com.h2database" % "h2" % "1.3.168",
      "mysql" % "mysql-connector-java" % "5.1.19"
    )
  ).dependsOn(core, macros)

lazy val macros = (project in file("macros")).
  settings(commonSettings: _*)

lazy val examples = (project in file("examples")).
  settings(commonSettings: _*).dependsOn(core)

lazy val tests = (project in file("tests")).
  settings(commonSettings: _*).
  settings(
    testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Suite"))),
    parallelExecution in Test := false,
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % scalatestVersion % "test")
  ).dependsOn(core, examples, persistence)

val scalaMeterFramework = new TestFramework("org.scalameter.ScalaMeterFramework")

lazy val benchmarks = (project in file("benchmarks")).
  settings(commonSettings: _*).
  settings(
    javaOptions += "-Xms4g",
    javaOptions += "-Xmx4g"
  ).dependsOn(core)

