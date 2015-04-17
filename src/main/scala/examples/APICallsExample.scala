package examples

import internal.Compartment
import internal.DispatchQuery._
import internal.util.Log.info

object APICallsExample extends App {

  class APICalls extends Compartment {

    case class API() {
      def callA() {
        info("Call A is correct.")
      }

      def callB() {
        info("Call B is a mess somehow.")
      }

      def callC() {
        info("Call C is correct.")
      }
    }

    case class MyApp() {
      val api = API() play FixedAPI()

      def run() {
        api.callA()

        api.callB()

        implicit var dd = From(_.isInstanceOf[API]).
          To(_.isInstanceOf[FixedAPI]).
          Through(_ => true).
          Bypassing(_.isInstanceOf[FixedAPI])
        api.callC()
      }
    }

    case class FixedAPI() {
      def callB() {
        info("Call B is fixed now. :-)")
      }

      def callC() {
        info("Call C is changed too. :-(")
      }
    }

  }

  new APICalls {
    MyApp().run()
  }
}
