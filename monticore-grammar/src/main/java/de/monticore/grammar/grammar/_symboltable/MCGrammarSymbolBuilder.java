/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import java.util.ArrayList;
import java.util.List;

import static de.se_rwth.commons.logging.Log.errorIfNull;

public class MCGrammarSymbolBuilder extends MCGrammarSymbolBuilderTOP {

  protected final List<MCGrammarSymbolSurrogate> superGrammars = new ArrayList<>();

  public void addSuperGrammar(MCGrammarSymbolSurrogate superGrammarRef) {
    this.superGrammars.add(errorIfNull(superGrammarRef));
  }

  public MCGrammarSymbol build(){
    MCGrammarSymbol symbol = super.build();
    superGrammars.forEach(symbol::addSuperGrammar);
    return symbol;
  }

}
