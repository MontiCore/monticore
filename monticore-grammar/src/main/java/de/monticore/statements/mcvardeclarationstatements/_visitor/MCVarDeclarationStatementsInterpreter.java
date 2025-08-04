package de.monticore.statements.mcvardeclarationstatements._visitor;

import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.values.VoidMIValue;
import de.monticore.statements.mcvardeclarationstatements._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Optional;

public class MCVarDeclarationStatementsInterpreter extends MCVarDeclarationStatementsInterpreterTOP {
  
  public MCVarDeclarationStatementsInterpreter() {}
  
  public MCVarDeclarationStatementsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  @Override
  public MIValue interpret(ASTLocalVariableDeclarationStatement node) {
    MIValue result = node.getLocalVariableDeclaration().evaluate(getRealThis());
    if (result.isError()) return result;
    return new VoidMIValue();
  }
  
  @Override
  public MIValue interpret(ASTLocalVariableDeclaration node) {
    for (ASTVariableDeclarator declarator : node.getVariableDeclaratorList()) {
      MIValue result = declarator.evaluate(getRealThis());
      if (result.isError()) return result;
    }
    return new VoidMIValue();
  }
  
  @Override
  public MIValue interpret(ASTVariableDeclarator node) {
    VariableSymbol symbol = node.getDeclarator().getSymbol();
    if (node.isPresentVariableInit()) {
      MIValue initialValue = node.getVariableInit().evaluate(getRealThis());
      if (initialValue.isError()) return initialValue;
      getRealThis().declareVariable(symbol, Optional.of(initialValue));
    }
    return new VoidMIValue();
  }
  
  @Override
  public MIValue interpret(ASTSimpleInit node) {
    return node.getExpression().evaluate(getRealThis());
  }
}
