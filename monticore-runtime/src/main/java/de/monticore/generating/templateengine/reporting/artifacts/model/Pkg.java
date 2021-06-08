/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts.model;

public class Pkg extends APkg {
  
  // Package name, not qualified (relative to parent package)
  private String name;
  
  // Parent Pkg
  private APkg parentPkg;
  
  // Simple constructor
  public Pkg(APkg parent, String name) {
    this.name = name;
    this.parentPkg = parent;
  }
  
  public String getQualifiedName() {
    if (parentPkg instanceof RootPkg) {
      return name;
    }
    
    return parentPkg.getQualifiedName() + "." + name;
  }
  
  /**
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * @see visualization.model.APkg#resolveAncestorWithElements()
   */
  @Override
  public APkg resolveAncestorWithElements() {
    if (parentPkg.hasElements()) {
      return parentPkg;
    }
    
    return parentPkg.resolveAncestorWithElements();
  }
  
}
