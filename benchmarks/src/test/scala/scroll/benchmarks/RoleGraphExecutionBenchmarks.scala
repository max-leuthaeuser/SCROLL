package scroll.benchmarks

import org.scalameter.api._

trait RoleGraphExecutionBenchmarks extends BenchmarkHelper {
  performance of "RoleGraph" in {
    measure method "invoke role method" in {
      using(compartmentsJGRAPHT) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS,
        exec.minWarmupRuns -> MIN_WARMUPS,
        exec.maxWarmupRuns -> MAX_WARMUPS,
        exec.jvmflags -> JVM_FLAGS
        ) in {
        c => c.invokeAtRole()
      }
    }

    measure method "invoke role method (cached)" in {
      using(compartmentsCACHED) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS,
        exec.minWarmupRuns -> MIN_WARMUPS,
        exec.maxWarmupRuns -> MAX_WARMUPS,
        exec.jvmflags -> JVM_FLAGS
        ) in {
        c => c.invokeAtRole()
      }
    }

    measure method "invoke role method (kiama)" in {
      using(compartmentsKIAMA) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS,
        exec.minWarmupRuns -> MIN_WARMUPS,
        exec.maxWarmupRuns -> MAX_WARMUPS,
        exec.jvmflags -> JVM_FLAGS
        ) in {
        c => c.invokeAtRole()
      }
    }
  }
}