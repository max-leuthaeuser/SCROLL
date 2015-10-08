import org.scalameter.api._

trait RoleGraphMemoryBenchmarks extends BenchmarkHelper {
  override def measurer = new Measurer.MemoryFootprint

  performance of "Memory Footprint" in {
    performance of "Compartment" in {
      using(compartments) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS
        ) in {
        // measure size of the generated compartment
        c => c
      }
    }
  }
}