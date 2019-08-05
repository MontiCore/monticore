/* (c) https://github.com/MontiCore/monticore */


package de.monticore.grammar.symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;

import static de.monticore.grammar.symboltable.MCGrammarSymbol.KIND;

public class MCGrammarSymbolReference extends CommonSymbolReference<MCGrammarSymbol> {

  public MCGrammarSymbolReference(String referencedSymbolName, Scope enclosingScopeOfReference) {
    super(referencedSymbolName, KIND, enclosingScopeOfReference);
  }
}
