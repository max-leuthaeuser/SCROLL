package scroll.internal.dispatch.impl

import scroll.internal.dispatch.Dispatchable
import scroll.internal.errors.SCROLLErrors.IllegalRoleInvocationDispatch
import scroll.internal.errors.SCROLLErrors.InvocationError
import scroll.internal.util.ReflectiveHelper

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import scala.util.Failure
import scala.util.Success
import scala.util.Try

/** Trait handling the actual dispatching of role methods.
  */
trait SCROLLDispatchable extends Dispatchable {

  override def dispatch[E](on: AnyRef, m: Method, args: Seq[Any]): Either[InvocationError, E] = {
    require(null != on)
    require(null != m)
    require(null != args)
    Try(ReflectiveHelper.resultOf[E](on, m, args)) match {
      case Success(s)                              => Right(s)
      case Failure(exc: InvocationTargetException) => throw exc.getTargetException
      case Failure(_) => Left(IllegalRoleInvocationDispatch(on.toString, m.getName, args))
    }
  }

}
