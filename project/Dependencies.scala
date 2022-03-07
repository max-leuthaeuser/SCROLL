import sbt.CrossVersion
import sbt.stringToOrganization

object Dependencies extends Dependencies

trait Dependencies {

  object v {
    val scalaVersion     = "3.1.1"
    val akkaVersion      = "2.6.18"
    val scalatestVersion = "3.2.11"
    val chocoVersion     = "4.10.8"
    val guavaVersion     = "31.1-jre"
    val emfcommonVersion = "2.23.0"
    val emfecoreVersion  = "2.25.0"
    val umlVersion       = "3.1.0.v201006071150"
    val jvm              = "1.8"
  }

  val coreDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor"             % v.akkaVersion,
    "com.google.guava"   % "guava"                  % v.guavaVersion,
    "org.choco-solver"   % "choco-solver"           % v.chocoVersion,
    "org.eclipse.emf"    % "org.eclipse.emf.common" % v.emfcommonVersion,
    "org.eclipse.emf"    % "org.eclipse.emf.ecore"  % v.emfecoreVersion,
    "org.eclipse.uml2"   % "org.eclipse.uml2.uml"   % v.umlVersion
  )

  val coreDependenciesOverrides = Seq(
    "org.eclipse.emf" % "org.eclipse.emf.common" % v.emfcommonVersion,
    "org.eclipse.emf" % "org.eclipse.emf.ecore"  % v.emfecoreVersion
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest"                % v.scalatestVersion % "test",
    "org.scalatest" %% "scalatest-core"           % v.scalatestVersion % "test",
    "org.scalatest" %% "scalatest-shouldmatchers" % v.scalatestVersion % "test"
  )

}
