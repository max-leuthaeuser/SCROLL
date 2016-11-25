val akkaVersion = "2.4.12"
val shapelessVersion = "2.3.2"
val scalatestVersion = "3.0.0"
val chocoVersion = "4.0.0"
val slf4jVersion = "1.7.21"
val macrosVersion = "2.1.0"
val scalaloggingVersion = "3.5.0"
val guavaVersion = "20.0"
val jgraphTVersion = "1.0.0"

lazy val commonSettings = Seq(
  scalaVersion := "2.12.0",
  version := "1.3.0",
  mainClass := None,
  resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
  ),
  libraryDependencies ++= Seq(
    "org.jgrapht" % "jgrapht-core" % jgraphTVersion,
    "com.google.guava" % "guava" % guavaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.chuusai" %% "shapeless" % shapelessVersion,
    "org.choco-solver" % "choco-solver" % chocoVersion,
    "org.slf4j" % "slf4j-simple" % slf4jVersion,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.eclipse.emf" % "org.eclipse.emf.common" % "2.11.0-v20150805-0538",
    "org.eclipse.emf" % "org.eclipse.emf.ecore" % "2.11.1-v20150805-0538",
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
).aggregate(core, tests, examples)

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
      "-Xfuture"),
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

lazy val examples = (project in file("examples")).
  settings(commonSettings: _*).dependsOn(core)

lazy val tests = (project in file("tests")).
  settings(commonSettings: _*).
  settings(
    testOptions in Test := Seq(Tests.Filter(s => s.endsWith("Suite"))),
    parallelExecution in Test := false,
    libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % scalatestVersion % "test")
  ).dependsOn(core, examples)
