package de.monticore.aggregation.blah._symboltable;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;

import java.util.Deque;

public class BlahSymbolTableCreator  extends BlahSymbolTableCreatorTOP {
  public BlahSymbolTableCreator(ResolvingConfiguration resolvingConfig, Scope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  public BlahSymbolTableCreator(ResolvingConfiguration resolvingConfig, Deque<Scope> scopeStack) {
    super(resolvingConfig, scopeStack);
  }
  
  protected de.monticore.aggregation.blah._symboltable.BlubSymbol create_Blub(de.monticore.aggregation.blah._ast.ASTBlub ast) {
    // creates new visibility scope
    BlahScope bla = new BlahScope(false);
    bla.setExportsSymbols(true);

    return new BlubSymbolBuilder().name(ast.getName()).build();
  }
}
