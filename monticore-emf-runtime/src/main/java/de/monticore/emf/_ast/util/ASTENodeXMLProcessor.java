package de.monticore.emf._ast.util;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.util.XMLProcessor;

import de.monticore.emf._ast.ASTENodePackage;

/**
 * This class contains helper methods to serialize and deserialize XML documents
 */
public class ASTENodeXMLProcessor extends XMLProcessor {
  
  /**
   * Public constructor to instantiate the helper.
   */
  public ASTENodeXMLProcessor() {
    super((EPackage.Registry.INSTANCE));
    ASTENodePackage.eINSTANCE.eClass();
  }
  
  /**
   * Register for "*" and "xml" file extensions the
   * EautomatonResourceFactoryImpl factory.
   */
  @Override
  protected Map<String, Resource.Factory> getRegistrations() {
    if (registrations == null) {
      super.getRegistrations();
      registrations.put(XML_EXTENSION, new ASTENodeResourceFactoryImpl());
      registrations.put(STAR_EXTENSION, new ASTENodeResourceFactoryImpl());
    }
    return registrations;
  }
  
}
