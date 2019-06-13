package scroll.tests.parameterized

import org.scalatest.prop.PropertyChecks
import scroll.tests.AbstractSCROLLTest

abstract class AbstractParameterizedSCROLLTest extends AbstractSCROLLTest with PropertyChecks {
	protected val PARAMS =
		Table(
			("cached", "checkForCycles"),
			(true, true),
			(true, false),
			(false, true),
			(false, false)
		)
}
