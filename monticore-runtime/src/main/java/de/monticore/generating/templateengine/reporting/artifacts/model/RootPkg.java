/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts.model;

/**
 * Represents the topmost package in the package hierarchy of a recorded run
 */
public class RootPkg extends APkg {
  
  /**
   * @see visualization.model.APkg#getQualifiedName()
   */
  @Override
  public String getQualifiedName() {
    return "";
  }
  
  /**
   * Adds an element to the package tree of this root pkg. Missing subpackages are created on
   * demand. In addition, the Pkg of the provided Element is set.
   * 
   * @param packagePath qualified Name of the package
   * @param e Element
   */
  public void addElementToPkgTree(String packagePath, Element e) {
    APkg pkg = getPkg(packagePath);
    pkg.addElement(e);
    e.setPkg(pkg);
  }
  
  /**
   * @see visualization.model.APkg#resolveAncestorWithElements()
   */
  @Override
  public APkg resolveAncestorWithElements() {
    return null;
  }
  
}
