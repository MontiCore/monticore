/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.monticore.generating.templateengine.reporting.artifacts.ReportingNameHelper;

public class Element {
  
  private long numberOfCalls = 0;
  
  private ElementType type;
  
  // SimpleName
  private String simpleName;
  
  // File extension
  private String extension;
  
  // The package that contains this element
  private APkg pkg;
  
  // List of links going from this element
  private Map<String, Element> links = new HashMap<String, Element>();
  
  // Number of Link calls per Link
  private Map<String, Long> numberOfLinkCalls = new HashMap<String, Long>();
  
  private boolean hasLinkToFile = false;
  
  /**
   * Get full qualified name of represented element ($package.$class) without fileExtension
   */
  public String getQualifiedName() {
    String fqn = "";
    if (!pkg.getQualifiedName().isEmpty()) {
      fqn += pkg.getQualifiedName() + ".";
    }
    fqn += simpleName;
    return fqn;
  }
  
  /**
   * Add a link to the provided element. If provided element is of type {@link ElementType#FILE},
   * the flag {@link #hasLinkToFile} becomes true. There can only exist one link per fullName
   * ($simpleName$extension). If a link already exists, the number of linkCalls is incremented.
   */
  public void addlink(Element e) {    
    String fqn = e.getFullQualifiedName();
    if (!links.containsKey(fqn)) {
      links.put(fqn, e);
      numberOfLinkCalls.put(fqn, 0l);
      if (e.getType() == ElementType.FILE) {
        hasLinkToFile = true;
      }
    }
  }
  
  public String getExtension() {
    return extension;
  }
  
  /**
   * Element type represented by this element (e.g. Model, Template, etc.)
   */
  public ElementType getType() {
    return type;
  }
  
  /**
   * @return
   */
  public Collection<Element> getLinks() {
    return links.values();
  }
  
  public String getSimpleName() {
    return simpleName;
  }
  
  /**
   * Get full name of represented element ($simpleName.$extension) or ($simpleName) if no
   * fileExtension specified.
   */
  public String getFullName() {
    return ReportingNameHelper.getFullName(simpleName, extension);
  }
  
  /**
   * @param template
   */
  public void setType(ElementType elementType) {
    this.type = elementType;
  }
  
  /**
   * @param extension
   */
  public void setExtension(String extension) {
    this.extension = extension;
  }
  
  /**
   * @param simpleName
   */
  public void setSimpleName(String simpleName) {
    this.simpleName = simpleName;
  }
  
  /**
   * @param pkg
   */
  public void setPkg(APkg pkg) {
    this.pkg = pkg;
  }
  
  public long getNumberOfCalls() {
    return numberOfCalls;
  }
  
  public String getFullQualifiedName() {
    String fqn = getQualifiedName();
    if (!extension.isEmpty()) {
      fqn += "." + extension;
    }
    return fqn;
  }
  
  /**
   * @return
   */
  public APkg getPkg() {
    return pkg;
  }
  
  /**
   * @param link
   * @return
   */
  public Long getNumberOfLinkCalls(Element link) {
    return numberOfLinkCalls.get(link.getFullQualifiedName());
  }
  
  /**
   * @return
   */
  public boolean hasLinkToFile() {
    return hasLinkToFile;
  }
  
  public boolean hasLink(Element target) {
    return links.values().contains(target);
  }
  
  public void incLinkCalls(Element target) {
    String fqn = target.getFullQualifiedName();
    if (numberOfLinkCalls.containsKey(fqn)) {
      long linkCalls = numberOfLinkCalls.get(fqn);
      linkCalls++;
      numberOfLinkCalls.put(fqn, linkCalls);
    }
  }
  
  public void incCalls() {
    numberOfCalls++;
  }
}
