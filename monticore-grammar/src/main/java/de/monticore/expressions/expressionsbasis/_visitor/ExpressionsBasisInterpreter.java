/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.VariableMIValue;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
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
  public MIValue interpret(ASTNameExpression n) {
    SymTypeExpression type = TypeCheck3.typeOf(n);
    if (type.isFunctionType() && type.asFunctionType().hasSymbol()) {
      Optional<FunctionSymbol> symbol = type.getSourceInfo().getSourceSymbol().map(s -> (FunctionSymbol)s);
      if (symbol.isEmpty()) {
        String errorMsg = "0x57053 Cannot resolve function '" + n.getName() + "'.";
        Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
        return new ErrorMIValue(errorMsg);
      }
      return loadFunction(symbol.get());
    }
    
    Optional<VariableSymbol> symbol = type.getSourceInfo().getSourceSymbol().map(s -> (VariableSymbol)s);
    if (symbol.isEmpty()) {
      String errorMsg = "0x57054 Cannot resolve variable '" + n.getName() + "'.";
      Log.error(errorMsg, n.get_SourcePositionStart(), n.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    return new VariableMIValue(getCurrentScope(), symbol.get());
  }

  @Override
  public MIValue interpret(ASTLiteralExpression n) {
    return n.getLiteral().evaluate(getRealThis());
  }

}
