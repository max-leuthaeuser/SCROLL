import sbt.Keys._
import sbt._

import org.scalastyle.sbt.ScalastylePlugin.autoImport._
import wartremover.WartRemover.autoImport._
import org.wartremover.contrib.ContribWarts.autoImport._
import org.danielnixon.extrawarts.ExtraWart

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
    Wart.FinalCaseClass,
    Wart.ImplicitConversion,
    Wart.ImplicitParameter,
    Wart.IsInstanceOf,
    Wart.LeakingSealed,
    Wart.MutableDataStructures,
    Wart.NonUnitStatements,
    Wart.Nothing,
    Wart.Null,
    Wart.Overloading,
    Wart.Recursion,
    Wart.Return,
    Wart.StringPlusAny,
    Wart.Throw,
    Wart.ToString,
    Wart.TraversableOps,
    Wart.Var,
    Wart.While
  )

  val contribWarts = Seq(
    // ContribWart.Apply,
    // ContribWart.ExposedTuples,
    ContribWart.MissingOverride,
    ContribWart.OldTime,
    ContribWart.RefinedClasstag,
    // ContribWart.SealedCaseClass,
    ContribWart.SomeApply,
    // ContribWart.SymbolicName,
    ContribWart.UnintendedLaziness
    // ContribWart.UnsafeInheritance
  )

  val extraWarts = Seq(
    ExtraWart.EnumerationPartial,
    ExtraWart.FutureObject,
    // ExtraWart.GenMapLikePartial,
    // ExtraWart.GenTraversableLikeOps,
    ExtraWart.GenTraversableOnceOps,
    ExtraWart.ScalaGlobalExecutionContext,
    ExtraWart.StringOpsPartial,
    ExtraWart.ThrowablePartial,
    ExtraWart.TraversableOnceOps
  )

  val wartremoverConfiguration = Seq(
    wartremoverErrors ++= stdWarts ++ contribWarts ++ extraWarts
  )

  val linterConfiguration = Seq(
    addCompilerPlugin(lib.linter),
    scalacOptions += "-P:linter:enable-only:" +
      "AssigningOptionToNull+" +
      "AvoidOptionCollectionSize+" +
      "AvoidOptionMethod+" +
      "AvoidOptionStringSize+" +
      "BigDecimalNumberFormat+" +
      "BigDecimalPrecisionLoss+" +
      "CloseSourceFile+" +
      // "ContainsTypeMismatch+" +
      "DecomposingEmptyCollection+" +
      "DivideByOne+" +
      "DivideByZero+" +
      "DuplicateIfBranches+" +
      "DuplicateKeyInMap+" +
      "EmptyStringInterpolator+" +
      "FilterFirstThenSort+" +
      "FloatingPointNumericRange+" +
      "FuncFirstThenMap+" +
      "IdenticalCaseBodies+" +
      "IdenticalCaseConditions+" +
      "IdenticalIfCondition+" +
      "IdenticalIfElseCondition+" +
      "IdenticalStatements+" +
      "IfDoWhile+" +
      "IndexingWithNegativeNumber+" +
      "InefficientUseOfListSize+" +
      "IntDivisionAssignedToFloat+" +
      "InvalidParamToRandomNextInt+" +
      "InvalidStringConversion+" +
      "InvalidStringFormat+" +
      "InvariantCondition+" +
      "InvariantExtrema+" +
      "InvariantReturn+" +
      "JavaConverters+" +
      "LikelyIndexOutOfBounds+" +
      "MalformedSwap+" +
      // "MergeMaps+" +
      "MergeNestedIfs+" +
      "ModuloByOne+" +
      // "NumberInstanceOf+" +
      "OnceEvaluatedStatementsInBlockReturningFunction+" +
      "OperationAlwaysProducesZero+" +
      "OptionOfOption+" +
      "PassPartialFunctionDirectly+" +
      "PatternMatchConstant+" +
      "PossibleLossOfPrecision+" +
      "PreferIfToBooleanMatch+" +
      "ProducesEmptyCollection+" +
      "ReflexiveAssignment+" +
      "ReflexiveComparison+" +
      "RegexWarning+" +
      "StringMultiplicationByNonPositive+" +
      "SuspiciousMatches+" +
      "SuspiciousPow+" +
      "TransformNotMap+" +
      "TypeToType+" +
      "UndesirableTypeInference+" +
      "UnextendedSealedTrait+" +
      "UnitImplicitOrdering+" +
      "UnlikelyEquality+" +
      "UnlikelyToString+" +
      "UnnecessaryMethodCall+" +
      "UnnecessaryReturn+" +
      "UnnecessaryStringIsEmpty+" +
      "UnnecessaryStringNonEmpty+" +
      "UnsafeAbs+" +
      "UnthrownException+" +
      "UnusedForLoopIteratorValue+" +
      "UnusedParameter+" +
      "UseAbsNotSqrtSquare+" +
      "UseCbrt+" +
      "UseConditionDirectly+" +
      "UseContainsNotExistsEquals+" +
      "UseCountNotFilterLength+" +
      "UseExistsNotCountCompare+" +
      "UseExistsNotFilterIsEmpty+" +
      "UseExistsNotFindIsDefined+" +
      "UseExp+" +
      "UseExpm1+" +
      "UseFilterNotFlatMap+" +
      "UseFindNotFilterHead+" +
      "UseFlattenNotFilterOption+" +
      "UseFuncNotFold+" +
      "UseFuncNotReduce+" +
      "UseFuncNotReverse+" +
      "UseGetOrElseNotPatMatch+" +
      "UseGetOrElseOnOption+" +
      "UseHeadNotApply+" +
      "UseHeadOptionNotIf+" +
      "UseHypot+" +
      "UseIfExpression+" +
      "UseInitNotReverseTailReverse+" +
      "UseIsNanNotNanComparison+" +
      "UseIsNanNotSelfComparison+" +
      "UseLastNotApply+" +
      "UseLastNotReverseHead+" +
      "UseLastOptionNotIf+" +
      "UseLog10+" +
      "UseLog1p+" +
      "UseMapNotFlatMap+" +
      "UseMinOrMaxNotSort+" +
      "UseOptionExistsNotPatMatch+" +
      "UseOptionFlatMapNotPatMatch+" +
      "UseOptionFlattenNotPatMatch+" +
      "UseOptionForallNotPatMatch+" +
      "UseOptionForeachNotPatMatch+" +
      "UseOptionGetOrElse+" +
      "UseOptionIsDefinedNotPatMatch+" +
      "UseOptionIsEmptyNotPatMatch+" +
      "UseOptionMapNotPatMatch+" +
      "UseOptionOrNull+" +
      "UseOrElseNotPatMatch+" +
      "UseQuantifierFuncNotFold+" +
      "UseSignum+" +
      "UseSqrt+" +
      "UseTakeRightNotReverseTakeReverse+" +
      "UseUntilNotToMinusOne+" +
      "UseZipWithIndexNotZipIndices+" +
      "VariableAssignedUnusedValue+" +
      "WrapNullWithOption+" +
      "YodaConditions+" +
      "ZeroDivideBy"
  )

  val staticAnalysis =
    scalastyleConfiguration ++
      wartremoverConfiguration ++
      linterConfiguration

}
