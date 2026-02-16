/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class MCGrammarSymbolBuilder extends MCGrammarSymbolBuilderTOP {

  protected final List<MCGrammarSymbolSurrogate> superGrammars = new ArrayList<>();

  public void addSuperGrammar(MCGrammarSymbolSurrogate superGrammarRef) {
    this.superGrammars.add(Preconditions.checkNotNull(superGrammarRef));
  }

  public MCGrammarSymbol build(){
    MCGrammarSymbol symbol = super.build();
    superGrammars.forEach(symbol::addSuperGrammar);
    return symbol;
  }

}
