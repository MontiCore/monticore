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
 * symbol A extends B;
 * B extends C;
 * symbol C extends D;
 * symbol D;
 * Produces the following map: {"A"->"C", "C"->"D"}
 */
public class SymbolKindHierarchyCollector implements CD4AnalysisVisitor {

  protected String currentType = null;

  private Map<String, String> kindHierarchy = new HashMap<>();

  public static Map<String, String> calculateKindHierarchy(List<ASTCDType> symbolProds) {
    // iterate over all locally defined symbol classes
    SymbolKindHierarchyCollector visitor = new SymbolKindHierarchyCollector();
    symbolProds.forEach(s -> s.getSymbol().accept(visitor));
    System.out.println(":::: calculate");
    return visitor.getKindHierarchy();
  }

  public Map<String, String> getKindHierarchy() {
    return kindHierarchy;
  }

  @Override public void visit(CDTypeSymbol symbol) {
    System.out.println(":::: visit " + symbol.getName() + " with " + currentType);
    if (hasSymbolStereotype(symbol)) {
      if (null != currentType) {
        System.out.println(":::: add " + symbol.getName() + " with " + currentType);
        kindHierarchy.put(currentType, symbol.getName());
      }
      currentType = symbol.getName();
    }
  }

  @Override public void traverse(CDTypeSymbol symbol) {
    System.out.println(":::: traverse "+symbol.getName());
    for (CDTypeSymbol superType : symbol.getSuperTypes()) {
      System.out.println(":::: traversing "+symbol.getName() +" with " + superType.getName());
      superType.accept(this);
    }
    currentType = null;
  }

  protected boolean hasSymbolStereotype(CDTypeSymbol symbol) {
    return symbol.getStereotype(MC2CDStereotypes.SYMBOL.toString()).isPresent();
  }
}
