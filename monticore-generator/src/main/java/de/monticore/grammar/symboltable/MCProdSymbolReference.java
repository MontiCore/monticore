/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;

import static de.monticore.grammar.symboltable.MCProdSymbol.KIND;

public class MCProdSymbolReference extends CommonSymbolReference<MCProdSymbol> {

  public MCProdSymbolReference(String referencedSymbolName, Scope enclosingScopeOfReference) {
    super(referencedSymbolName, KIND, enclosingScopeOfReference);
  }
}
