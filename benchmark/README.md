SCROLL Benchmark
================

## How to run ##

1) Run `sbt` and switch to the `benchmark` module with `project benchmark`.

2) Launch `JMH` e.g., for the `BankExample` benchmark with: `Jmh / run -jvmArgs "-Xms10g -Xmx12g -XX:+UseG1GC -XX:ParallelGCThreads=4 -XX:ConcGCThreads=1 -XX:InitiatingHeapOccupancyPercent=70" .*BankBenchmark`


For GC parameters see: https://www.oracle.com/technetwork/articles/java/g1gc-1984535.html

- `-XX:ParallelGCThreads=n`: set `n` to approximately 5/8 of the logical processors.
- `-XX:ConcGCThreads=n`: set `n` to approximately 1/4 of the number of parallel garbage collection threads.

## Analyse the Results ##

Use e.g.: http://jmh.morethan.io/

## Enable Profiling ##

Use e.g., YourKit with: `Jmh / run -jvmArgs "-agentpath:/Applications/YourKit-Java-Profiler-2019.1.app/Contents/Resources/bin/mac/libyjpagent.jnilib=onexit=memory,onexit=snapshot -Xms10g -Xmx12g -XX:+UseG1GC -XX:ParallelGCThreads=4 -XX:ConcGCThreads=1 -XX:InitiatingHeapOccupancyPercent=70" .*BankBenchmark`
