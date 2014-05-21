package examples

import internal.dispatch.DispatchDescription._
import internal.dispatch.DispatchRule._
import internal.dispatch.Statement._
import util.TreeString._

object DispatchDescriptionExample extends App
{
  val c = When {
    () => true
  } Dispatch(
    In("ClassA").With("RoleA")(
      Then("RoleA.m1 before ClassA.m1"),
      Then("RoleA.m2 after ClassA.m2")
    ),
    In("ClassB").With("*")(
      Then("always replace ClassB.m1"),
      Then("never replace ClassB.m2")
    ))

  println(c.treeString)
  println(c.priorities.treeString)
}