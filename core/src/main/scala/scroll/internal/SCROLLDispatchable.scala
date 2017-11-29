package scroll.internal

import java.lang.reflect.Method

import scroll.internal.errors.SCROLLErrors.{IllegalRoleInvocationMultipleDispatch, IllegalRoleInvocationSingleDispatch, InvocationError}
import scroll.internal.util.ReflectiveHelper

import scala.util.{Failure, Success, Try}

/**
  * Trait handling the actual dispatching of role methods.
  */
trait SCROLLDispatchable extends Dispatchable {
  override def dispatch[E](on: Any, m: Method): Either[InvocationError, E] = {
    require(null != on)
    require(null != m)
    Try(ReflectiveHelper.resultOf[E](on, m)) match {
      case Success(s) => Right(s)
      case Failure(_) => Left(IllegalRoleInvocationSingleDispatch(on.toString, m.getName))
    }
  }

  override def dispatch[E, A](on: Any, m: Method, args: Seq[A]): Either[InvocationError, E] = {
    require(null != on)
    require(null != m)
    require(null != args)
    Try(ReflectiveHelper.resultOf[E](on, m, args.map(_.asInstanceOf[Object]))) match {
      case Success(s) => Right(s)
      case Failure(_) => Left(IllegalRoleInvocationMultipleDispatch(on.toString, m.getName, args))
    }
  }

}