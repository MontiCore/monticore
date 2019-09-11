/* (c) https://github.com/MontiCore/monticore */
package de.monticore.aggregation.blah._symboltable;

import java.util.Deque;

public class BlahSymbolTableCreator  extends BlahSymbolTableCreatorTOP {
  public BlahSymbolTableCreator(IBlahScope enclosingScope) {
    super(enclosingScope);
  }

  public BlahSymbolTableCreator(Deque<IBlahScope> scopeStack) {
    super(scopeStack);
  }
  
  protected de.monticore.aggregation.blah._symboltable.BlubSymbol create_Blub(de.monticore.aggregation.blah._ast.ASTBlub ast) {
    // creates new visibility scope
    BlahScope bla = new BlahScope(false);
    bla.setExportingSymbols(true);

    return new BlubSymbolBuilder().setName(ast.getName()).build();
  }
}
