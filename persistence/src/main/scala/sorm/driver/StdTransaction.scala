package sorm.driver

trait StdTransaction {
  self: StdConnection =>
  def transaction[T](t: => T): T = connection.transaction(t)
}
