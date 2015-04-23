name := "RoleDispatch"

scalaVersion := "2.11.6"

val scalatestVersion = "2.2.1"
val shapelessVersion = "2.2.0-RC4"
val scalaxmlVersion = "1.0.1"
val graphCoreVersion = "1.9.0"
val graphConstrainedVersion = "1.9.0"

version := "0.4"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % shapelessVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "org.scala-lang.modules" %% "scala-xml" % scalaxmlVersion,
  "com.assembla.scala-incubator" %% "graph-core" % graphCoreVersion,
  "com.assembla.scala-incubator" %% "graph-constrained" % graphConstrainedVersion
)

scalacOptions ++= Seq("-unchecked",
  "-deprecation",
  "-feature",
  "-language:dynamics",
  "-language:reflectiveCalls",
  "-language:postfixOps",
  "-language:implicitConversions")

testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false