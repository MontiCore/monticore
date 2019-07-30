/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

/**
 * Represents a reference of {@link MCGrammarSymbol}.
 */
public class MCGrammarSymbolReference extends MCGrammarSymbolReferenceTOP {


  public MCGrammarSymbolReference(String name, IGrammarScope enclosingScopeOfReference) {
    super(name, enclosingScopeOfReference);
  }

  @Override
  public String getName() {
    return this.name;
  }

}

