/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.uglyexpressions._ast.ASTInstanceofExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsHandler;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsTraverser;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.se_rwth.commons.logging.Log;

import static de.monticore.types.check.TypeCheck.compatible;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in JavaClassExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfUglyExpressions
    extends AbstractDeriveFromExpression
    implements UglyExpressionsVisitor2, UglyExpressionsHandler {

  protected UglyExpressionsTraverser traverser;

  protected ISynthesize synthesize;

  @Deprecated
  public DeriveSymTypeOfUglyExpressions() {
    // default behaviour, as this class had no synthezises beforehand
    synthesize = new FullSynthesizeFromMCFullGenericTypes();
  }

  public DeriveSymTypeOfUglyExpressions(ISynthesize synthesize) {
    this.synthesize = synthesize;
  }

  @Override
  public void setTraverser(UglyExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public UglyExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void traverse(ASTInstanceofExpression node) {
    SymTypeExpression expressionResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if (getTypeCheckResult().isType()) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0267 the expression at source position " + node.getExpression().get_SourcePositionStart() + " cannot be a type");
      return;
    }

    SymTypeExpression typeResult = SymTypeExpressionFactory.createObscureType();

    //calculate right type: type that the expression should be an instance of
    deprecated_traverse(node.getMCType());
    if (getTypeCheckResult().isPresentResult()) {
      if (!getTypeCheckResult().isType()) {
        if (!getTypeCheckResult().getResult().isObscureType()) {
          getTypeCheckResult().reset();
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          Log.error("0xA0269 the expression at source position " + node.getMCType().get_SourcePositionStart() + " must be a type");
          return;
        }
      }
      else {
        typeResult = getTypeCheckResult().getResult();
      }
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      logError("0xA0270", node.getExpression().get_SourcePositionStart());
      return;
    }

    if (!expressionResult.isObscureType() && !typeResult.isObscureType()) {
      //the method was not finished yet (either with Log.error or return) -> both types are present and thus the result is boolean
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createPrimitive("boolean");

      getTypeCheckResult().setResult(wholeResult);
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void traverse(ASTTypeCastExpression node) {
    //innerResult is the SymTypeExpression of the type that will be casted into another type
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(node.getExpression());
    if (getTypeCheckResult().isType()) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0262 the expression at source position " + node.getExpression().get_SourcePositionStart() + " cannot be a type");
      return;
    }
    //castResult is the SymTypeExpression of the type the innerResult will be casted to
    SymTypeExpression castResult;

    //castResult is the type in the brackets -> (ArrayList) list

    deprecated_traverse(node.getMCType());
    if (getTypeCheckResult().isPresentResult()) {
      castResult = getTypeCheckResult().getResult();
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0265 the type at source position " + node.getMCType().get_SourcePositionStart() + " cannot be calculated");
      return;
    }

    if (!innerResult.isObscureType() && !castResult.isObscureType()) {
      //wholeResult will be the result of the whole expression
      SymTypeExpression wholeResult = calculateTypeCastExpression(node, castResult, innerResult);

      storeResultOrLogError(wholeResult, node, "0xA0266");
    }
    else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateTypeCastExpression(ASTTypeCastExpression node, SymTypeExpression castResult, SymTypeExpression innerResult) {
    if (compatible(castResult, innerResult) || compatible(innerResult, castResult)) {
      return castResult;
    }
    return SymTypeExpressionFactory.createObscureType();
  }

  // Warning: deprecated and wrong behavior
  // (it does not set whether it's an expression or type identifier)
  // this is fixed in typecheck 3
  // and exists only here like this to not change the behaviour of typecheck1

  @Deprecated
  public void deprecated_traverse(ASTMCType type) {
    SymTypeExpression wholeResult = null;
    TypeCheckResult result = synthesize.synthesizeType(type);
    if (result.isPresentResult()) {
      wholeResult = result.getResult();
    }
    if (wholeResult != null) {
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }
    else {
      getTypeCheckResult().reset();
    }
  }

  @Deprecated
  public void deprecated_traverse(ASTMCReturnType returnType) {
    SymTypeExpression wholeResult = null;
    if (returnType.isPresentMCVoidType()) {
      wholeResult = SymTypeExpressionFactory.createTypeVoid();
    }
    else if (returnType.isPresentMCType()) {
      TypeCheckResult res = synthesize.synthesizeType(returnType);
      if (res.isPresentResult()) {
        wholeResult = res.getResult();
      }
    }
    if (wholeResult != null) {
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }
    else {
      getTypeCheckResult().reset();
    }
  }

  @Deprecated
  public void deprecated_traverse(ASTMCTypeArgument typeArgument) {
    SymTypeExpression wholeResult = null;
    if (typeArgument.getMCTypeOpt().isPresent()) {
      TypeCheckResult res = synthesize.synthesizeType(typeArgument.getMCTypeOpt().get());
      if (res.isPresentResult()) {
        wholeResult = res.getResult();
      }
    }
    if (wholeResult != null) {
      getTypeCheckResult().setResult(wholeResult);
      getTypeCheckResult().setType();
    }
    else {
      getTypeCheckResult().reset();
    }
  }

}
