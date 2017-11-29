package scroll.internal

import java.lang.reflect.Method

import scroll.internal.errors.SCROLLErrors.InvocationError

/**
  * This Trait specifies a general interface for reflectively invoking methods.
  */
trait Dispatchable {
  /**
    * For empty argument list dispatch.
    *
    * @param on the instance to dispatch the given method m on
    * @param m  the method to dispatch
    * @tparam E the return type of method m
    * @return the resulting return value of the method invocation or an appropriate error
    */
  def dispatch[E](on: Any, m: Method): Either[InvocationError, E]

  /**
    * For multi-argument dispatch.
    *
    * @param on   the instance to dispatch the given method m on
    * @param m    the method to dispatch
    * @param args the arguments to pass to method m
    * @tparam E the return type of method m
    * @tparam A the type of the argument values
    * @return the resulting return value of the method invocation or an appropriate error
    */
  def dispatch[E, A](on: Any, m: Method, args: Seq[A]): Either[InvocationError, E]
}