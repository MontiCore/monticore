/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts.formatter;

import java.util.List;

import de.monticore.generating.templateengine.reporting.artifacts.model.RootPkg;

public abstract class AFormatter {
  
  private String indentString = "";
  
  protected void indent() {
    indentString += "  ";
  }
  
  protected void unindent() {
    indentString = indentString.substring(0, indentString.length() - 2);
  }
  
  protected void addLine(List<String> lines, String line) {
    lines.add(indentString + line);
  }
  
  public abstract List<String> getContent(RootPkg rootPkg);
  
}
