import org.apache.commons.configuration.BaseConfiguration
import java.io.OutputStream
import com.thinkaurelius.titan.core.TitanGraph
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import org.apache.log4j.Logger
import org.apache.log4j.Level

object InMemoryConnect {

  trait SilentLogging {
    implicit val out = new java.io.ByteArrayOutputStream()
    Logger.getRootLogger.setLevel(Level.OFF)
  }

  trait DefaultLogging {
    implicit val out = Console.out
    Logger.getRootLogger.setLevel(Level.ALL)
  }

  trait DefaultExecutionContext {
    implicit val ec = ExecutionContext.global
  }
}

trait InMemoryConnect {
  private val conf = new BaseConfiguration()

  def connect()(implicit out: OutputStream,
                ec: ExecutionContext = ExecutionContext.global): Future[TitanGraph] = {

    Console.withOut(out) {
      conf.setProperty("storage.backend", "inmemory")
      import com.thinkaurelius.titan.core.TitanFactory
      Future { TitanFactory.open(conf) }
    }

  }
}