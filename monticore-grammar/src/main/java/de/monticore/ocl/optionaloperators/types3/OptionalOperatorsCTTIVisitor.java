// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.optionaloperators.types3;

import de.monticore.ocl.optionaloperators._ast.ASTOptionalEmptyExpression;
import de.monticore.ocl.optionaloperators._visitor.OptionalOperatorsHandler;
import de.monticore.ocl.optionaloperators._visitor.OptionalOperatorsTraverser;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createInferenceVariable;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory.createOptional;

public class OptionalOperatorsCTTIVisitor
    extends OptionalOperatorsTypeVisitor
    implements OptionalOperatorsHandler {

  protected static final String OPT_EMPTY_NO_TARGET_TYPE_ERROR_CODE = "0xFDB76";

  protected OptionalOperatorsTraverser traverser;

  @Override
  public OptionalOperatorsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(OptionalOperatorsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTOptionalEmptyExpression expr) {
    if (!getInferenceContext4Ast().getContextOfExpression(expr).hasTargetType()) {
      Log.error(
          OPT_EMPTY_NO_TARGET_TYPE_ERROR_CODE
              + " empty Optional expression requires a target type",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      getType4Ast().setTypeOfExpression(expr, createObscureType());
      return;
    }

    SymTypeOfFunction exprFunc;
    if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
      exprFunc = getInferenceContext4Ast().getResolvedOfExpression(expr)
          .asFunctionType();
    }
    else {
      SymTypeInferenceVariable typeVar = createInferenceVariable();
      exprFunc = createFunction(createOptional(typeVar));
      getInferenceContext4Ast().setResolvedOfExpression(expr, exprFunc);
    }
    CompileTimeTypeCalculator.handleCall(
        expr, exprFunc, List.of(),
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
  }

}
