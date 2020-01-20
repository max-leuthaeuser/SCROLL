SCROLL
======
*SCala ROLes Language*

[![Build Status](https://travis-ci.com/max-leuthaeuser/SCROLL.svg)](https://travis-ci.com/max-leuthaeuser/SCROLL) [![Codacy Badge](https://api.codacy.com/project/badge/0021c87e1b824c2f8a58b81406d5db48)](https://www.codacy.com/app/max-leuthaeuser/SCROLL) [![codecov](https://codecov.io/gh/max-leuthaeuser/SCROLL/branch/master/graph/badge.svg)](https://codecov.io/gh/max-leuthaeuser/SCROLL)

## Introduction ##

*SCROLL* is an embedded method-call interception domain-specific language (DSL) tailored to the features needed to implement roles and resolve the ambiguities arising with regard to dynamic dispatch. The library approach together with an implementation with Scala was chosen for mainly the following reasons: it allows focusing on role semantics, supports a customizable, dynamic dispatch at runtime, and allows for a terse, flexible representation. No additional tooling (like a custom lexer, parser or compiler) is needed to execute the *SCROLL* meta-object protocol (MOP). It is purely embedded in the host language, thus uses the standard Scala compiler to generate Java Virtual Machine bytecode. With that, the implementation is reasonable small (∼1400 lines of code) and maintainable. The programming interface with Scala's  exible syntax holds the property of being easily readable, even to inexperienced users. 

See the [wiki](https://github.com/max-leuthaeuser/SCROLL/wiki) for further information.

## Basic Implementation Concepts ##

![Basics](https://github.com/max-leuthaeuser/SCROLL/wiki/img/basics.png)

To provide a DSL for the pure embedding of roles in structured contexts, *SCROLL* requires the basic implementation concepts from the host language shown in the image above:

 - **Compiler rewrites**: A concept for compiler rewrites for method calls, functions calls, and attribute access is required. It hands over calls to the library for  nding behavior and structure that is not natively available at the player. This can be seen as a compiler-supported variant of method-call interception.
 
 - **Implicit conversions**: For aggregating the compound object from the core and its roles, and for exposing the *SCROLL* MOP API, implicit conversions are needed. An implicit conversion from type ```S``` to type ```T``` is defined by an implicit value which has the function type ```S => T```, or by an implicit method convertible to a value of that type. Implicit conversions are applied in two situations: i) If an expression ```e``` is of type ```S```, and ```S``` does not conform to the expression's expected type ```T```, and ii) in a selection ```e.m``` with ```e``` of type ```S```, if the selector ```m``` does not denote a member of ```S```. In the first case, a conversion ```c``` is searched for which is applicable to ```e``` and whose result type conforms to ```T```. In the second case, a conversion ```c``` is searched for which is applicable to ```e``` and whose result contains a member named ```m```.
 
 - **Definition table for the plays relationship**: The relationships between each individual player and its roles need to be stored. A definition table holds all kinds of program components, whose attributes are created by declaration: types, variables, methods, functions, and parameters. In *SCROLL*, a definition table for roles is implemented with a graph-based data structure, but it may be implemented with tables, maps, or lists as well.
  
## Example ##

```scala
// A Natural type, the player:
class Person(val firstName: String)

val peter = new Person("Peter")

// A new context, a Compartment:
new Compartment {
  // A Role type, here used as dynamic extension:
  class PersonExtension(val lastName: String) {
    def fullName(): String = {
      // the +-operator used as base call:
      val first: String = +this firstName
      val last: String = lastName
      first + " " + last
    }
  }

  val name: String = peter play new PersonExtension("Meier") fullName()
  println(name)
}
```

A more elaborated example can be found [here](https://github.com/max-leuthaeuser/SCROLL/wiki/The-Bank-Example-%28Overview%29) and [here](https://github.com/max-leuthaeuser/SCROLL/wiki/The-Bank-Example-%28Advanced-Role-features%29).

You can find even more examples [here](https://github.com/max-leuthaeuser/SCROLL/tree/master/examples/src/main/scala/scroll/examples).

You also might want to check the [tests](https://github.com/max-leuthaeuser/SCROLL/tree/master/tests/src/test/scala/scroll/tests).

## Edit and develop ##

See the [developer wiki](https://github.com/max-leuthaeuser/SCROLL/wiki/Developers) for further information.

## Use the library ##

See the [user wiki](https://github.com/max-leuthaeuser/SCROLL/wiki/Users) for further information.

## Publications ##

[Dissertation][diss]


[gen-eclipse]: https://github.com/typesafehub/sbteclipse
[scala-dynamic]: http://www.scala-lang.org/api/current/#scala.Dynamic
[BankExample]: https://github.com/max-leuthaeuser/SCROLL/blob/master/examples/src/main/scala/scroll/examples/BankExample.scala
[scaladoc]: http://max-leuthaeuser.github.io/SCROLL
[diss]: http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-227624
