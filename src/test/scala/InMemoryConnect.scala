import org.apache.commons.configuration.BaseConfiguration
import java.io.OutputStream
import com.thinkaurelius.titan.core.TitanGraph
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object InMemoryConnect {

  trait SilentLogging {
    implicit val out = new java.io.ByteArrayOutputStream()
  }

  trait DefaultLogging {
    implicit val out = Console.out
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