/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.values.NotAValue;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Optional;

public class ExpressionsBasisInterpreter extends ExpressionsBasisInterpreterTOP {

  public ExpressionsBasisInterpreter() {
    super();
  }

  public ExpressionsBasisInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  @Override
  public Value interpret(ASTNameExpression n) {
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope) n.getEnclosingScope()).resolveVariable(n.getName());
    if (symbol.isPresent()) {
      return load(symbol.get());
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTLiteralExpression n) {
    return n.getLiteral().evaluate(getRealThis());
  }

}
