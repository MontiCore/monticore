package de.monticore.emf._ast.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/**
 * The <b>Resource Factory</b> associated with the package.
 */

public class ASTENodeResourceFactoryImpl extends ResourceFactoryImpl {
  /**
   * Creates an instance of the resource factory.
   */
  public ASTENodeResourceFactoryImpl() {
    super();
  }
  
  /**
   * Creates an instance of the resource.
   */
  @Override
  public Resource createResource(URI uri) {
    Resource result = new ASTENodeResourceImpl(uri);
    return result;
  }
}
