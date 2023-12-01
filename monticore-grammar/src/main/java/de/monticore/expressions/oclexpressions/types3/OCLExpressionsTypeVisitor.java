/*
 *  (c) https://github.com/MontiCore/monticore
 */

package de.monticore.expressions.oclexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.oclexpressions._ast.ASTAnyExpression;
import de.monticore.expressions.oclexpressions._ast.ASTEquivalentExpression;
import de.monticore.expressions.oclexpressions._ast.ASTExistsExpression;
import de.monticore.expressions.oclexpressions._ast.ASTForallExpression;
import de.monticore.expressions.oclexpressions._ast.ASTIfThenElseExpression;
import de.monticore.expressions.oclexpressions._ast.ASTImpliesExpression;
import de.monticore.expressions.oclexpressions._ast.ASTInDeclaration;
import de.monticore.expressions.oclexpressions._ast.ASTIterateExpression;
import de.monticore.expressions.oclexpressions._ast.ASTLetinExpression;
import de.monticore.expressions.oclexpressions._ast.ASTOCLAtPreQualification;
import de.monticore.expressions.oclexpressions._ast.ASTOCLTransitiveQualification;
import de.monticore.expressions.oclexpressions._ast.ASTTypeIfExpression;
import de.monticore.expressions.oclexpressions._visitor.OCLExpressionsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.TypeVisitorLifting;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;

public class OCLExpressionsTypeVisitor extends AbstractTypeVisitor
    implements OCLExpressionsVisitor2 {

  @Override
  public void endVisit(ASTTypeIfExpression expr) {
    // typeif [var] instanceof [type] then [then] else [else]
    SymTypeExpression varResult = expr.getNameSymbol().getType();
    SymTypeExpression typeResult =
        getType4Ast().getPartialTypeOfTypeId(expr.getMCType());
    SymTypeExpression thenResult =
        getType4Ast().getPartialTypeOfExpr(expr.getThenExpression().getExpression());
    SymTypeExpression elseResult =
        getType4Ast().getPartialTypeOfExpr(expr.getElseExpression());

    SymTypeExpression result;
    if (typeResult.isObscureType()
        || varResult.isObscureType()
        || thenResult.isObscureType()
        || elseResult.isObscureType()) {
      // if any inner obscure then error already logged
      result = createObscureType();
    }
    else if (!SymTypeRelations.isSubTypeOf(typeResult, varResult)) {
      if (SymTypeRelations.isSubTypeOf(varResult, typeResult)) {
        Log.error(
            "0xFD290 checking whether '"
                + expr.getName()
                + "' of type "
                + varResult.printFullName()
                + " is of type "
                + typeResult.printFullName()
                + ", which is redundant"
                + ", since it is always true",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
      }
      else {
        Log.error(
            "0xFD289 checking whether '"
                + expr.getName()
                + "' of type "
                + varResult.printFullName()
                + " is of type "
                + typeResult.printFullName()
                + ", which is impossible",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
      }
      result = createObscureType();
    }
    else {
      result = calculateConditionalExpression(thenResult, elseResult);
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTIfThenElseExpression expr) {
    SymTypeExpression conditionResult =
        getType4Ast().getPartialTypeOfExpr(expr.getCondition());
    SymTypeExpression thenResult =
        getType4Ast().getPartialTypeOfExpr(expr.getThenExpression());
    SymTypeExpression elseResult =
        getType4Ast().getPartialTypeOfExpr(expr.getElseExpression());

    SymTypeExpression result;
    if (conditionResult.isObscureType()
        || thenResult.isObscureType()
        || elseResult.isObscureType()) {
      // if any inner obscure then error already logged
      result = createObscureType();
    }
    else if (!SymTypeRelations.isBoolean(conditionResult)) {
      Log.error(
          "0xFD286 condition must be a Boolean expression, "
              + "but is of type "
              + conditionResult.printFullName(),
          expr.getCondition().get_SourcePositionStart(),
          expr.getCondition().get_SourcePositionEnd());
      result = createObscureType();
    }
    else {
      result = calculateConditionalExpression(thenResult, elseResult);
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateConditionalExpression(
      SymTypeExpression thenType, SymTypeExpression elseType) {
    SymTypeExpression result;
    if (thenType.isObscureType() || elseType.isObscureType()) {
      result = createObscureType();
    }
    else {
      result = createUnion(thenType, elseType);
    }
    return result;
  }

  @Override
  public void endVisit(ASTImpliesExpression expr) {
    SymTypeExpression left =
        getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right =
        getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (leftPar, rightPar) ->
                    calculateConditionalBooleanOperation(
                        expr.getLeft(), expr.getRight(), leftPar, rightPar, "implies"))
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTEquivalentExpression expr) {
    SymTypeExpression left =
        getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right =
        getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (leftPar, rightPar) ->
                    calculateConditionalBooleanOperation(
                        expr.getLeft(), expr.getRight(), leftPar, rightPar, "equivalent"))
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateConditionalBooleanOperation(
      ASTExpression left,
      ASTExpression right,
      SymTypeExpression leftResult,
      SymTypeExpression rightResult,
      String operator) {

    if (SymTypeRelations.isBoolean(leftResult) &&
        SymTypeRelations.isBoolean(rightResult)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      // operator not applicable
      Log.error(
          String.format(
              "0xFD207 The operator '%s' is not applicable to "
                  + "the expressions of type '%s' and '%s' "
                  + "but only to expressions of type boolean.",
              operator,
              leftResult.printFullName(),
              rightResult.printFullName()),
          left.get_SourcePositionStart(),
          right.get_SourcePositionEnd());
      return createObscureType();
    }
  }

  @Override
  public void endVisit(ASTForallExpression expr) {
    SymTypeExpression innerType =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (innerPar) ->
                    calculateQuantificationExpression(
                        expr.getExpression(), "ForAllExpression", innerPar))
            .apply(innerType);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTExistsExpression expr) {
    SymTypeExpression innerType =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (innerPar) ->
                    calculateQuantificationExpression(
                        expr.getExpression(), "ExistsExpression", innerPar))
            .apply(innerType);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateQuantificationExpression(
      ASTExpression innerExpr, String exprName, SymTypeExpression innerType) {
    SymTypeExpression result;
    if (SymTypeRelations.isBoolean(innerType)) {
      result =
          SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      Log.error(
          "0xFD208 The type of the expression in the "
              + exprName
              + " is "
              + innerType.printFullName()
              + " but has to be boolean.",
          innerExpr.get_SourcePositionStart(),
          innerExpr.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  @Override
  public void endVisit(ASTAnyExpression expr) {
    SymTypeExpression exprResult =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());

    SymTypeExpression result;
    if (exprResult.isObscureType()) {
      result = createObscureType();
    }
    else if (!isCollection(exprResult)) {
      Log.error(
          "0xFD292 expected a collection for 'any', but got "
              + exprResult.getTypeInfo(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    else {
      result =
          MCCollectionSymTypeRelations.getCollectionElementType(exprResult);
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTLetinExpression expr) {
    boolean obscureDeclaration =
        expr.streamOCLVariableDeclarations()
            .anyMatch(
                decl -> getType4Ast().getPartialTypeOfExpr(decl.getExpression()).isObscureType()
            );

    SymTypeExpression exprResult =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());

    SymTypeExpression result;
    if (exprResult.isObscureType() || obscureDeclaration) {
      result = createObscureType();
    }
    else {
      result = exprResult;
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTIterateExpression expr) {
    SymTypeExpression result;
    // cannot iterate without initialization
    if (!expr.getInit().isPresentExpression()) {
      Log.error(
          "0xFD75C to 'iterate'"
              + "an initialization of the variable '"
              + expr.getInit().getName()
              + "'is required",
          expr.getInit().get_SourcePositionStart(),
          expr.getInit().get_SourcePositionEnd());
      result = createObscureType();
    }
    else {
      SymTypeExpression initResult =
          getType4Ast().getPartialTypeOfExpr(expr.getInit().getExpression());
      SymTypeExpression inDelcResult =
          evaluateInDeclarationType(expr.getIteration());
      SymTypeExpression valueResult =
          getType4Ast().getPartialTypeOfExpr(expr.getValue());

      if (initResult.isObscureType()
          || inDelcResult.isObscureType()
          || valueResult.isObscureType()) {
        result = createObscureType();
      }
      else if (!SymTypeRelations.isCompatible(initResult, valueResult)) {
        Log.error(
            "0xFDC53 unable to assign the result of the expression ("
                + valueResult.printFullName()
                + ") to the variable '"
                + expr.getName()
                + "' of type "
                + initResult.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
        result = createObscureType();
      }
      else {
        result = initResult.deepClone();
      }
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  /**
   * checks the InDeclaration for typing issues, logs on error
   *
   * @return the type of the variable, Obscure on error
   */
  protected SymTypeExpression evaluateInDeclarationType(ASTInDeclaration inDeclaration) {
    Optional<SymTypeExpression> typeResult =
        inDeclaration.isPresentMCType()
            ? Optional.of(getType4Ast().getPartialTypeOfTypeId(inDeclaration.getMCType()))
            : Optional.empty();
    Optional<SymTypeExpression> expressionResult =
        inDeclaration.isPresentExpression()
            ? Optional.of(getType4Ast().getPartialTypeOfExpr(inDeclaration.getExpression()))
            : Optional.empty();
    SymTypeExpression result;

    if (expressionResult.isPresent()
        && !isCollection(expressionResult.get())) {
      Log.error(
          "0xFD297 expected collection after 'in', but got "
              + expressionResult.get().printFullName(),
          inDeclaration.getExpression().get_SourcePositionStart(),
          inDeclaration.getExpression().get_SourcePositionEnd());
      result = createObscureType();
    }
    else if (expressionResult.isPresent()
        && typeResult.isPresent()
        && !SymTypeRelations.isCompatible(
        typeResult.get(),
        MCCollectionSymTypeRelations.getCollectionElementType(expressionResult.get()))) {
      Log.error(
          "0xFD298 cannot assign element of "
              + expressionResult.get().printFullName()
              + " to variable of type "
              + typeResult.get().printFullName(),
          inDeclaration.get_SourcePositionStart(),
          inDeclaration.get_SourcePositionEnd());
      result = createObscureType();
    }
    else if (expressionResult.isEmpty()
        && (SymTypeRelations.isNumericType(typeResult.get())
        || SymTypeRelations.isBoolean(typeResult.get()))) {
      // this is technically not enough,
      // the correct check is whether the type is a domain class ->
      // if it is, one can get all instantiations
      Log.error(
          "0xFD7E4 iterate based on primitives is undefined, "
              + "try providing a collection to iterate over",
          inDeclaration.getMCType().get_SourcePositionStart(),
          inDeclaration.getMCType().get_SourcePositionEnd());
      result = createObscureType();
    }
    else {
      result =
          typeResult.orElseGet(
              () -> MCCollectionSymTypeRelations
                  .getCollectionElementType(expressionResult.get()));
    }
    return result;
  }

  @Override
  public void endVisit(ASTOCLAtPreQualification expr) {
    SymTypeExpression exprResult =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());

    SymTypeExpression result;
    if (exprResult.isObscureType()) {
      result = createObscureType();
    }
    else {
      result = exprResult;
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTOCLTransitiveQualification expr) {
    SymTypeExpression exprResult =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());

    SymTypeExpression result;
    if (exprResult.isObscureType()) {
      result = createObscureType();
    }
    else {
      result = exprResult;
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // hook points

  /**
   * whether a type is a collection-type,
   * this differs between languages (s.a. OCL)
   */
  protected boolean isCollection(SymTypeExpression type) {
    return MCCollectionSymTypeRelations.isList(type) ||
        MCCollectionSymTypeRelations.isSet(type);
  }
}
