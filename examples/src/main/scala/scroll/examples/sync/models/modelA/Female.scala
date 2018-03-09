package scroll.examples.sync.models.modelA

class Female (cname: String) extends Person (cname) {
        
  override def toString(): String = {
    return "Female: " + fullName + " D: " + deleted;
  }      
}