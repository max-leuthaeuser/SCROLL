package scroll.examples

import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery
import scroll.internal.support.DispatchQuery.Bypassing
import scroll.internal.util.Log.info

object APICallsExample extends App {

  class APICalls extends Compartment {

    case class API() {
      def callA(): Unit = {
        info("Call A is correct.")
      }

      def callB(): Unit = {
        info("Call B is a mess somehow.")
      }

      def callC(): Unit = {
        info("Call C is correct.")
      }
    }

    case class MyApp() {
      implicit var dd: DispatchQuery = DispatchQuery.empty
      val api = API() play FixedAPI()

      def run(): Unit = {
        api.callA()

        api.callB()

        dd = Bypassing(_.isInstanceOf[FixedAPI])
        val _ = api.callC()
      }
    }

    case class FixedAPI() {
      def callB(): Unit = {
        info("Call B is fixed now. :-)")
      }

      def callC(): Unit = {
        info("Call C is changed too. :-(")
      }
    }

  }

  new APICalls {
    MyApp().run()
  }
}
