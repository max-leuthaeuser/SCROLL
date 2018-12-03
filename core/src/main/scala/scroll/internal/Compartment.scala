package scroll.internal

import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.errors.SCROLLErrors.SCROLLError
import scroll.internal.support.DispatchQuery
import scroll.internal.util.ReflectiveHelper

import scala.reflect.ClassTag

/**
  * This Trait allows for implementing an objectified collaboration with a limited number of participating roles and a fixed scope.
  *
  * ==Overview==
  * Roles are dependent on some sort of context. We call them compartments. A typical example of a compartment is a university,
  * which contains the roles Student and Teacher collaborating in Courses. Everything in SCROLL happens inside of Compartments
  * but roles (implemented as standard Scala classes) can be defined or imported from everywhere. Just mix in this Trait
  * into your own specific compartment class or create an anonymous instance.
  *
  * ==Example==
  * {{{
  * val player = new Player()
  * new Compartment {
  *   class RoleA
  *   class RoleB
  *
  *   player play new RoleA()
  *   player play new RoleB()
  *
  *   // call some behaviour
  * }
  * }}}
  */
trait Compartment extends ICompartment {

  override def newPlayer(obj: Object): Player[Object] = {
    require(null != obj)
    new Player(obj)
  }

  implicit class Player[T <: AnyRef : ClassTag](override val wrapped: T) extends IPlayer[T](wrapped) with SCROLLDynamic {

    override def unary_+ : Player[T] = this

    override def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    override def applyDynamic[E](name: String)(args: Any*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] =
      applyDispatchQuery(dispatchQuery, wrapped).view.map { r =>
        (r, ReflectiveHelper.findMethod(r, name, args))
      }.collectFirst {
        case (r, Some(m)) => (r, m)
      } match {
        case Some((r, fm)) => dispatch[E](r, fm, args: _*)
        case _ => Left(RoleNotFound(wrapped.toString, name, args))
      }


    override def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] =
      applyDispatchQuery(dispatchQuery, wrapped).view.find(ReflectiveHelper.hasMember(_, name)) match {
        case Some(r) => Right(ReflectiveHelper.propertyOf[E](r, name))
        case None => Left(RoleNotFound(wrapped.toString, name, Seq.empty))
      }

    override def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Unit =
      applyDispatchQuery(dispatchQuery, wrapped).view.
        find(ReflectiveHelper.hasMember(_, name)).
        foreach(ReflectiveHelper.setPropertyOf(_, name, value))

  }

}
