name := "SCROLL"

scalaVersion := "2.11.7"

val akkaVersion = "2.4.0"
val shapelessVersion = "2.2.3"
val kiamaVersion = "1.8.0"
val jgraphTVersion = "0.9.1"
val scalameterVersion = "0.8-SNAPSHOT"
val scalatestVersion = "2.2.1"

version := "0.9.3"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.chuusai" %% "shapeless" % shapelessVersion,
  "com.googlecode.kiama" %% "kiama" % kiamaVersion,
  "org.jgrapht" % "jgrapht-core" % jgraphTVersion,
  "org.eclipse.emf" % "org.eclipse.emf.common" % "2.10.1",
  "org.eclipse.emf" % "org.eclipse.emf.ecore" % "2.10.1",
  "org.eclipse.uml2" % "org.eclipse.uml2.uml" % "3.1.0.v201006071150",
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "com.storm-enroute" %% "scalameter" % scalameterVersion % "test"
)

javacOptions in Compile ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq("-unchecked",
  "-deprecation",
  "-feature",
  "-language:dynamics",
  "-language:reflectiveCalls",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-target:jvm-1.8")

val scalaMeterFramework = new TestFramework("org.scalameter.ScalaMeterFramework")

testFrameworks in Test += scalaMeterFramework

testOptions in Test += Tests.Argument(scalaMeterFramework, "-silent")

parallelExecution in Test := false

logBuffered := false

// its a library
mainClass := None

organization := "com.github.max-leuthaeuser"

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

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
