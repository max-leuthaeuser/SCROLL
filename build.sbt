name := "RoleDispatch"

scalaVersion := "2.10.4"

version := "0.1"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:dynamics")

testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false
