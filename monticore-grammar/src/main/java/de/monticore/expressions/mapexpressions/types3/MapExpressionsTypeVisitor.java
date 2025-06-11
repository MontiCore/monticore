package de.monticore.expressions.mapexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.mapexpressions._ast.ASTMapEntry;
import de.monticore.expressions.mapexpressions._ast.ASTMapExpression;
import de.monticore.expressions.mapexpressions._visitor.MapExpressionsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapExpressionsTypeVisitor extends AbstractTypeVisitor
    implements MapExpressionsVisitor2 {
  
  @Override
  public void endVisit(ASTMapExpression expr) {
    
    if (getType4Ast().hasTypeOfExpression(expr)) {
      // type already calculated
      return;
    }
    
    SymTypeExpression keyType =
        getContainedExpressionType(expr.getMapEntryList().stream().map(ASTMapEntry::getKey));
    SymTypeExpression valueType =
        getContainedExpressionType(expr.getMapEntryList().stream().map(ASTMapEntry::getValue));
    
    SymTypeExpression result;
    if (keyType.isObscureType() || valueType.isObscureType()) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else {
      result = MCCollectionSymTypeFactory.createMap(keyType, valueType);
    }
    
    getType4Ast().setTypeOfExpression(expr, result);
  }
  
  protected SymTypeExpression getContainedExpressionType(Stream<ASTExpression> exprs) {
    List<SymTypeExpression> exprTypes =
        exprs.map(e -> getType4Ast().getPartialTypeOfExpr(e)).collect(Collectors.toList());
    if (exprTypes.isEmpty() || exprTypes.stream().anyMatch(SymTypeExpression::isObscureType)) {
      return SymTypeExpressionFactory.createObscureType();
    }
    SymTypeExpression unionType = SymTypeExpressionFactory.createUnion(Set.copyOf(exprTypes));
    return SymTypeRelations.normalize(unionType);
  }
}
