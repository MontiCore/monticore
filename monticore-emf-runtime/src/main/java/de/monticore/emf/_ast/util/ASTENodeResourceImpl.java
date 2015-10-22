package de.monticore.emf._ast.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

/**
 * The <b>Resource </b> associated with the package.
 */

public class ASTENodeResourceImpl extends XMLResourceImpl {
  
  /**
   * Creates an instance of the resource.
   * 
   * @param uri the URI of the new resource.
   */
  public ASTENodeResourceImpl(URI uri) {
    super(uri);
  }
}
