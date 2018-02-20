/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MCProdSymbolReference extends CommonSymbolReference<MCProdSymbol> {
  
  public MCProdSymbolReference(String referencedSymbolName, Scope enclosingScopeOfReference) {
    super(referencedSymbolName, MCProdSymbol.KIND, enclosingScopeOfReference);
  }
}
