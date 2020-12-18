package scroll.examples

import scroll.internal.compartment.impl.Compartment
import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.DispatchQuery.Bypassing

object APICallsExample {

  class APICalls extends Compartment {

    case class API() {
      def callA(): Unit = {
        println("Call A is correct.")
      }

      def callB(): Unit = {
        println("Call B is a mess somehow.")
      }

      def callC(): Unit = {
        println("Call C is correct.")
      }
    }

    case class MyApp() {
      private val api = API() play FixedAPI()

      def run(): Unit = {
        api.callA()

        api.callB()

        {
          implicit val dd: DispatchQuery = Bypassing(_.isInstanceOf[FixedAPI])
          val _ = api.callC()
        }
      }
    }

    case class FixedAPI() {
      def callB(): Unit = {
        println("Call B is fixed now. :-)")
      }

      def callC(): Unit = {
        println("Call C is changed too. :-(")
      }
    }

  }

  def main(args: Array[String]): Unit = {
    new APICalls {
      MyApp().run()
    }
  }
}
