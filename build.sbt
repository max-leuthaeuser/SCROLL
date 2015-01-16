name := "RoleDispatch"

scalaVersion := "2.11.5"

version := "0.3.2"

libraryDependencies ++= Seq(
	"org.scalatest" 				%	"scalatest_2.11" 	%	"2.1.3"		%	"test",
	"org.scala-lang.modules" 		%%	"scala-xml" 		%	"1.0.1",
	"com.assembla.scala-incubator" 	%%	"graph-core" 		%	"1.9.0",
	"com.assembla.scala-incubator" 	%%	"graph-constrained" %	"1.9.0"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:dynamics")

testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false
