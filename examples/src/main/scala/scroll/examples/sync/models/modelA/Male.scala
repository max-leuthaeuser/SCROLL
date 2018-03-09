package scroll.examples.sync.models.modelA

class Male (cname: String) extends Person (cname) {
    
  override def toString(): String = {
    return "Male: " + fullName + " D: " + deleted;
  }      
}