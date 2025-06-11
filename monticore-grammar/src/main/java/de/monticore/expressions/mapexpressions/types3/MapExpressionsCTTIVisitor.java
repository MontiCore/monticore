package de.monticore.expressions.mapexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.mapexpressions._ast.ASTMapEntry;
import de.monticore.expressions.mapexpressions._ast.ASTMapExpression;
import de.monticore.expressions.mapexpressions._visitor.MapExpressionsHandler;
import de.monticore.expressions.mapexpressions._visitor.MapExpressionsTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.*;

public class MapExpressionsCTTIVisitor extends MapExpressionsTypeVisitor
    implements MapExpressionsHandler {
  
  protected MapExpressionsTraverser traverser;
  
  @Override
  public MapExpressionsTraverser getTraverser() {
    return traverser;
  }
  
  @Override
  public void setTraverser(MapExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  
  @Override
  public void handle(ASTMapExpression expr) {
    List<ASTExpression> keyExprs =
        expr.getMapEntryList().stream().map(ASTMapEntry::getKey).collect(Collectors.toList());
    List<ASTExpression> valueExprs =
        expr.getMapEntryList().stream().map(ASTMapEntry::getValue).collect(Collectors.toList());
    
    if (keyExprs.size() != valueExprs.size()) {
      getType4Ast().setTypeOfExpression(expr, SymTypeExpressionFactory.createObscureType());
    }
    else {
      SymTypeOfFunction exprFunc;
      if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
        exprFunc = getInferenceContext4Ast().getResolvedOfExpression(expr).asFunctionType();
      }
      else {
        SymTypeInferenceVariable keyTypeVar = createInferenceVariable();
        SymTypeInferenceVariable valueTypeVar = createInferenceVariable();
        List<SymTypeExpression> argTypes =
            Stream.concat(Collections.nCopies(keyExprs.size(), keyTypeVar).stream(),
                    Collections.nCopies(valueExprs.size(), valueTypeVar).stream())
                .collect(Collectors.toList());
        exprFunc =
            createFunction(MCCollectionSymTypeFactory.createMap(keyTypeVar, valueTypeVar), argTypes,
                false);
        getInferenceContext4Ast().setResolvedOfExpression(expr, exprFunc);
      }
      List<ASTExpression> combinedExprs =
          Stream.concat(keyExprs.stream(), valueExprs.stream()).collect(Collectors.toList());
      CompileTimeTypeCalculator.handleCall(expr, exprFunc.getWithFixedArity(combinedExprs.size()),
          combinedExprs, getTraverser(), getType4Ast(), getInferenceContext4Ast());
    }
    if (getType4Ast().hasPartialTypeOfExpression(expr) && !getType4Ast().getPartialTypeOfExpr(expr)
        .isObscureType()) {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
  }
}
