/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MCGrammarSymbolBuilder extends MCGrammarSymbolBuilderTOP {

  protected final List<Supplier<MCGrammarSymbol>> superGrammars = new ArrayList<>();

  public void addSuperGrammarSupplier(Supplier<MCGrammarSymbol> superGrammarRef) {
    this.superGrammars.add(Preconditions.checkNotNull(superGrammarRef));
  }

  public MCGrammarSymbol build(){
    MCGrammarSymbol symbol = super.build();
    superGrammars.forEach(symbol::addSuperGrammar);
    return symbol;
  }

}
