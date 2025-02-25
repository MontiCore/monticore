package de.monticore.interpreter.values;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIScope;

import de.monticore.interpreter.ModelInterpreter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.List;

public class ASTFunctionMIValue extends FunctionMIValue {
  
  protected ASTNode body;
  
  public ASTFunctionMIValue(MIScope parentScope, List<VariableSymbol> parameterSymbols, ASTNode body) {
    super(parentScope, parameterSymbols);
    this.body = body;
  }
  
  @Override
  public MIValue execute(ModelInterpreter interpreter, List<MIValue> arguments) {
    MIScope newScope = new MIScope(parentScope);
    
    for (int i = 0; i < parameterSymbols.size(); i++) {
      newScope.declareVariable(parameterSymbols.get(i), arguments.get(i));
    }
    
    interpreter.pushScope(newScope);
    MIValue result = body.evaluate(interpreter);
    interpreter.popScope();
    
    return result;
  }

}
