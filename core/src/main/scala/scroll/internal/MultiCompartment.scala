package scroll.internal

import scroll.internal.errors.SCROLLErrors._
import scroll.internal.support._
import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable
import scala.reflect.ClassTag

/**
  * This Trait allows for implementing an objectified collaboration with a limited number of participating roles and a fixed scope.
  * In contrast to the normal Compartment, in case of ambiguities all role methods will be called in sequence.
  */
trait MultiCompartment extends Compartment {

  implicit class MultiPlayer[T <: AnyRef : ClassTag](val wrapped: T) extends Dynamic with SCROLLDispatchable {
    /**
      * Applies lifting to Player
      *
      * @return an lifted Player instance with the calling object as wrapped.
      */
    def unary_+ : MultiPlayer[T] = this

    /**
      * Adds a play relation between core and role.
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def play[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = {
      require(null != role)
      wrapped match {
        case p: MultiPlayer[_] => addPlaysRelation[T, R](p.wrapped.asInstanceOf[T], role)
        case p: AnyRef => addPlaysRelation[T, R](p.asInstanceOf[T], role)
        case p => throw new RuntimeException(s"Only instances of 'Player' or 'AnyRef' are allowed to play roles! You tried it with '$p'.")
      }
      this
    }

    /**
      * Alias for [[Player.play]].
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def <+>[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = play(role)

    /**
      * Removes the play relation between core and role.
      *
      * @param role the role that should be removed
      * @return this
      */
    def drop[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = {
      removePlaysRelation[T, R](wrapped, role)
      this
    }

    /**
      * Alias for [[Player.drop]].
      *
      * @param role the role that should be removed
      * @return this
      */
    def <->[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = drop(role)

    def applyDynamic[E, A](name: String)(args: A*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] = {
      val core = dispatchQuery.filter(getCoreFor(wrapped)).head
      val results = mutable.ListBuffer.empty[Either[SCROLLError, E]]
      dispatchQuery.filter(plays.getRoles(core)).foreach(r => {
        ReflectiveHelper.findMethod(r, name, args).foreach(fm => {
          args match {
            case Nil => results += dispatch(r, fm)
            case _ => results += dispatch(r, fm, args)
          }
        })
      })
      if (results.isEmpty) {
        // give up
        Left(RoleNotFound(core.toString, name, args))
      }
      else {
        Right(results)
      }
    }

    def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] = {
      val core = dispatchQuery.filter(getCoreFor(wrapped)).head
      val results = mutable.ListBuffer.empty[Either[SCROLLError, E]]
      dispatchQuery.filter(plays.getRoles(core)).filter(ReflectiveHelper.hasMember(_, name)).foreach(r => {
        results += ReflectiveHelper.propertyOf(r, name)
      })
      if (results.isEmpty) {
        // give up
        Left(RoleNotFound(core.toString, name, Seq.empty))
      }
      else {
        Right(results)
      }
    }

    def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Unit = {
      val core = dispatchQuery.filter(getCoreFor(wrapped)).head
      dispatchQuery.filter(plays.getRoles(core)).filter(ReflectiveHelper.hasMember(_, name)).foreach(ReflectiveHelper.setPropertyOf(_, name, value))
    }


    override def equals(o: Any): Boolean = o match {
      case other: MultiPlayer[_] => getCoreFor(wrapped) == getCoreFor(other.wrapped)
      case other: Any => getCoreFor(wrapped) match {
        case Nil => false
        case p :: Nil => p == other
        case _ => false
      }
      case _ => false // default case
    }

    override def hashCode(): Int = wrapped.hashCode()
  }

}