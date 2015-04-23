name := "RoleDispatch"

scalaVersion := "2.11.6"

val scalatestVersion = "2.2.1"

version := "0.4"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.2.0-RC4",
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.1",
  "com.assembla.scala-incubator" %% "graph-core" % "1.9.0",
  "com.assembla.scala-incubator" %% "graph-constrained" % "1.9.0"
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