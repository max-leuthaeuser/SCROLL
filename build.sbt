name := "RoleDispatch"

scalaVersion := "2.11.0"

version := "0.2"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.3" % "test"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:dynamics")

testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false
