package scroll.benchmarks

import org.openjdk.jmh.results.format.ResultFormatFactory
import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.CommandLineOptions

import java.text.SimpleDateFormat
import java.util.Date

object RunnerApp {

  def main(args: Array[String]): Unit = {
    val opts      = new CommandLineOptions(args: _*)
    val runner    = new Runner(opts)
    val results   = runner.run()
    val timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())

    ResultFormatFactory
      .getInstance(ResultFormatType.SCSV, s"benchmark_$timeStamp.csv")
      .writeOut(results)
    ResultFormatFactory
      .getInstance(ResultFormatType.JSON, s"benchmark_$timeStamp.json")
      .writeOut(results)
  }

}
