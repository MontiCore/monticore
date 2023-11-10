package de.monticore.expressions.uglyexpressions;

import de.monticore.expressions.uglyexpressions._ast.ASTInstanceofExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class UglyExpressionsTypeVisitor
    extends AbstractTypeVisitor
    implements UglyExpressionsVisitor2 {

  @Override
  public void endVisit(ASTTypeCastExpression expr) {
    SymTypeExpression typeResult = getType4Ast().getPartialTypeOfTypeId(expr.getMCType());
    SymTypeExpression exprResult = getType4Ast().getPartialTypeOfExpr(expr.getExpression());

    SymTypeExpression result;
    if (typeResult.isObscureType() || exprResult.isObscureType()) {
      // if any inner obscure then error already logged
      result = createObscureType();
    }
    else if (SymTypeRelations.isNumericType(typeResult) && SymTypeRelations.isNumericType(exprResult)) {
      // allow to cast numbers down, e.g., (int) 5.0 or (byte) 5
      result = typeResult;
    }
    else if (SymTypeRelations.isSubTypeOf(exprResult, typeResult)) {
      // check whether typecast is possible
      result = typeResult;
    }
    else {
      Log.error(
          String.format(
              "0xFD204 The expression of type '%s' " + "can't be cast to the given type '%s'.",
              exprResult.printFullName(), typeResult.printFullName()),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTInstanceofExpression expr) {
    SymTypeExpression exprResult = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression typeResult = getType4Ast().getPartialTypeOfTypeId(expr.getMCType());

    SymTypeExpression result;
    if (exprResult.isObscureType() || typeResult.isObscureType()) {
      result = createObscureType();
    }
    else {
      result = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

}
