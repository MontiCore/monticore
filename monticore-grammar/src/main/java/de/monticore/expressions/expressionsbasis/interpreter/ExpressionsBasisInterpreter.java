// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.expressionsbasis.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisInheritanceHandler;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.interpreter.util.SymbolAccessHandler;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;

import java.util.Optional;

/**
 * Interpreter Visitor for ExpressionsBasis
 */
public class ExpressionsBasisInterpreter
    extends ExpressionsBasisInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  protected SymbolAccessHandler symbolAccessHandler =
      new SymbolAccessHandler();

  public ExpressionsBasisInterpreter(InterpreterDataForBasicSymbols iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    SymTypeExpression exprType = TypeCheck3.typeOf(node);
    Optional<ISymbol> sourceSymOpt = exprType.getSourceInfo().getSourceSymbol();
    Preconditions.checkState(sourceSymOpt.isPresent());
    ISymbol sourceSym = sourceSymOpt.get();
    SymbolAccessHandler.SymbolAccess symbolAccess = symbolAccessHandler
        .getSymbolAccess(sourceSym, iData.getFrameLayoutStack().peek(), iData);
    iData.putCalculation(symbolAccess.getter());
    if (symbolAccess.setter().isPresent()) {
      iData.putSetter(symbolAccess.setter().get());
    }
  }

}
