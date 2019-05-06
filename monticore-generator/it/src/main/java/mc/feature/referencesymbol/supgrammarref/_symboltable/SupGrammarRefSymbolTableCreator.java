package mc.feature.referencesymbol.supgrammarref._symboltable;

import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import mc.feature.referencesymbol.reference._ast.ASTRand;
import mc.feature.referencesymbol.reference._ast.ASTTest;
import mc.feature.referencesymbol.reference._symboltable.RandSymbol;
import mc.feature.referencesymbol.reference._symboltable.TestSymbol;
import mc.feature.referencesymbol.supgrammarref._ast.ASTSupRand;

import java.util.Deque;

public class SupGrammarRefSymbolTableCreator extends SupGrammarRefSymbolTableCreatorTOP {

  private TestSymbol testSymbol;
  private ASTSupRand astRand;
  private ASTTest astTest;


  public SupGrammarRefSymbolTableCreator(final ResolvingConfiguration resolvingConfig, final Scope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  public SupGrammarRefSymbolTableCreator(final ResolvingConfiguration resolvingConfig, final Deque<Scope> scopeStack) {
    super(resolvingConfig, scopeStack);
  }

  @Override
  public void visit(ASTTest node) {
    this.astTest = node;
    testSymbol = new TestSymbol(astTest.getName());
    astTest.setTestSymbol(testSymbol);

    addToScopeAndLinkWithNode(testSymbol, astTest);
    setEnclosingScopeOfNodes(astTest);
  }


  @Override
  public void visit(ASTRand node) {

    addToScopeAndLinkWithNode(new RandSymbol(node.getName()), node);
    setEnclosingScopeOfNodes(node);
  }

  @Override
  protected Scope create_SupRand(ASTSupRand ast) {
    // creates new shadowing scope
    SupGrammarRefScope a = new SupGrammarRefScope(true);
    a.setExportsSymbols(true);
    return a;
  }
}
