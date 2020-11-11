package sm2._symboltable;

import de.se_rwth.commons.logging.Log;
import sm2._ast.ASTAutomaton;
import sm2._ast.ASTState;

import java.util.Deque;

public class SM2ScopeSkeletonCreator extends SM2ScopeSkeletonCreatorTOP {

  public SM2ScopeSkeletonCreator(ISM2Scope enclosingScope) {
    super(enclosingScope);
  }

  public SM2ScopeSkeletonCreator(Deque<? extends ISM2Scope> scopeStack) {
    super(scopeStack);
  }

  public SM2ScopeSkeletonCreator(){
    super();
  }

  @Override
  public void visit(final ASTAutomaton automatonNode) {
    final AutomatonSymbol automaton = new AutomatonSymbol(automatonNode.getName());
    addToScopeAndLinkWithNode(automaton, automatonNode);
  }

  @Override
  public void visit(final ASTState stateNode) {
    final StateSymbol stateSymbol = new StateSymbol(stateNode.getName());

    Log.info("StateSymbol defined for " + stateSymbol.getName(), "SM2SymbolTableCreator");

    addToScopeAndLinkWithNode(stateSymbol, stateNode);
  }

}
