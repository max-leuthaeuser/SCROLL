package scroll.examples.sync

import java.io.File

object ShowClassload extends App {

  var classLoader = new java.net.URLClassLoader(Array(new File("Inherit.jar").toURI.toURL), this.getClass.getClassLoader)
  // need to specify parent, so we have all class instances in current context

  // Suffix "$" is for Scala "object"
  var clazzExModule = classLoader.loadClass("inherit.Module")
  //var clazzExModule = classLoader.loadClass("inherit.Module$")

  var methods = clazzExModule.getMethods()
  var method = clazzExModule.getMethod("giveOut")
  println("Invoked method name: " + method.getName());
  method.invoke(clazzExModule);

  println("++++Methods: ");
  methods.foreach {
    a => println(a.toString());
  }

  println("++++Second++++++++++++++++++++++++++++++++");
  var classLoader1 = new java.net.URLClassLoader(Array(new File("ModelC.jar").toURI.toURL), this.getClass.getClassLoader)
  var clazzExModule1 = classLoader1.loadClass("synchro.modelC.PersonForRegister")

  var constructors = clazzExModule1.getConstructors()
  constructors.foreach {
    a => println(a.toString());
  }
  var constructor = clazzExModule1.getConstructors()(0)
  println("constructor: " + constructor.toString());
  constructor.getParameterTypes.foreach {
    a => println(a.toString());
  }

  var myClassObject = constructor.newInstance("John Wick", boolean2Boolean(false));
  //println("Class: " + myClassObject.toString());
  //var constructor = clazzExModule1.getConstructor(java.lang.String.class, Boolean.class);
  //var myClassObject = constructor.newInstance("John Wick", true);

  // Getting the target method from the loaded class and invoke it using its name
  var methods1 = clazzExModule1.getMethods()
  var method1 = clazzExModule1.getMethod("getMale")
  println("Invoked method name: " + method1.getName());
  var value = method1.invoke(myClassObject);
  println("Value: " + value);
  println("++++Methods: ");
  methods1.foreach {
    a => println(a.toString());
  }
}