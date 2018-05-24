package mc.feature.referencesymbol.reference._symboltable;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.feature.referencesymbol.reference._ast.ASTRand;
import mc.feature.referencesymbol.reference._ast.ASTReferenceToTest;
import mc.feature.referencesymbol.reference._ast.ASTTest;

import java.util.Deque;

public class ReferenceSymbolTableCreator extends ReferenceSymbolTableCreatorTOP {

  private RandSymbol randSymbol;
  private TestSymbol testSymbol;
  private ASTRand astRand;
  private ASTTest astTest;


  public ReferenceSymbolTableCreator(final ResolvingConfiguration resolvingConfig, final MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  public ReferenceSymbolTableCreator(final ResolvingConfiguration resolvingConfig, final Deque<MutableScope> scopeStack) {
    super(resolvingConfig, scopeStack);
  }

  @Override
  public void visit(ASTRand node) {
    this.astRand = node;
    randSymbol = new RandSymbol(astRand.getName());
    astRand.setRandSymbol(randSymbol);
    initialize_Rand(randSymbol, astRand);

    addToScopeAndLinkWithNode(randSymbol, astRand);
    setEnclosingScopeOfNodes(astRand);
  }

  @Override
  public void visit(ASTTest node) {
    this.astTest = node;
    testSymbol = new TestSymbol(astTest.getName());
    astTest.setTestSymbol(testSymbol);
    initialize_Test(testSymbol, astTest);

    addToScopeAndLinkWithNode(testSymbol, astTest);
    setEnclosingScopeOfNodes(astTest);
  }

  @Override
  public void visit(ASTReferenceToTest node) {
    setEnclosingScopeOfNodes(node);
  }
}
