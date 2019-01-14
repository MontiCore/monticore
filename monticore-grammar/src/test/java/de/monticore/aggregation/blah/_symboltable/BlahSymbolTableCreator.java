package de.monticore.aggregation.blah._symboltable;

import de.monticore.aggregation.blah._ast.ASTBlub;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

import java.util.Deque;

public class BlahSymbolTableCreator  extends BlahSymbolTableCreatorTOP {
  public BlahSymbolTableCreator(ResolvingConfiguration resolvingConfig, MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  public BlahSymbolTableCreator(ResolvingConfiguration resolvingConfig, Deque<MutableScope> scopeStack) {
    super(resolvingConfig, scopeStack);
  }
  
  protected BlubSymbol create_Blub(ASTBlub ast) {
    // creates new visibility scope
    BlubSymbol bla = new BlubSymbol(ast.getName());
//    bla.setExportsSymbols(true);
    return bla;
  }
}
