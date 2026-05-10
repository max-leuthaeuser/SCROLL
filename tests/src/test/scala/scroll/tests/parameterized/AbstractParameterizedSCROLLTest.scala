package scroll.tests.parameterized

import org.scalatest.prop.TableDrivenPropertyChecks
import scroll.tests.AbstractSCROLLTest

abstract class AbstractParameterizedSCROLLTest extends AbstractSCROLLTest with TableDrivenPropertyChecks {

  protected val graphModes = Seq((true, true), (true, false), (false, true), (false, false))

  protected val PARAMS =
    Table(("cached", "checkForCycles"), graphModes*)

  protected val PARAM_PAIRS =
    Table(
      ("cachedA", "checkForCyclesA", "cachedB", "checkForCyclesB"),
      graphModes.flatMap { case (cachedA, checkForCyclesA) =>
        graphModes.map { case (cachedB, checkForCyclesB) =>
          (cachedA, checkForCyclesA, cachedB, checkForCyclesB)
        }
      }*
    )

}
