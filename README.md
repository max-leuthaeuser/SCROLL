SCROLL
======
*SCala ROLes Language*

[![Build Status](https://travis-ci.org/max-leuthaeuser/SCROLL.svg)](https://travis-ci.org/max-leuthaeuser/SCROLL) [![Codacy Badge](https://api.codacy.com/project/badge/0021c87e1b824c2f8a58b81406d5db48)](https://www.codacy.com/app/max-leuthaeuser/SCROLL)

A playground for role dispatch based on Scala.

**1. Current state:** 

You are able to define compartments, roles and play-relationships. Invoking Role-methods is done via the [Dynamic][scala-dynamic] trait.
  
**2. Example:**

[BankExample][BankExample]

  You can find more scroll.examples in the ```examples/``` folder.
  You also might want to check the ```tests/```folder.

**3. Edit and develop:**
  1. Clone this repo.
  2. You may want to use SBT and run ```gen-idea```if you are using Intellij IDE (to config see [here][sbt-gen-idea]). This is not required anymore since Intellij 14. Just use the built-in import SBT project functionality.
  3. You may want to use SBT and run ```eclipse``` if you are using the Eclipse Scala IDE (to config see [here][gen-eclipse]).
  4. ScalaDoc is available [here][scaladoc].
  
**4. Use the library:**

Just add the dependency to your SBT config:
```libraryDependencies ++= Seq("com.github.max-leuthaeuser" %% "scroll" % "1.3.0")```

Or to always use the latest version:
```libraryDependencies ++= Seq("com.github.max-leuthaeuser" %% "scroll" % "latest.integration")```

[sbt-gen-idea]: https://github.com/mpeltonen/sbt-idea
[gen-eclipse]: https://github.com/typesafehub/sbteclipse
[scala-dynamic]: http://www.scala-lang.org/api/current/#scala.Dynamic
[BankExample]: https://github.com/max-leuthaeuser/SCROLL/blob/master/examples/src/main/scala/scroll/examples/BankExample.scala
[scaladoc]: http://max-leuthaeuser.github.io/SCROLL
