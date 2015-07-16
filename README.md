SCROLL
======
*SCala ROLes Language*

[![Build Status](https://travis-ci.org/max-leuthaeuser/SCROLL.svg)](https://travis-ci.org/max-leuthaeuser/SCROLL)

A playground for role dispatch based on Scala.

**1. Current state:** 

You are able to define compartments, roles and play-relationships. Invoking Role-methods is done via the [Dynamic][scala-dynamic] trait.
  
**2. Example:**

[BankExample][BankExample]

  You can find more scroll.examples in the ```scroll/examples/``` folder.
  You also might want to check the ```test/```folder.

**3. Edit and develop:**

3.1. Clone this repo.

3.2. You may want to use SBT and run ```gen-idea```if you are using Intellij IDE <= 13 (to config see [here][sbt-gen-idea]). This is not required anymore since Intellij 14. Just use the built-in import SBT project functionality.

3.3. You may want to use SBT and run ```eclipse``` if you are using the Eclipse Scala IDE. (to config see [here][gen-eclipse])

**4. Use the library:**

Just add the dependency to your SBT config:
```libraryDependencies ++= Seq("com.github.max-leuthaeuser" % "scroll_2.11" % "0.9.2")```

[sbt-gen-idea]: https://github.com/mpeltonen/sbt-idea
[gen-eclipse]: https://github.com/typesafehub/sbteclipse
[scala-dynamic]: http://www.scala-lang.org/api/current/#scala.Dynamic
[BankExample]: https://github.com/max-leuthaeuser/SCROLL/blob/master/src/main/scala/scroll/examples/BankExample.scala
