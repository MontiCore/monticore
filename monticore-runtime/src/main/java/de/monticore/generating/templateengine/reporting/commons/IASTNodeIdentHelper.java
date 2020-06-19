/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

public interface IASTNodeIdentHelper {
  
  public static final String LAYOUT_FULL = "@%s!%s";
  
  public static final String LAYOUT_TYPE = "@!%s";
  
  default String format(String id, String type) {
    return String.format(LAYOUT_FULL, id, type);
  }
  
  default String format(String type) {
    return String.format(LAYOUT_TYPE, type);
  }
  
  public String getIdent(ASTNode ast);
  
  default public String getIdent(ISymbol symbol) {
    return format(maskSpecialChars(symbol.getName()), "Symbol");
  }
  
  default public String maskSpecialChars(String name) {
    // Replace all special characters by _
    name = name.replaceAll("[^a-zA-Z0-9_$]", "_");
    if (name.matches("[0-9].*")) {
      // if the name starts with a digit ...
      name = "_".concat(name);
    }
    return name;
  }
  
  default public String getIdent(IScope scope) {
    String type;
    if (scope.getClass().getName().endsWith("ArtifactScope")) {
      type = "ArtifactScope";
    } else if (scope.getClass().getName().endsWith("ArtifactScope")) {
      type = "GlobalScope";
    } else {
      type = "Scope";
    }
    if (scope.isPresentName()) {
      return format(maskSpecialChars(scope.getName()), type);
    } else {
      return format(maskSpecialChars(""), type);
    }
  }
  
}
