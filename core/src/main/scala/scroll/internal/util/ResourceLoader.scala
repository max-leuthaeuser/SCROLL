package scroll.internal.util

object ResourceLoader {

  /** Returns a filesystem path to a classpath resource, extracting it to a temp file if it lives inside a JAR. */
  def resourcePath(name: String, loader: ClassLoader = getClass.getClassLoader): String = {
    val url = loader.getResource(name.stripPrefix("/"))
    require(url != null, s"Classpath resource not found: $name")
    if (url.getProtocol == "file") url.getPath
    else {
      val suffix = name.substring(name.lastIndexOf('.'))
      val tmp    = java.io.File.createTempFile("scroll-resource", suffix)
      tmp.deleteOnExit()
      val in  = url.openStream()
      val out = new java.io.FileOutputStream(tmp)
      try in.transferTo(out)
      finally { in.close(); out.close() }
      tmp.getAbsolutePath
    }
  }

}
