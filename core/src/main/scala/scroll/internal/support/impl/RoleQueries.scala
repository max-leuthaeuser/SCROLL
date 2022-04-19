package scroll.internal.support.impl

import scroll.internal.errors.SCROLLErrors.TypeError
import scroll.internal.errors.SCROLLErrors.TypeNotFound
import scroll.internal.graph.RoleGraphProxyApi
import scroll.internal.support.RoleQueriesApi

import scala.reflect.ClassTag
import scala.reflect.classTag

class RoleQueries(private[this] val roleGraph: RoleGraphProxyApi) extends RoleQueriesApi {

  import scroll.internal.support.impl.QueryStrategies._

  private[this] def safeReturn[T](seq: Seq[T], tpe: Class[?]): Either[TypeError, Seq[T]] =
    seq match {
      case Nil => Left(TypeNotFound(tpe))
      case s   => Right(s)
    }

  private[this] def safeReturnHead[T](seq: Seq[T], tpe: Class[?]): Either[TypeError, T] =
    safeReturn(seq, tpe).fold(
      l => Left(l),
      { case head +: _ =>
        Right(head)
      }
    )

  override def all[T <: AnyRef: ClassTag](matcher: RoleQueryStrategy = MatchAny()): Seq[T] =
    roleGraph.plays.allPlayers.collect { case p: T if matcher.matches(p) => p }

  override def all[T <: AnyRef: ClassTag](matcher: T => Boolean): Seq[T] =
    roleGraph.plays.allPlayers.collect { case p: T if matcher(p) => p }

  override def one[T <: AnyRef: ClassTag](
    matcher: RoleQueryStrategy = MatchAny()
  ): Either[TypeError, T] = safeReturnHead(all[T](matcher), classTag[T].runtimeClass)

  override def one[T <: AnyRef: ClassTag](matcher: T => Boolean): Either[TypeError, T] =
    safeReturnHead(all[T](matcher), classTag[T].runtimeClass)

}
