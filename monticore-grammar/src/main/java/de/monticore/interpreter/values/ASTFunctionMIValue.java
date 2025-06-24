package de.monticore.interpreter.values;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.InterpreterUtils;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIScope;

import de.monticore.interpreter.ModelInterpreter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;

public class ASTFunctionMIValue implements FunctionMIValue {
  
  protected MIScope parentScope;
  protected List<VariableSymbol> parameterSymbols;
  protected ASTNode body;
  
  public ASTFunctionMIValue(MIScope parentScope, List<VariableSymbol> parameterSymbols, ASTNode body) {
    this.parentScope = parentScope;
    this.parameterSymbols = parameterSymbols;
    this.body = body;
  }
  
  @Override
  public MIValue execute(ModelInterpreter interpreter, List<MIValue> arguments) {
    MIScope newScope = new MIScope(parentScope);
    
    for (int i = 0; i < parameterSymbols.size(); i++) {
      VariableSymbol parameterSymbol = parameterSymbols.get(i);
      SymTypeExpression paramType = parameterSymbol.getType();
      
      MIValue argument = arguments.get(i);
      argument = InterpreterUtils.convertImplicit(paramType, argument);
      if (argument.isError()) return argument;
      
      newScope.declareVariable(parameterSymbol, argument);
    }
    
    interpreter.pushScope(newScope);
    MIValue result = body.evaluate(interpreter);
    interpreter.popScope();
    
    return result;
  }

}
