import sbt.stringToOrganization

object Dependencies extends Dependencies

trait Dependencies {

  object v {
    val scalaVersion = "2.13.3"
    val akkaVersion = "2.6.10"
    val shapelessVersion = "2.3.3"
    val scalatestVersion = "3.2.2"
    val chocoVersion = "4.10.5"
    val guavaVersion = "29.0-jre"
    val emfcommonVersion = "2.20.0"
    val emfecoreVersion = "2.23.0"
    val umlVersion = "3.1.0.v201006071150"
    val jvm = "1.8"
  }

  val coreDependencies = Seq(
    "com.google.guava" % "guava" % v.guavaVersion,
    "com.typesafe.akka" %% "akka-actor" % v.akkaVersion,
    "com.chuusai" %% "shapeless" % v.shapelessVersion,
    "org.choco-solver" % "choco-solver" % v.chocoVersion,
    "org.scala-lang" % "scala-reflect" % v.scalaVersion,
    "org.eclipse.emf" % "org.eclipse.emf.common" % v.emfcommonVersion,
    "org.eclipse.emf" % "org.eclipse.emf.ecore" % v.emfecoreVersion,
    "org.eclipse.uml2" % "org.eclipse.uml2.uml" % v.umlVersion
  )

  val coreDependenciesOverrides = Seq(
    "org.scala-lang" % "scala-library" % v.scalaVersion,
    "org.eclipse.emf" % "org.eclipse.emf.common" % v.emfcommonVersion,
    "org.eclipse.emf" % "org.eclipse.emf.ecore" % v.emfecoreVersion
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % v.scalatestVersion % "test",
    "org.scalatest" %% "scalatest-core" % v.scalatestVersion % "test",
    "org.scalatest" %% "scalatest-shouldmatchers" % v.scalatestVersion % "test"
  )

}
