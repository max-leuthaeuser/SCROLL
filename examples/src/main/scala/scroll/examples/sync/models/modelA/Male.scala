package scroll.examples.sync.models.modelA

/**
  * Male from model A extends from Person.
  */
class Male(cname: String) extends Person(cname) {

  override def toString(): String = {
    return "Male: " + fullName + " D: " + deleted;
  }
}