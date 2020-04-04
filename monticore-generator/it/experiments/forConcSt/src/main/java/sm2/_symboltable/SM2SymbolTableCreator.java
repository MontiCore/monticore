/* (c) https://github.com/MontiCore/monticore */
package sm2._symboltable;

import de.se_rwth.commons.logging.Log;
import sm2._ast.ASTAutomaton;
import sm2._ast.ASTState;

import java.util.Deque;

public class SM2SymbolTableCreator extends SM2SymbolTableCreatorTOP {
  
  public SM2SymbolTableCreator(ISM2Scope enclosingScope) {
    super(enclosingScope);
  }
  
  public SM2SymbolTableCreator(Deque<? extends ISM2Scope> scopeStack) {
    super(scopeStack);
  }
  
  @Override
  public void visit(final ASTAutomaton automatonNode) {
    final AutomatonSymbol automaton = new AutomatonSymbol(automatonNode.getName());
    addToScopeAndLinkWithNode(automaton, automatonNode);
  }
  
  @Override
  public void visit(final ASTState stateNode) {
    final StateSymbol stateSymbol = new StateSymbol(stateNode.getName());
    
    Log.info("StateSymbol defined for " + stateSymbol.getName(), SM2SymbolTableCreator.class.getName());

    addToScopeAndLinkWithNode(stateSymbol, stateNode);
  }

}
