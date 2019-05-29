import sbt.Keys._
import sbt._

import org.scalastyle.sbt.ScalastylePlugin.autoImport._
import wartremover.WartRemover.autoImport._
import org.wartremover.contrib.ContribWarts.autoImport._
// TODO: re-enable after a version for Scala 2.13.0-RC2 is available
// import org.danielnixon.extrawarts.ExtraWart

object Linting extends Linting

trait Linting {

  val lib = Dependencies

  val scalastyleConfiguration = Seq(
    scalastyleFailOnError := true,
    test in Test := test.in(Test)
      .dependsOn(scalastyle.in(Test).toTask(""))
      .dependsOn(scalastyle.in(Compile).toTask(""))
      .value
  )

  val stdWarts = Warts.allBut(
    Wart.Any,
    Wart.AnyVal,
    Wart.AsInstanceOf,
    Wart.DefaultArguments,
    Wart.Equals,
    Wart.ImplicitConversion,
    Wart.ImplicitParameter,
    Wart.IsInstanceOf,
    Wart.MutableDataStructures,
    Wart.NonUnitStatements,
    Wart.Nothing,
    Wart.Null,
    Wart.Overloading,
    Wart.Recursion,
    Wart.Return,
    Wart.Throw,
    Wart.ToString,
    // TODO: could be fixed
    Wart.StringPlusAny,
    Wart.Var,
    Wart.While
  )

  val contribWarts = Seq(
    ContribWart.Apply,
    // ContribWart.ExposedTuples,
    ContribWart.NoNeedForMonad,
    ContribWart.MissingOverride,
    ContribWart.OldTime,
    ContribWart.RefinedClasstag,
    ContribWart.SealedCaseClass,
    ContribWart.SomeApply,
    ContribWart.UnintendedLaziness
  )

  // TODO: re-enable after a version for Scala 2.13.0-RC2 is available
  // val extraWarts = Seq(
  //   ExtraWart.EnumerationPartial,
  //   ExtraWart.FutureObject,
  //   ExtraWart.GenMapLikePartial,
  //   ExtraWart.GenTraversableLikeOps,
  //   ExtraWart.GenTraversableOnceOps,
  //   ExtraWart.ScalaGlobalExecutionContext,
  //   ExtraWart.StringOpsPartial,
  //   ExtraWart.ThrowablePartial,
  //   ExtraWart.TraversableOnceOps
  // )

  val wartremoverConfiguration = Seq(
    wartremoverExcluded += baseDirectory.value / "src" / "main" / "scala" / "scroll" / "internal" / "formal",
    wartremoverErrors ++= stdWarts ++ contribWarts
    // TODO: re-enable after a version for Scala 2.13.0-RC2 is available
    // ++ extraWarts
  )

  // TODO: re-enable after a version for Scala 2.13.0-RC2 is available
  // val linterConfiguration = Seq(
  //   addCompilerPlugin(lib.linter)
  // )

  val staticAnalysis =
    scalastyleConfiguration ++
      wartremoverConfiguration
  // TODO: re-enable after a version for Scala 2.13.0-RC2 is available
  // ++ linterConfiguration

}
