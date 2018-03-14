/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.monticore.generating.templateengine.reporting.artifacts.ReportingNameHelper;

public abstract class APkg {
     
  /**
   * Maps name of subpackage to subpackage
   */
  private Map<String, Pkg> subPkgs = new HashMap<String, Pkg>();
  
  /**
   * Maps fullName of Element ({@link Element#getFullName()}) to Element
   */
  private Map<String, Element> elements = new HashMap<String, Element>();
  
  private boolean containsNonFileElement = false;
  
  /**
  * Add the provided element to this package. It can only exist one element per fullName ({@link Element#getFullName()})
   * @param e
   */
  public void addElement(Element e) {    
      elements.put(e.getFullName(), e);
      if (e.getType() != ElementType.FILE) {
        containsNonFileElement = true;
      }
  }
  
  public Collection<Element> getElements() {
    return this.elements.values();
  }
  
  /**
   * @return The QualifiedName (Name without FileExtension)
   */
  public abstract String getQualifiedName();
  
  public void addSubPkg(Pkg pkg) {
    subPkgs.put(pkg.getName(), pkg);
  }
  
  /**
   * @return
   */
  public List<Pkg> getSubPkgs() {
    return new ArrayList<Pkg>(subPkgs.values());
  }
  
  /**
   * @return
   */
  public boolean hasElements() {
    return !elements.isEmpty();
  }
  
  /**
   * returns the APkg representing the path. If path is empty, the this, if no package for the given
   * path exists, create all required Pkgs.
   * 
   * @param fqn
   * @return
   */
  public APkg getPkg(String path) {
    if (path.isEmpty()) {
      return this;
    }
    
    String packageName = ReportingNameHelper.getFirstPathPart(path);
    path = ReportingNameHelper.removeFirstPathPart(path);
    
    APkg subPkg = subPkgs.get(packageName);
    if (subPkg == null) {
      Pkg pkg = new Pkg(this, packageName);
      addSubPkg(pkg);
      subPkg = pkg;
    }
    
    return subPkg.getPkg(path);
  }
  
  /**
   * Resolves the associated element and increments the number of calls for this element; 
   * 
   * @param packageName
   * @param simpleName
   * @param extension
   * @return the associated element or null if such an element does not exist
   */
  public Element resolve(String packageName, String simpleName, String extension) {
    if (packageName.isEmpty()) {
      String fullName = ReportingNameHelper.getFullName(simpleName, extension);      
      return elements.get(fullName);
    }
    
    String pkgName = ReportingNameHelper.getFirstPathPart(packageName);
    packageName = ReportingNameHelper.removeFirstPathPart(packageName);
    
    if (!subPkgs.containsKey(pkgName)) {
      return null;
    }
    
    Pkg subPkg = subPkgs.get(pkgName);
    return subPkg.resolve(packageName, simpleName, extension);
  }

  /** 
   * @return the next ancestor pkg which has elements, or null such pkg does not exist.
   */
  public abstract APkg resolveAncestorWithElements();

  /**
   * @return containsNonFileElement
   */
  public boolean containsNonFileElement() {
    return containsNonFileElement;
  }

}
