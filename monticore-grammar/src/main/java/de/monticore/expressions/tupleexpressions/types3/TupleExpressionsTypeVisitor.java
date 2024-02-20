/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.tupleexpressions.types3;

import de.monticore.expressions.tupleexpressions._ast.ASTTupleExpression;
import de.monticore.expressions.tupleexpressions._visitor.TupleExpressionsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class TupleExpressionsTypeVisitor extends AbstractTypeVisitor
    implements TupleExpressionsVisitor2 {

  @Override
  public void endVisit(ASTTupleExpression expr) {
    Log.errorIfNull(expr);
    SymTypeExpression result;
    List<SymTypeExpression> listedTypes = expr.streamExpressions()
        .map(e -> getType4Ast().getPartialTypeOfExpr(e))
        .collect(Collectors.toList());

    if (listedTypes.stream().anyMatch(SymTypeExpression::isObscureType)) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else {
      // note: per grammar, the number of listed types is at least 2
      result = SymTypeExpressionFactory.createTuple(listedTypes);
    }

    getType4Ast().setTypeOfExpression(expr, result);
  }

}
