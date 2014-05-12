package examples

import internal.dispatch.DispatchDescription._
import internal.dispatch.DispatchRule._
import internal.dispatch.Statement._

object DispatchDescriptionExample extends App
{
  val c = When {
    () => true
  } Dispatch(
    In("ClassA").With("RoleA")(
      invoke {
        "RoleA.m1 before ClassA.m1"
      },
      invoke {
        "RoleA.m2 after ClassA.m2"
      }
    ),
    In("ClassB").With("*")(
      invoke {
        "always replace ClassB.m1"
      },
      invoke {
        "never replace ClassB.m2"
      }
    )
    )

  println(c)
}