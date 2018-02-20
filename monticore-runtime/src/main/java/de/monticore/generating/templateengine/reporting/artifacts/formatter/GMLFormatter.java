/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts.formatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import de.monticore.generating.templateengine.reporting.artifacts.model.APkg;
import de.monticore.generating.templateengine.reporting.artifacts.model.Element;
import de.monticore.generating.templateengine.reporting.artifacts.model.ElementType;
import de.monticore.generating.templateengine.reporting.artifacts.model.Pkg;
import de.monticore.generating.templateengine.reporting.artifacts.model.RootPkg;

public class GMLFormatter extends AFormatter {
  
  private Map<Element, Integer> elementIds = new HashMap<Element, Integer>();
  
  private Map<APkg, Integer> packageIds = new HashMap<APkg, Integer>();
  
  private int nodeIdCounter = 0;
  
  private int edgeIdCounter = 0;
  
  private long maxEdgeCalls = 0;
  
  private double edgeSizeRange = 0;
  
  /**
   * @see AFormatter.printer.APrinter#print(visualization.model.RootPkg)
   */
  @Override
  public List<String> getContent(RootPkg rootPkg) {
    List<String> lines = Lists.newArrayList();
    calculateMaxEdgeCalls(rootPkg);    
    edgeSizeRange = maxEdgeCalls / 7;
    
    addLine(lines, "graph [");       
    indent();
    addLine(lines, "directed 1");
    lines.addAll(getAllPkgContent(rootPkg));
    lines.addAll(getAllLinkContent(rootPkg));
    unindent();
    addLine(lines, "]");
    return lines;
  }
  
  private void calculateMaxEdgeCalls(APkg pkg) {
    for (Element e : pkg.getElements()) {
      for (Element link : e.getLinks()) {
        Long linkCalls = e.getNumberOfLinkCalls(link);
        if (linkCalls > maxEdgeCalls) {
          maxEdgeCalls = linkCalls;
        }
      }
    }
    
    for (Pkg p : pkg.getSubPkgs()) {
      calculateMaxEdgeCalls(p);
    }
  }

  /**
   * @param pkg
   */
  private List<String> getAllLinkContent(APkg pkg) {
    List<String> lines = Lists.newArrayList();
    
    for (Element e : pkg.getElements()) {
      lines.addAll(getLinkContent(e));
    }
    
    for (Pkg p : pkg.getSubPkgs()) {
      lines.addAll(getAllLinkContent(p));
    }
    
    return lines;
  }

  private List<String> getAllPkgContent(APkg pkg) {
    List<String> lines = Lists.newArrayList();
    
    if (pkg.hasElements()) {
      lines.addAll(getPkgContent(pkg));
    }
    
    for (Pkg subPkg : pkg.getSubPkgs()) {
      lines.addAll(getAllPkgContent(subPkg));
    }
    
    for (Element e : pkg.getElements()) {
      lines.addAll(getElementContent(e));
    }
    
    return lines;
  }
  
  private List<String> getPkgContent(APkg pkg) {
    List<String> lines = Lists.newArrayList();
    
    if (!pkg.containsNonFileElement()) {
      return lines;
    }
    addLine(lines, "node [");
    indent();
    addLine(lines, "id " + getGroupIDByPackage(pkg));
    addLine(lines, "label " + "\"" + pkg.getQualifiedName() + "\"");
    addLine(lines, "graphics [");
    indent();
    addLine(lines, "type \"roundrectangle\"");
    addLine(lines, "fill  \"#F5F5F5\"");
    addLine(lines, "outline  \"#000000\"");
    addLine(lines, "outlineStyle \"dashed\"");
    addLine(lines, "topBorderInset  0.0");
    addLine(lines, "bottomBorderInset 0.0");
    addLine(lines, "leftBorderInset 0.0");
    addLine(lines, "rightBorderInset  0.0");
    unindent();
    addLine(lines, "]");
    addLine(lines, "LabelGraphics [");
    indent();
    addLine(lines, "fill  \"#EBEBEB\"");
    addLine(lines, "fontSize  15");
    addLine(lines, "fontName  \"Dialog\"");
    addLine(lines, "alignment \"right\"");
    addLine(lines, "autoSizePolicy  \"node_width\"");
    addLine(lines, "anchor  \"t\"");
    addLine(lines, "borderDistance  0.0");
    unindent();
    addLine(lines, "]");
    addLine(lines, "isGroup 1");
    APkg ancestor = pkg.resolveAncestorWithElements();
    if (ancestor != null) {
      addLine(lines, "gid " + getGroupIDByPackage(ancestor));
    }
    unindent();
    addLine(lines, "]");
    
    return lines;
  }

  /**
   * Print the dot graph representation of this element
   * 
   * @param o Open file to write to
   * @param space Space for indentation
   */
  public List<String> getElementContent(Element element) {
    List<String> lines = Lists.newArrayList();
    
    if (element.getType() == ElementType.FILE) {
      return lines;
    }
    
    addLine(lines, "node [");
    indent();
    addLine(lines, "id " + getID(element));
    addLine(lines, "label " + "\"" + element.getFullName() + " (" + element.getNumberOfCalls()
        + ")\"");
    addLine(lines, "graphics [");
    indent();
    if (element.hasLinkToFile()) {
      addLine(lines, "fill \"#00FF00\"");
    }
    addLine(lines, "type " + "\"" + getShape(element.getType()) + "\"");
    unindent();
    addLine(lines, "]");    
    addLine(lines, "gid " + getGroupIDByPackage(element.getPkg()));
    unindent();
    addLine(lines, "]");
    
    return lines;
  }
  
  /**
   * 
   * @param name
   * @return
   */
  private String getShape(ElementType type) {
    switch (type) {
      case HELPER:
        return "diamond";
      case MODEL:
        return "hexagon";
      case TEMPLATE:
        return "ellipse";
      default:
        return "octagon";
    }
  }
  
  /**
   * @param pkg
   * @return
   */
  private Integer getGroupIDByPackage(APkg pkg) {
    if (packageIds.containsKey(pkg)) {
      return packageIds.get(pkg);
    }
    
    nodeIdCounter++;
    packageIds.put(pkg, nodeIdCounter);
    return nodeIdCounter;
  }
  
  /**
   * @param element
   * @return
   */
  private Integer getID(Element element) {
    if (elementIds.containsKey(element)) {
      return elementIds.get(element);
    }
    
    nodeIdCounter++;
    elementIds.put(element, nodeIdCounter);
    return nodeIdCounter;
  }
  
  /**
   * write all links to supplied open file
   */
  public List<String> getLinkContent(Element element) {
    List<String> lines = Lists.newArrayList();
    
    Integer elementId = elementIds.get(element);    
    for (Element link : element.getLinks()) {
      if (link.getType() == ElementType.FILE) {
        continue;
      }
      long calls = element.getNumberOfLinkCalls(link);
      edgeIdCounter++;
      addLine(lines, "edge [");
      indent();
      addLine(lines, "id " + edgeIdCounter);
      addLine(lines, "source " + elementId);
      addLine(lines, "target " + elementIds.get(link));
      addLine(lines, "label " + "\"(" + calls + ")\"");
      addLine(lines, "graphics [");
      indent();
      addLine(lines, "targetArrow \"standard\"");
      addLine(lines, "width " + getEdgeWith(calls));
      unindent();
      addLine(lines, "]");
      unindent();
      addLine(lines, "]");           
    }
    return lines;
  }

  /**
   * @param calls
   * @return
   */
  private int getEdgeWith(long calls) {    
    return Math.min((int) (calls/edgeSizeRange) + 1, 7);
  }
}
