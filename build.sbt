name := "RoleDispatch"

scalaVersion := "2.11.4"

version := "0.3.1"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.3" % "test"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.1"

libraryDependencies += "com.assembla.scala-incubator" %% "graph-core" % "1.9.0"

libraryDependencies += "com.assembla.scala-incubator" %% "graph-constrained" % "1.9.0"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:dynamics")

testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false
