package mc.feature.scopes.supautomaton._symboltable;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.feature.scopes.supautomaton._ast.ASTSup;
import mc.feature.scopes.superautomaton._ast.ASTAutomaton;
import mc.feature.scopes.superautomaton._ast.ASTState;
import mc.feature.scopes.superautomaton._symboltable.AutomatonSymbol;
import mc.feature.scopes.superautomaton._symboltable.StateSymbol;

public class SupAutomatonSymbolTableCreator extends SupAutomatonSymbolTableCreatorTOP {

  public SupAutomatonSymbolTableCreator(ResolvingConfiguration resolvingConfig, MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  @Override
  public void visit(ASTState node)
  {
    addToScopeAndLinkWithNode(new StateSymbol(node.getName()), node);
  }

  @Override
  public void visit(ASTSup ast) {
    MutableScope scope = create_Sup(ast);
    initialize_Sup(scope, ast);
    putOnStack(scope);
    setLinkBetweenSpannedScopeAndNode(scope, ast);
    setEnclosingScopeOfNodes(ast);
  }

  @Override
  public void visit(ASTAutomaton node) {
    addToScopeAndLinkWithNode(new AutomatonSymbol(node.getName()), node);
  }


  @Override
  protected MutableScope create_Sup(ASTSup ast) {
    // creates new shadowing scope
    SupAutomatonScope a = new SupAutomatonScope(true);
    a.setExportsSymbols(true);
    return a;
  }

  @Override
  public void endVisit(final ASTAutomaton automatonNode) {
    removeCurrentScope();

    setEnclosingScopeOfNodes(automatonNode);
  }

  @Override
  public void endVisit(final ASTState node) {
    removeCurrentScope();

    setEnclosingScopeOfNodes(node);
  }
}
