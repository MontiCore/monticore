/* (c) https://github.com/MontiCore/monticore */


package de.monticore.grammar.symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MCGrammarSymbolReference extends CommonSymbolReference<MCGrammarSymbol> {

  public MCGrammarSymbolReference(String referencedSymbolName,Scope enclosingScopeOfReference) {
    super(referencedSymbolName, MCGrammarSymbol.KIND, enclosingScopeOfReference);
  }
}
