package scroll.internal.support

import scroll.internal.ICompartment
import scroll.internal.errors.SCROLLErrors.TypeError

import scala.reflect.ClassTag
import scala.reflect.classTag

trait RoleQueries {
  self: ICompartment =>

  /**
    * Query the role playing graph for all player instances that do conform to the given matcher.
    *
    * @param matcher the matcher that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return all player instances as Seq, that do conform to the given matcher
    */
  def all[T <: AnyRef : ClassTag](matcher: RoleQueryStrategy = MatchAny()): Seq[T] =
    plays.allPlayers.collect { case p: T if matcher.matches(p) => p }

  /**
    * Query the role playing graph for all player instances that do conform to the given function.
    *
    * @param matcher the matching function that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return all player instances as Seq, that do conform to the given matcher
    */
  def all[T <: AnyRef : ClassTag](matcher: T => Boolean): Seq[T] =
    plays.allPlayers.collect { case p: T if matcher(p) => p }

  /**
    * Query the role playing graph for all player instances that do conform to the given matcher and return the first found.
    *
    * @param matcher the matcher that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return the first player instance, that does conform to the given matcher or an appropriate error
    */
  def one[T <: AnyRef : ClassTag](matcher: RoleQueryStrategy = MatchAny()): Either[TypeError, T] =
    safeReturnHead(all[T](matcher), classTag[T].toString)

  /**
    * Query the role playing graph for all player instances that do conform to the given function and return the first found.
    *
    * @param matcher the matching function that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return the first player instances, that do conform to the given matcher or an appropriate error
    */
  def one[T <: AnyRef : ClassTag](matcher: T => Boolean): Either[TypeError, T] = safeReturnHead(all[T](matcher), classTag[T].toString)

}
