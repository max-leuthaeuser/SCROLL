package scroll.internal.util

import org.slf4j.LoggerFactory

/**
  * Very simple logging utility using slf4j.
  */
object Log {
  private[this] lazy val logger = LoggerFactory.getLogger("SCROLL")

  // set a system property such that Simple Logger will include timestamp
  System.setProperty("org.slf4j.simpleLogger.showDateTime", "true")
  // set a system property such that Simple Logger will include timestamp in the given format
  System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd/HH:mm:ss.SSS/zzz")

  /**
    * Log a info message.
    *
    * @param message the message to log
    */
  def info(message: String): Unit = {
    logger.info(message)
  }

}