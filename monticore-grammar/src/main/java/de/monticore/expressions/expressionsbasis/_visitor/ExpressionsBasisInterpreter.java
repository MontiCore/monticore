/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.values.ErrorValue;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

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
    SymTypeExpression type = TypeCheck3.typeOf(n);
    Optional<ISymbol> symbol = type.getSourceInfo().getSourceSymbol();
    if (symbol.isEmpty()) {
      String errorMsg = "Unknown variable symbol detected";
      Log.error(errorMsg);
      return new ErrorValue(errorMsg);
    }
    
    return load(symbol.get());
  }

  @Override
  public Value interpret(ASTLiteralExpression n) {
    return n.getLiteral().evaluate(getRealThis());
  }

}
