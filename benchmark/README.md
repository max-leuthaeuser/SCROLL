SCROLL Benchmark
================

This subproject contains the JMH benchmarks used to measure dispatch, caching, and larger end-to-end role scenarios.
It depends on the main `core` module and is compiled as the `benchmark` sbt subproject.

## How to run ##

1) Run `sbt` and switch to the `benchmark` module with `project benchmark`, or compile it directly with
   `benchmark / compile`.

2) Launch `JMH` e.g., for the `BankExample` benchmark with: `Jmh / run -jvmArgs "-Xms10g -Xmx12g -XX:+UseG1GC -XX:ParallelGCThreads=4 -XX:ConcGCThreads=1 -XX:InitiatingHeapOccupancyPercent=70" .*BankBenchmark`

3) To run through the dedicated benchmark runner that also exports results as `.csv` and `.json`, use:
   `benchmark / Jmh / run .*BankBenchmark`

The main library now exposes the public `scroll` facade for user-facing code. The benchmark sources intentionally stay
close to the lower-level runtime APIs they measure, so they may still import selected `scroll.internal.*` types.

For GC parameters see: https://www.oracle.com/technetwork/articles/java/g1gc-1984535.html

- `-XX:ParallelGCThreads=n`: set `n` to approximately 5/8 of the logical processors.
- `-XX:ConcGCThreads=n`: set `n` to approximately 1/4 of the number of parallel garbage collection threads.

## Analyse the Results ##

Use e.g.: http://jmh.morethan.io/

When benchmarks are started through `RunnerApp`, result files are written to the working directory as
`benchmark_<timestamp>.csv` and `benchmark_<timestamp>.json`.

## Enable Profiling ##

Use e.g., YourKit with: `Jmh / run -jvmArgs "-agentpath:/Applications/YourKit-Java-Profiler-2019.1.app/Contents/Resources/bin/mac/libyjpagent.jnilib=onexit=memory,onexit=snapshot -Xms10g -Xmx12g -XX:+UseG1GC -XX:ParallelGCThreads=4 -XX:ConcGCThreads=1 -XX:InitiatingHeapOccupancyPercent=70" .*BankBenchmark`
