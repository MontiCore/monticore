/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;

import java.util.List;

/**
 * This class calculates a hierarchy of symbol kinds and stores these in a map. Each entry of the
 * map maps the name of a symbol-defining type to a list of names of a (transitive) subtype that also
 * defines a symbol.
 * Example:
 * symbol A extends B;
 * B extends C;
 * symbol C extends D;
 * symbol D;
 * Produces the following map: {"C"->{"A"}, "D"->{"C", "A"}}
 */
public class SymbolKindHierarchies {

  protected static final String INHERITED_SYMBOL = MC2CDStereotypes.INHERITED_SYMBOL.toString();

  public static ListMultimap<String, String> calculateSubKinds(List<ASTCDType> symbolProds,
      SymbolTableService service) {
    ListMultimap<String, String> kindHierarchy = LinkedListMultimap.create();
    // iterate over all locally defined symbol classes
    for (ASTCDType s : symbolProds) {
      CDTypeSymbol symbol = s.getSymbol();
      // if a symbol kind has a supersymbol kind, the CD symbol has the stereotype "inheritedSymbol"
      if (hasInheritedSymbolStereotype(symbol)) {
        String symbolName = s.getName();
        symbolName = symbolName.startsWith("AST")? symbolName.substring(3) : symbolName;
        String superSymbolName = getSuperSymbolName(symbol);
        kindHierarchy.put(superSymbolName, symbolName);
      }
    }
    return kindHierarchy;
  }

  protected static String getSuperSymbolName(CDTypeSymbol symbol) {
    String qSymName = symbol.getStereotype(INHERITED_SYMBOL).get().getValue();
    int lastDotIndex = qSymName.contains(".") ? qSymName.lastIndexOf(".") + 1 : 0;
    int lengthWithoutSymSuffix = qSymName.length() - "Symbol".length();
    return qSymName.substring(lastDotIndex, lengthWithoutSymSuffix);
  }

  protected static boolean hasInheritedSymbolStereotype(CDTypeSymbol symbol) {
    if (null == symbol) {
      return false;
    }
    return symbol.getStereotype(INHERITED_SYMBOL).isPresent();
  }

}
