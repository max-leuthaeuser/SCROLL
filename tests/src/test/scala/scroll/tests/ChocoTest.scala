package scroll.tests

import org.chocosolver.solver.Solver
import org.chocosolver.solver.constraints.IntConstraintFactory
import org.chocosolver.solver.search.strategy.IntStrategyFactory
import org.chocosolver.solver.trace.Chatterbox
import org.chocosolver.solver.variables.VariableFactory
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

class ChocoTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for ChocoSolver.")

  feature("Specifying a Constraint programming problem") {
    scenario("Simple constraint programming problem") {
      // 1. Create a Solver
      val solver = new Solver("my first problem")
      // 2. Create variables through the variable factory
      val x = VariableFactory.bounded("X", 0, 5, solver)
      val y = VariableFactory.bounded("Y", 0, 5, solver)
      // 3. Create and post constraints by using constraint factories
      solver.post(IntConstraintFactory.arithm(x, "+", y, "<", 5))
      // 4. Define the search strategy
      solver.set(IntStrategyFactory.lexico_LB(x, y))
      // 5. Launch the resolution process
      solver.findSolution() shouldBe true
      //6. Print search statistics
      // Chatterbox.printStatistics(solver)
      val solution = solver.getSolutionRecorder.getLastSolution
      // println(solution.toString)
      solution.getIntVal(x) shouldBe 0
      solution.getIntVal(y) shouldBe 0
    }

  }
}
