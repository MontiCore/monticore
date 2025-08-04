package de.monticore.interpreter;

import de.monticore.interpreter.values.VoidMIValue;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;

import java.util.Iterator;
import java.util.Optional;

public class MIForEachIterator implements MIForIterator {
  
  VariableSymbol symbol;
  Iterator<Object> iterator;
  
  public MIForEachIterator(VariableSymbol symbol, Iterator<Object> iterator) {
    this.symbol = symbol;
    this.iterator = iterator;
  }
  
  @Override
  public MIValue execute(ModelInterpreter interpreter, ASTMCStatement body) {
    if (!iterator.hasNext()) return new VoidMIValue();
    
    interpreter.declareVariable(symbol, Optional.empty());
    do {
      SymTypeExpression targetType = symbol.getType();
      Object nextValue = iterator.next();
      MIValue convertedValue = InterpreterUtils.convertImplicit(targetType, MIValueFactory.createValue(nextValue));
      interpreter.storeVariable(symbol, convertedValue);
      
      MIValue statementResult = body.evaluate(interpreter);
      if (statementResult.isContinue()) continue;
      if (statementResult.isBreak()) break;
      if (statementResult.isFlowControlSignal()) return statementResult;
    } while (iterator.hasNext());
    
    return new VoidMIValue();
  }
}
