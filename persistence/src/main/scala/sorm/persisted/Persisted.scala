package sorm.persisted

import reflect.runtime.universe._

import sorm._
import reflection._

import sext._, embrace._

object Persisted {

  def apply
  [T <: Persisted]
  (instance: T,
   id: Long)
  : T
  = throw new Exception("Persisted on persisted called")

  def apply
  [T <: AnyRef : WeakTypeTag]
  (instance: T,
   id: Long)
  : T with Persisted
  = apply(instance.reflected, id)

  def apply
  [T]
  (reflected: Reflected,
   id: Long)
  : T with Persisted
  = apply(reflected.propertyValues, id, reflected.reflection)
    .asInstanceOf[T with Persisted]

  def apply
  [T: WeakTypeTag]
  (args: Map[String, Any],
   id: Long)
  : T with Persisted
  = apply(args, id, Reflection[T])
    .asInstanceOf[T with Persisted]

  def apply
  (args: Map[String, Any],
   id: Long,
   r: Reflection)
  : Persisted
  = PersistedClass(r)
    .instantiate(
      id +: r.primaryConstructorArguments.toStream.unzip._1.map {
        args
      }
    )

}