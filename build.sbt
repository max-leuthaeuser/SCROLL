name := "RoleDispatch"

scalaVersion := "2.11.5"

val gremlinScalaV = "3.0.0.M6c"

val titanV = "0.9.0-M1"

val scalatestV = "2.2.1"

version := "0.3.2"

libraryDependencies ++= Seq(
	"com.thinkaurelius.titan" % "titan-core" % titanV,
	"com.thinkaurelius.titan" % "titan-cassandra" % titanV,
	"com.thinkaurelius.titan" % "titan-es" % titanV,
  	"com.michaelpollmeier" %% "gremlin-scala" % gremlinScalaV,
  	"org.scalatest" %% "scalatest" % scalatestV % "test",
	"org.scala-lang.modules" 		%%	"scala-xml" 		%	"1.0.1",
	"com.assembla.scala-incubator" 	%%	"graph-core" 		%	"1.9.0",
	"com.assembla.scala-incubator" 	%%	"graph-constrained" %	"1.9.0"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:dynamics")

testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false

net.virtualvoid.sbt.graph.Plugin.graphSettings
