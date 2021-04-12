package scroll.tests.mocks

class RoleE {
  var valueInt: Int = -1

  var valueDouble: Double = -1

  var valueFloat: Float = -1

  var valueLong: Long = -1

  var valueShort: Short = -1

  var valueByte: Byte = -1

  var valueChar: Char = 'A'

  var valueBoolean: Boolean = false

  def updateInt(newValue: Int): Unit = valueInt = newValue

  def updateDouble(newValue: Double): Unit = valueDouble = newValue

  def updateFloat(newValue: Float): Unit = valueFloat = newValue

  def updateLong(newValue: Long): Unit = valueLong = newValue

  def updateShort(newValue: Short): Unit = valueShort = newValue

  def updateByte(newValue: Byte): Unit = valueByte = newValue

  def updateChar(newValue: Char): Unit = valueChar = newValue

  def updateBoolean(newValue: Boolean): Unit = valueBoolean = newValue

}
