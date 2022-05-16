package scroll.tests.parameterized

import org.scalatest.prop.TableDrivenPropertyChecks
import scroll.tests.AbstractSCROLLTest

abstract class AbstractParameterizedSCROLLTest extends AbstractSCROLLTest with TableDrivenPropertyChecks {

  protected val PARAMS =
    Table(("cached", "checkForCycles"), (true, true), (true, false), (false, true), (false, false))

}
