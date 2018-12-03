package scroll.internal

import java.lang.reflect.Method

import scroll.internal.errors.SCROLLErrors.InvocationError

/**
  * This Trait specifies a general interface for reflectively invoking methods.
  */
trait Dispatchable {
  /**
    * For reflective dispatch.
    *
    * @param on   the instance to dispatch the given method m on
    * @param m    the method to dispatch
    * @param args the arguments to pass to method m
    * @tparam E the return type of method m
    * @return the resulting return value of the method invocation or an appropriate error
    */
  def dispatch[E](on: AnyRef, m: Method, args: Any*): Either[InvocationError, E]

}
