package scroll.internal.util

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Very simple logging utility. Simply set the DEBUG flag
 * and show logging message to STDOUT via info().
 */
object Log {
  val DEBUG = true

  private def now(): String = {
    val DATE_FORMAT_NOW = "HH:mm:ss:SSS"
    val cal = Calendar.getInstance()
    val sdf = new SimpleDateFormat(DATE_FORMAT_NOW)
    "[ - " + sdf.format(cal.getTime) + " - ] "
  }

  /**
   * Prints the provided message to stdout iff DEBUG is set to true.
   *
   * @param message the message to print to stdout
   */
  def info(message: String) {
    if (DEBUG) println(now + message)
  }

}