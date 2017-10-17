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

  implicit class MultiPlayer[T <: AnyRef : ClassTag](val wrapped: T) extends Dynamic with SCROLLDispatch {
    /**
      * Applies lifting to Player
      *
      * @return an lifted Player instance with the calling object as wrapped.
      */
    def unary_+ : MultiPlayer[T] = this

    /**
      * Returns the player of this player instance if this is a role, or this itself.
      *
      * @param dispatchQuery provide this to sort the resulting instances if a role instance is played by multiple core objects
      * @return the player of this player instance if this is a role, or this itself or an appropriate error
      */
    def player(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[TypeError, Any] = dispatchQuery.filter(getCoreFor(this)) match {
      case elem :: Nil => Right(elem)
      case l: Seq[T] => Right(l.head)
      case _ => Left(TypeNotFound(this.getClass.toString))
    }

    /**
      * Adds a play relation between core and role.
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def play[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = {
      wrapped match {
        case p: MultiPlayer[_] => addPlaysRelation[T, R](p.wrapped.asInstanceOf[T], role)
        case p: Any => addPlaysRelation[T, R](p.asInstanceOf[T], role)
        case _ => // do nothing
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
      * Adds a play relation between core and role but always returns the player instance.
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def playing[R <: AnyRef : ClassTag](role: R): T = play(role).wrapped

    /**
      * Alias for [[Player.playing]].
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def <=>[R <: AnyRef : ClassTag](role: R): T = playing(role)

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

    /**
      * Transfers a role to another player.
      *
      * @tparam R type of role
      * @param role the role to transfer
      */
    def transfer[R <: AnyRef : ClassTag](role: R) = new {
      def to[P <: AnyRef : ClassTag](player: P): Unit = {
        transferRole[T, P, R](wrapped, player, role)
      }
    }

    /**
      * Checks of this Player is playing a role of the given type.
      */
    def isPlaying[E: ClassTag]: Boolean = plays.getRoles(wrapped).exists(ReflectiveHelper.is[E])

    /**
      * Checks of this Player has an extension of the given type.
      * Alias for [[Player.isPlaying]].
      */
    def hasExtension[E: ClassTag]: Boolean = isPlaying[E]

    def applyDynamic[E, A](name: String)(args: A*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] = {
      val core = dispatchQuery.filter(getCoreFor(wrapped)).head
      val anys = dispatchQuery.filter(Seq(core, wrapped) ++ plays.getRoles(core))
      val results: mutable.ListBuffer[Either[SCROLLError, E]] = mutable.ListBuffer.empty
      anys.foreach(r => {
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
      val anys = dispatchQuery.filter(Seq(core, wrapped) ++ plays.getRoles(core))
      val results: mutable.ListBuffer[Either[SCROLLError, E]] = mutable.ListBuffer.empty
      anys.filter(ReflectiveHelper.hasMember(_, name)).foreach(r => {
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
      val anys = dispatchQuery.filter(Seq(core, wrapped) ++ plays.getRoles(core))
      anys.filter(ReflectiveHelper.hasMember(_, name)).foreach(ReflectiveHelper.setPropertyOf(_, name, value))
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