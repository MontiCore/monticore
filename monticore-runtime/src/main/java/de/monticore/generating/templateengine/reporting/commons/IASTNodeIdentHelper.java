/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.references.SymbolReference;

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
  
  default public String getIdent(Symbol symbol) {
    return format(maskSpecialChars(symbol.getName()), "Symbol");
  }
  
  default public String getIdent(SymbolReference<?> symbol) {
    return format(maskSpecialChars(symbol.getName()), "SymbolReference");
  }
  
  default public String maskSpecialChars(String name) {
    // Replace all special characters by _
    name = name.replaceAll("[^a-zA-Z0-9_$\\-+]", "_");
    if (name.matches("[0-9].*")) {
      // if the name starts with a digit ...
      name = "_".concat(name);
    }
    return name;
  }

  default public String getIdent(Scope scope) {
    String type;
    if (scope instanceof ArtifactScope) {
      type = "ArtifactScope";
    } else if (scope instanceof GlobalScope) {
      type = "GlobalScope";
    } else {
      type = "Scope";
    }
    return format(maskSpecialChars(scope.getName().orElse("")), type);
  }
  
}
