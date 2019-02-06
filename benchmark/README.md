SCROLL Benchmark
================

## How to run ##

1) Run `sbt` and switch to the `benchmark` module with `project benchmark`.

2) Launch `JMH` e.g., for the `BankExample` benchmark with: `jmh:run -jvmArgs "-Xms10g -Xmx12g -XX:+UseG1GC -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70" .*BankBenchmark`

## Analyse the results ##

Use e.g.: http://jmh.morethan.io/
