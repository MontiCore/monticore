/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts.model;

import de.monticore.generating.templateengine.reporting.artifacts.ReportingNameHelper;

public class ElementFactory {
  
  public Element createTemplate (RootPkg rootPkg, String qualifiedName, String extension) {       
    return createElement(rootPkg, qualifiedName, extension, ElementType.TEMPLATE);
  } 

  /**
   * @param qualifiedfilename
   * @param fileextension
   * @return 
   */
  public Element createFile(RootPkg rootPkg, String qualifiedName, String extension) {
    return createElement(rootPkg, qualifiedName, extension, ElementType.FILE);
  }

  /**
   * @param rootPkg
   * @param modelname
   * @param filename
   */
  public Element createModel(RootPkg rootPkg, String qualifiedName, String extension) {
    return createElement(rootPkg, qualifiedName, extension, ElementType.MODEL);
  }
  
  /**
   * @param rootPkg
   * @param modelname
   * @param filename
   */
  public Element createHelper(RootPkg rootPkg, String qualifiedName, String extension) {
    return createElement(rootPkg, qualifiedName, extension, ElementType.HELPER);
  }
  
  private Element createElement(RootPkg rootPkg, String qualifiedName, String extension, ElementType elementType) {
    Element e = new Element();
    e.setSimpleName(ReportingNameHelper.getSimpleName(qualifiedName));
    e.setExtension(extension);
    e.setType(elementType);
    rootPkg.addElementToPkgTree(ReportingNameHelper.getPath(qualifiedName), e);
    return e;
  }
}
