package scroll.internal

import java.lang.reflect.Method

import scroll.internal.errors.SCROLLErrors.{IllegalRoleInvocationDispatch, InvocationError}
import scroll.internal.util.ReflectiveHelper

import scala.util.{Failure, Success, Try}

/**
  * Trait handling the actual dispatching of role methods.
  */
trait SCROLLDispatchable extends Dispatchable {
  override def dispatch[E](on: AnyRef, m: Method, args: Any*): Either[InvocationError, E] = {
    require(null != on)
    require(null != m)
    require(null != args)
    Try(ReflectiveHelper.resultOf[E](on, m, args.map(_.asInstanceOf[Object]))) match {
      case Success(s) => Right(s)
      case Failure(_) => Left(IllegalRoleInvocationDispatch(on.toString, m.getName, args))
    }
  }

}