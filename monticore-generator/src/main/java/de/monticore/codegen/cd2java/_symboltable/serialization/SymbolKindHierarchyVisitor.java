/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class calculates a hierarchy of symbol kinds and stores these in a map. Each entry of the
 * map maps the name of a symbol-defining type to the name of a (transitive) supertype that also
 * defines a symbol.
 * Example:
 *   symbol A extends B;
 *   B extends C;
 *   symbol C extends D;
 *   symbol D;
 * Produces the following map: {"A"->"C", "C"->"D"}
 */
public class SymbolKindHierarchyVisitor implements CD4AnalysisVisitor {

  protected String currentType = null;

  private Map<String, String> kindHierarchy = new HashMap<>();

  public static Map<String, String> calculateKindHierarchy(List<ASTCDType> symbolProds) {
    // iterate over all locally defined symbol classes
    SymbolKindHierarchyVisitor visitor = new SymbolKindHierarchyVisitor();
    symbolProds.forEach(s -> s.getSymbol().accept(visitor));
    return visitor.getKindHierarchy();
  }

  public Map<String, String> getKindHierarchy() {
    return kindHierarchy;
  }

  @Override public void visit(CDTypeSymbol symbol) {
    if (hasSymbolStereotype(symbol)) {
      if (null != currentType) {
        kindHierarchy.put(currentType, symbol.getName());
      }
      currentType = symbol.getName();
    }
  }

  @Override public void traverse(CDTypeSymbol symbol) {
//    if (hasSymbolStereotype(symbol)) {
      for (CDTypeSymbol superType : symbol.getSuperTypes()) {
        superType.accept(this);
//      }
    }
    currentType = null;
  }

  protected boolean hasSymbolStereotype(CDTypeSymbol symbol) {
    return symbol.getStereotype(MC2CDStereotypes.SYMBOL.toString()).isPresent();
  }
}
