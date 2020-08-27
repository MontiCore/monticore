/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
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

  protected static final String INHERITED_SYMBOL = MC2CDStereotypes.INHERITED_SYMBOL.toString();

  public static Map<String, String> calculateKindHierarchy(List<ASTCDType> symbolProds,
      SymbolTableService service) {
    Map<String, String> kindHierarchy = new HashMap<>();
    // iterate over all locally defined symbol classes
    for (ASTCDType s : symbolProds) {
      CDTypeSymbol symbol = s.getSymbol();
      // if a symbol kind has a supersymbol kind, the CD symbol has the stereotype "inheritedSymbol"
      if (hasInheritedSymbolStereotype(symbol)) {
        String symbolName = service.getSymbolFullName(s);
        String superSymbolName = getSuperSymbolName(symbol);
        kindHierarchy.put(symbolName, superSymbolName);
      }
    }
    return kindHierarchy;
  }

  protected static String getSuperSymbolName(CDTypeSymbol symbol) {
    return symbol.getStereotype(INHERITED_SYMBOL).get().getValue();
  }

  protected static boolean hasInheritedSymbolStereotype(CDTypeSymbol symbol) {
    if (null == symbol) {
      return false;
    }
    return symbol.getStereotype(INHERITED_SYMBOL).isPresent();
  }

}
