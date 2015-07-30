package scroll.internal.ecore

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.BasicExtendedMetaData
import org.eclipse.emf.ecore.xmi.XMLResource
import org.eclipse.emf.ecore.xmi.impl.{EcoreResourceFactoryImpl, XMIResourceFactoryImpl}

trait ECoreImporter {
  var path: String = _

  private val META_MODEL_PATH = "src/main/scala/scroll/internal/ecore/"
  private val META_MODEL_NAME = "crom_l1_composed.ecore"

  private def registerMetaModel(rs: ResourceSetImpl) {
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap.put(
      "ecore", new EcoreResourceFactoryImpl())

    val extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry)
    rs.getLoadOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData)

    val r = rs.getResource(URI.createFileURI(META_MODEL_PATH + META_MODEL_NAME), true)
    val eObject = r.getContents.get(0)
    eObject match {
      case p: EPackage =>
        rs.getPackageRegistry.put(p.getNsURI, p)
      case _ => throw new IllegalStateException("Meta-Model for CROM could not be loaded!")
    }
  }

  protected def loadModel(): Resource = {
    require(null != path && path.nonEmpty)

    val resourceSet = new ResourceSetImpl()
    registerMetaModel(resourceSet)
    resourceSet.getResourceFactoryRegistry.getExtensionToFactoryMap.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl())
    val r = resourceSet.getResource(URI.createFileURI(path), true)

    require(null != r)
    require(!r.getContents.isEmpty)
    r
  }
}
