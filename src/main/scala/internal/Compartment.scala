package internal

// removes warnings by Eclipse about using implicit conversion

import scala.language.implicitConversions

import java.lang
import java.lang.reflect.Method
import reflect.runtime.universe._
import scala.collection.immutable.Queue
import annotations.Role
import graph.ScalaRoleGraph

// TODO: what happens if the same role is played multiple times from one player?
trait Compartment extends ReflectiveHelper {

  implicit class RoleQueryStrategy(name: String) {
    def matches(on: Any): Boolean = true

    def ==#[T](value: T) = new WithProperty(name, value)

    def ==>[T](value: T) = new WithResult(name, value)
  }

  case class *() extends RoleQueryStrategy("")

  case class WithProperty[T](name: String, value: T) extends RoleQueryStrategy(name) {
    override def matches(on: Any): Boolean = on.propertyOf[T](name) == value
  }

  case class WithResult[T](name: String, result: T) extends RoleQueryStrategy(name) {
    override def matches(on: Any): Boolean = on.resultOf[T](name) == result
  }

  private def isRole(value: Any): Boolean = {
    require(null != value)
    value.getClass.isAnnotationPresent(classOf[Role])
  }

  val plays = new ScalaRoleGraph()

  // declaring a is-part-of relation between compartments
  def partOf(other: Compartment) {
    require(null != other)
    plays.store ++= other.plays.store
  }

  // declaring a bidirectional is-part-of relation between compartment
  def union(other: Compartment): Compartment = {
    other.partOf(this)
    this.partOf(other)
    this
  }

  // removing is-part-of relation between compartments
  def notPartOf(other: Compartment) {
    require(null != other)
    other.plays.store.edges.toSeq.foreach(e => {
      plays.store -= e.value
    })
  }

  def all[T: WeakTypeTag](matcher: RoleQueryStrategy = *()): Seq[T] = {
    plays.store.nodes.toSeq.filter(_.value.is[T])
      .map(_.value.asInstanceOf[T]).filter(a => matcher.matches(getCoreFor(a)))
  }

  def all[T: WeakTypeTag](matcher: () => Boolean): Seq[T] = {
    plays.store.nodes.toSeq.filter(_.value.is[T])
      .map(_.value.asInstanceOf[T]).filter(_ => matcher())
  }

  private def safeReturn[T](seq: Seq[T], typeName: String): Seq[T] = seq.isEmpty match {
    case true => throw new RuntimeException(s"No player with type '$typeName' found!")
    case false => seq
  }

  def one[T: WeakTypeTag](matcher: RoleQueryStrategy = *()): T = safeReturn(all[T](matcher), weakTypeOf[T].toString).head

  def one[T: WeakTypeTag](matcher: () => Boolean): T = safeReturn(all[T](matcher), weakTypeOf[T].toString).head

  def addPlaysRelation(
                        core: Any,
                        role: Any) {
    require(isRole(role), "Argument for adding a role must be a role (you maybe want to add the @Role annotation).")
    plays.addBinding(core, role)
  }

  def removePlaysRelation(
                           core: Any,
                           role: Any) {
    require(isRole(role), "Argument for removing a role must be a role (you maybe want to add the @Role annotation).")
    plays.removeBinding(core, role)
  }

  def transferRole(
                    coreFrom: Any,
                    coreTo: Any,
                    role: Any) {
    require(null != coreFrom)
    require(null != coreTo)
    require(coreFrom != coreTo, "You can not transfer a role from itself.")
    require(isRole(role), "Argument for transferring a role must be a role (you maybe want to add the @Role annotation).")

    removePlaysRelation(coreFrom, role)
    addPlaysRelation(coreTo, role)
  }

  def transferRoles(
                     coreFrom: Any,
                     coreTo: Any,
                     roles: Set[Any]) {
    require(null != roles)
    roles.foreach(transferRole(coreFrom, coreTo, _))
  }

  def getCoreFor(role: Any): Any = {
    require(null != role)
    role match {
      case cur: RoleType[_] => getCoreFor(cur.role)
      case cur: PlayerType[_] => getCoreFor(cur.core)
      // default:
      case cur: Any => plays.store.get(cur).diPredecessors.toList match {
        case p :: Nil => getCoreFor(p.value)
        case Nil => cur
        case _ =>
      }
    }
  }

  trait DispatchType {
    // for single method dispatch
    def dispatch[E](
                     on: Any,
                     m: Method): E = {
      require(null != on)
      require(null != m)
      m.invoke(on, Array.empty[Object]: _*).asInstanceOf[E]
    }

    // for multi-method / multi-argument dispatch
    def dispatch[E, A](
                        on: Any,
                        m: Method,
                        args: Seq[A]): E = {
      require(null != on)
      require(null != m)
      require(null != args)
      val argTypes: Array[Class[_]] = m.getParameterTypes
      val actualArgs: Seq[Any] = args.zip(argTypes).map {
        case (arg: PlayerType[_], tpe: Class[_]) =>
          plays.getRoles(arg.core).find(_.getClass == tpe) match {
            case Some(curRole) => curRole
            // TODO: how to permit this?
            case None => throw new RuntimeException(s"No role for type '$tpe' found.")
          }
        case (arg: Double, tpe: Class[_]) => new lang.Double(arg.toDouble)
        case (arg@unchecked, tpe: Class[_]) => tpe.cast(arg)
      }
      // that looks funny:
      m.invoke(on, actualArgs.map {
        _.asInstanceOf[Object]
      }: _*).asInstanceOf[E]
    }

    protected def reorder(
                           anys: Queue[Any],
                           dispatchQuery: DispatchQuery): Queue[Any] = {
      require(null != anys)
      require(null != dispatchQuery)
      anys.filter(dispatchQuery.bypassing)
    }
  }

  implicit class RoleType[T](val role: T) extends Dynamic with DispatchType {
    def unary_- : RoleType[T] = this

    def applyDynamic[E, A](name: String)(args: A*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E = {
      val core = getCoreFor(role)
      val anys = reorder(Queue() ++ plays.getRoles(core) :+ role :+ core, dispatchQuery)
      anys.foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName == name).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })

      // otherwise give up
      throw new RuntimeException(s"No role with method '$name' found! (role: '$role')")
    }

    def applyDynamicNamed[E, A](name: String)(args: (String, A)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E = {
      val core = getCoreFor(role)
      val anys = reorder(Queue() ++ plays.getRoles(core) :+ role :+ core, dispatchQuery)
      anys.foreach(r => if (r.hasAttribute(name)) return r.propertyOf[E](name))

      // otherwise give up
      throw new RuntimeException(s"No role with value '$name' found! (core: '$core')")
    }

    def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty) {
      val core = getCoreFor(role)
      val anys = reorder(Queue() ++ plays.getRoles(core) :+ role :+ core, dispatchQuery)
      anys.foreach(r => if (r.hasAttribute(name)) return r.setPropertyOf(name, value))

      // otherwise give up
      throw new RuntimeException(s"No role with value '$name' found! (core: '$core')")
    }

    def isPlaying[E: WeakTypeTag]: Boolean = plays.getRoles(role)
      .find(r => r.getClass.getSimpleName == ReflectiveHelper.typeSimpleClassName(weakTypeOf[E])) match {
      case None => false
      case _ => true
    }

    // identity of roles: they don't have their own ID
    override def equals(o: Any) = o match {
      case that: RoleType[_] => that.role == this.role
      case that: PlayerType[_] => that.core == getCoreFor(this)
      case that: Any => that == getCoreFor(this)
    }

    override def hashCode(): Int = role.hashCode()
  }

  implicit class PlayerType[T](val core: T) extends Dynamic with DispatchType {
    def play(role: Any): PlayerType[T] = {
      core match {
        case p: PlayerType[_] => addPlaysRelation(p.core, role)
        case p: Any => addPlaysRelation(p, role)
      }
      this
    }

    def drop(role: Any): PlayerType[T] = {
      removePlaysRelation(core, role)
      this
    }

    def unary_+ : PlayerType[T] = this

    def transfer(role: Any) = new {
      def to(player: Any) {
        transferRole(this, player, role)
      }
    }

    def applyDynamic[E, A](name: String)(args: A*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E = {
      val anys = reorder(Queue() ++ plays.getRoles(getCoreFor(core)).tail :+ core :+ getCoreFor(core), dispatchQuery)
      anys.foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName == name).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })
      // otherwise give up
      throw new RuntimeException(s"No role with method '$name' found! (core: '$core')")
    }

    def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E = {
      val anys = reorder(Queue() ++ plays.getRoles(getCoreFor(core)).tail :+ core :+ getCoreFor(core), dispatchQuery)
      anys.foreach(r => if (r.hasAttribute(name)) return r.propertyOf[E](name))

      // otherwise give up
      throw new RuntimeException(s"No role with value '$name' found! (core: '$core')")
    }

    def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty) {
      val anys = reorder(Queue() ++ plays.getRoles(getCoreFor(core)).tail :+ core :+ getCoreFor(core), dispatchQuery)
      anys.foreach(r => if (r.hasAttribute(name)) return r.setPropertyOf(name, value))

      // otherwise give up
      throw new RuntimeException(s"No role with value '$name' found! (core: '$core')")
    }

    def isPlaying[E: WeakTypeTag]: Boolean = plays.getRoles(core)
      .find(r => r.getClass.getSimpleName == ReflectiveHelper.typeSimpleClassName(weakTypeOf[E])) match {
      case None => false
      case _ => true
    }

    override def equals(o: Any) = o match {
      case that: PlayerType[_] => that.core == this.core
      case that: Any => that == this.core
    }

    override def hashCode(): Int = core.hashCode()
  }

}
