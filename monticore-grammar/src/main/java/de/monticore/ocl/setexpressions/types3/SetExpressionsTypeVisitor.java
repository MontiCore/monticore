package de.monticore.ocl.setexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions.SetExpressionsMill;
import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.util.TypeVisitorLifting;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.SymTypeRelations.isIntegralType;
import static de.monticore.types3.SymTypeRelations.normalize;

public class SetExpressionsTypeVisitor extends AbstractTypeVisitor
    implements SetExpressionsVisitor2 {

  @Override
  public void endVisit(ASTSetInExpression expr) {
    SymTypeExpression elemResult = getType4Ast().getPartialTypeOfExpr(expr.getElem());
    SymTypeExpression setResult = getType4Ast().getPartialTypeOfExpr(expr.getSet());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (elemPar, setPar) -> calculateSetInExpression(expr, elemPar, setPar))
            .apply(elemResult, setResult);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTSetNotInExpression expr) {
    SymTypeExpression elemResult = getType4Ast().getPartialTypeOfExpr(expr.getElem());
    SymTypeExpression setResult = getType4Ast().getPartialTypeOfExpr(expr.getSet());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (elemPar, setPar) -> calculateSetInExpression(expr, elemPar, setPar))
            .apply(elemResult, setResult);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateSetInExpression(
      ASTExpression expr, SymTypeExpression elemResult, SymTypeExpression setResult) {
    SymTypeExpression result;

    if (isSetOrListCollection(setResult)) {
      SymTypeExpression setElemType = MCCollectionSymTypeRelations.getCollectionElementType(setResult);
      // it does not make any sense to ask if it is in the set
      // if it cannot be in the set
      if (MCCollectionSymTypeRelations.isSubTypeOf(elemResult, setElemType)
          || MCCollectionSymTypeRelations.isSubTypeOf(setElemType, elemResult)) {
        result = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
      }
      else {
        Log.error(
            "0xFD541 tried to check whether a "
                + elemResult.printFullName()
                + " is in the collection of "
                + setElemType.printFullName()
                + ", which is impossible",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
        result = createObscureType();
      }
    }
    else {
      Log.error(
          "0xFD542 tried to check whether a "
              + elemResult.printFullName()
              + " is in "
              + setResult.printFullName()
              + ", which is not a collection",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  @Override
  public void endVisit(ASTUnionExpression expr) {
    // union of two sets -> both sets need to have the same type
    // or their types need to be sub/super
    // types
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (leftPar, rightPar) -> calculateUnionExpression(expr, leftPar, rightPar))
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  public SymTypeExpression calculateUnionExpression(
      ASTExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    SymTypeExpression result;

    if (isSetOrListCollection(leftResult)
        && isSetOrListCollection(rightResult)) {
      Optional<SymTypeExpression> lub =
          MCCollectionSymTypeRelations.leastUpperBound(leftResult, rightResult);
      if (lub.isPresent() && !lub.get().isObscureType()) {
        result = lub.get();
      }
      else {
        Log.error(
            "0xFD543 could not calculate a least upper bound of "
                + leftResult.printFullName()
                + " and "
                + rightResult.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
        result = createObscureType();
      }
    }
    else {
      Log.error(
          "0xFD544 expected two lists, sets, ..., instead got "
              + leftResult.printFullName()
              + " and "
              + rightResult.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  @Override
  public void endVisit(ASTIntersectionExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (leftPar, rightPar) -> calculateIntersectionAndMinusOperation(expr, leftPar, rightPar))
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTSetMinusExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (leftPar, rightPar) -> calculateIntersectionAndMinusOperation(expr, leftPar, rightPar))
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  public SymTypeExpression calculateIntersectionAndMinusOperation(
      ASTExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    SymTypeExpression result;

    if (MCCollectionSymTypeRelations.isSet(leftResult)
        && MCCollectionSymTypeRelations.isSet(rightResult)) {
      Optional<SymTypeExpression> lub =
          MCCollectionSymTypeRelations.leastUpperBound(leftResult, rightResult);
      if (lub.isPresent() && !lub.get().isObscureType()) {
        result = lub.get();
      }
      else {
        Log.error(
            "0xFD540 could not calculate a least upper bound of "
                + leftResult.printFullName()
                + " and "
                + rightResult.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
        result = createObscureType();
      }
    }
    else {
      Log.error(
          "0xFD546 expected two Sets, instead got "
              + leftResult.printFullName()
              + " and "
              + rightResult.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  @Override
  public void endVisit(ASTSetUnionExpression expr) {
    SymTypeExpression collOfColl =
        getType4Ast().getTypeOfExpression(expr.getSet());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (innerType) -> calculateSetUnionExpression(expr, innerType))
            .apply(collOfColl);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateSetUnionExpression(
      ASTExpression expr, SymTypeExpression innerType) {
    SymTypeExpression result;
    if (isSetOrListCollection(innerType)) {
      // we need a collection of collections
      SymTypeExpression innerInnerType =
          MCCollectionSymTypeRelations.getCollectionElementType(innerType);
      result =
          calculateUnionExpression(expr, innerInnerType, innerInnerType);
    }
    else {
      Log.error("0xFD530 expected a collection of collections, but got "
              + innerType.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      result = createObscureType();
    }
    return result;
  }

  @Override
  public void endVisit(ASTSetIntersectionExpression expr) {
    SymTypeExpression collOfColl =
        getType4Ast().getTypeOfExpression(expr.getSet());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (innerType) -> calculateSetIntersectionExpression(expr, innerType))
            .apply(collOfColl);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateSetIntersectionExpression(
      ASTExpression expr, SymTypeExpression innerType) {
    SymTypeExpression result;
    if (isSetOrListCollection(innerType)) {
      // we need a collection of collections
      SymTypeExpression innerInnerType =
          MCCollectionSymTypeRelations.getCollectionElementType(innerType);
      result = calculateIntersectionAndMinusOperation(
          expr, innerInnerType, innerInnerType
      );
    }
    else {
      Log.error("0xFD531 expected a collection of sets, but got "
              + innerType.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      result = createObscureType();
    }
    return result;
  }

  @Override
  public void endVisit(ASTSetAndExpression expr) {
    SymTypeExpression setResult = getType4Ast().getPartialTypeOfExpr(expr.getSet());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
            (setPar) -> calculateLogicalSetExpression(expr, setPar)
        ).apply(setResult);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTSetOrExpression expr) {
    SymTypeExpression setResult = getType4Ast().getPartialTypeOfExpr(expr.getSet());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
            (setPar) -> calculateLogicalSetExpression(expr, setPar)
        ).apply(setResult);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateLogicalSetExpression(
      ASTExpression expr, SymTypeExpression setType) {
    SymTypeExpression result;
    if (isSetOrListCollection(setType)
        && MCCollectionSymTypeRelations.isBoolean(
        MCCollectionSymTypeRelations.getCollectionElementType(setType))) {
      result = createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      Log.error(
          "0xFD545 expected Collection of booleans, but got "
              + setType.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  @Override
  public void endVisit(ASTSetComprehension expr) {
    boolean isObscure = false;
    SymTypeExpression result;

    // check all SetComprehension items
    for (ASTSetComprehensionItem item : expr.getSetComprehensionItemList()) {
      if (item.isPresentExpression()) {
        ASTExpression boolExpr = item.getExpression();
        SymTypeExpression boolExprType =
            getType4Ast().getPartialTypeOfExpr(boolExpr);
        if (boolExprType.isObscureType()) {
          isObscure = true;
        }
        else if (!MCCollectionSymTypeRelations.isBoolean(boolExprType)) {
          Log.error(
              "0xFD554 filter expression in set comprehension "
                  + "need to be Boolean expressions, but got "
                  + boolExprType.printFullName(),
              boolExpr.get_SourcePositionStart(),
              boolExpr.get_SourcePositionEnd());
          isObscure = true;
        }
      }
      else if (item.isPresentSetVariableDeclaration()) {
        if (!checkSetVariableDeclaration(item.getSetVariableDeclaration())) {
          isObscure = true;
        }
      }
      else if (item.isPresentGeneratorDeclaration()) {
        if (!checkGeneratorDeclaration(item.getGeneratorDeclaration())) {
          isObscure = true;
        }
      }
    }

    // now we try to find the type of the collection
    if (!isObscure) {
      SymTypeExpression elementType;
      if (expr.getLeft().isPresentExpression()) {
        elementType =
            getType4Ast().getPartialTypeOfExpr(expr.getLeft().getExpression());
      }
      else if (expr.getLeft().isPresentGeneratorDeclaration()) {
        if (checkGeneratorDeclaration(expr.getLeft().getGeneratorDeclaration())) {
          elementType =
              expr.getLeft().getGeneratorDeclaration().getSymbol().getType();
        }
        else {
          elementType = createObscureType();
        }
      }
      else {
        if (expr.getLeft().getSetVariableDeclaration().isPresentExpression()) {
          Log.error("0xFD536 no variable assignment allowed"
                  + " on left side of \"|\"",
              expr.getLeft().get_SourcePositionStart(),
              expr.getLeft().get_SourcePositionEnd()
          );
          elementType = createObscureType();
        }
        else {
          elementType =
              expr.getLeft().getSetVariableDeclaration().getSymbol().getType();
        }
      }
      if (!elementType.isObscureType()) {
        if (expr.isSet()) {
          result = MCCollectionSymTypeFactory.createSet(elementType);
        }
        else {
          result = MCCollectionSymTypeFactory.createList(elementType);
        }
      }
      else {
        result = createObscureType();
      }
    }
    else {
      result = createObscureType();
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected boolean checkSetVariableDeclaration(ASTSetVariableDeclaration varDecl) {
    // note: this differs from OCL,
    // in OCL, 'Type name' is valid and uses all instances of Type
    // we require an expression to be present as well.

    // Expression
    if (!varDecl.isPresentExpression()) {
      Log.error(
          "0xFD537 SetVariableDeclaration requires an assigment to "
              + varDecl.getName(),
          varDecl.get_SourcePositionStart(),
          varDecl.get_SourcePositionEnd());
      return false;
    }
    SymTypeExpression exprType =
        getType4Ast().getPartialTypeOfExpr(varDecl.getExpression());
    if (exprType.isObscureType()) {
      return false;
    }

    // MCType
    if (!varDecl.isPresentMCType()) {
      return true;
    }
    SymTypeExpression mCType =
        getType4Ast().getPartialTypeOfTypeId(varDecl.getMCType());
    // check if an error already has been logged
    if (mCType.isObscureType()) {
      return false;
    }

    SymTypeExpression assigneeType;
    if (varDecl.sizeDim() == 0) {
      assigneeType = mCType;
    }
    else {
      assigneeType =
          SymTypeExpressionFactory.createTypeArray(mCType, varDecl.sizeDim());
    }
    if (!MCCollectionSymTypeRelations.isCompatible(assigneeType, exprType)) {
      Log.error(
          "0xFD547 cannot assign"
              + exprType.printFullName()
              + " to "
              + assigneeType.printFullName(),
          varDecl.get_SourcePositionStart(),
          varDecl.get_SourcePositionEnd());
      return false;
    }
    return true;
  }

  protected boolean checkGeneratorDeclaration(ASTGeneratorDeclaration genDecl) {
    SymTypeExpression exprType =
        getType4Ast().getPartialTypeOfExpr(genDecl.getExpression());
    if (exprType.isObscureType()) {
      return false;
    }
    if (!isSetOrListCollection(exprType)) {
      Log.error(
          "0xFD548 expected a collection for generator declaration,"
              + " but got "
              + exprType.printFullName(),
          genDecl.getExpression().get_SourcePositionStart(),
          genDecl.getExpression().get_SourcePositionEnd());
      return false;
    }
    SymTypeExpression elementType =
        MCCollectionSymTypeRelations.getCollectionElementType(exprType);
    if (genDecl.isPresentMCType()) {
      SymTypeExpression mCType =
          getType4Ast().getPartialTypeOfTypeId(genDecl.getMCType());
      if (!mCType.isObscureType() &&
          !MCCollectionSymTypeRelations.isCompatible(mCType, elementType)) {
        Log.error(
            "0xFD549 cannot assign elements of collection of type "
                + exprType.printFullName()
                + " to "
                + mCType.printFullName(),
            genDecl.get_SourcePositionStart(),
            genDecl.get_SourcePositionEnd());
        return false;
      }
    }
    return true;
  }

  @Override
  public void endVisit(ASTSetEnumeration expr) {
    SymTypeExpression result;
    List<SymTypeExpression> containedExprTypes = getContainedExpressionTypes(expr);
    if (containedExprTypes.stream().anyMatch(SymTypeExpression::isObscureType)) {
      result = createObscureType();
    }
    else if (!assertRangesContainIntegrals(expr)) {
      result = createObscureType();
    }
    else if (getType4Ast().hasTypeOfExpression(expr)) {
      // type already calculated
      return;
    }
    else {
      SymTypeExpression elementType = createUnion(Set.copyOf(containedExprTypes));
      if (expr.isSet()) {
        result = MCCollectionSymTypeFactory.createSet(elementType);
      }
      else {
        result = MCCollectionSymTypeFactory.createList(elementType);
      }
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTSetValueRange expr) {
    SymTypeExpression leftResult = getType4Ast().getPartialTypeOfExpr(expr.getLowerBound());
    SymTypeExpression rightResult = getType4Ast().getPartialTypeOfExpr(expr.getUpperBound());
    if (!leftResult.isObscureType() && !rightResult.isObscureType()) {
      if (!isIntegralType(normalize(leftResult))
          || !isIntegralType(normalize(rightResult))) {
        Log.error(
            "0xFD217 bounds in SetValueRange "
                + "are not integral types, but have to be, got "
                + leftResult.printFullName()
                + " and "
                + rightResult.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
      }
    }
  }

  // hook points

  /**
   * whether a type is a Set or List,
   * this differs between languages (s.a. OCL)
   */
  protected boolean isSetOrListCollection(SymTypeExpression type) {
    return MCCollectionSymTypeRelations.isList(type) ||
        MCCollectionSymTypeRelations.isSet(type);
  }

  // Helper

  /**
   * Get all expressions within the set enumeration.
   * E.g.: "{1, 2..4}" -> "1","2","4"
   * Returns empty on error (will have been logged)
   */
  protected Optional<List<ASTExpression>> getContainedExpressions(ASTSetEnumeration expr) {
    List<ASTExpression> containedExprs = new ArrayList<>();
    for (ASTSetCollectionItem cItem : expr.getSetCollectionItemList()) {
      if (SetExpressionsMill.typeDispatcher().isSetExpressionsASTSetValueItem(cItem)) {
        ASTSetValueItem setValueItem =
            SetExpressionsMill.typeDispatcher().asSetExpressionsASTSetValueItem(cItem);
        containedExprs.add(setValueItem.getExpression());
      }
      else if (SetExpressionsMill.typeDispatcher().isSetExpressionsASTSetValueRange(cItem)) {
        ASTSetValueRange valueRange =
            SetExpressionsMill.typeDispatcher().asSetExpressionsASTSetValueRange(cItem);
        containedExprs.add(valueRange.getLowerBound());
        containedExprs.add(valueRange.getUpperBound());
      }
      else {
        Log.error(
            "0xFD550 internal error: "
                + "unexpected subtype of ASTSetCollectionItem",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd());
        return Optional.empty();
      }
    }
    return Optional.of(containedExprs);
  }

  /**
   * Get all expressions' types within the set enumeration.
   * They need to be stored in Type4Ast before calling this.
   * E.g.: "{1, 2..4}" -> int, int, int
   * May contain Obscure (error will have been logged).
   */
  protected List<SymTypeExpression> getContainedExpressionTypes(ASTSetEnumeration expr) {
    Optional<List<ASTExpression>> containedExprs =
        getContainedExpressions(expr);
    if (containedExprs.isEmpty()) {
      return List.of(createObscureType());
    }
    List<SymTypeExpression> exprTypes =
        new ArrayList<>(containedExprs.get().size());
    for (ASTExpression containedExpr : containedExprs.get()) {
      exprTypes.add(getType4Ast().getPartialTypeOfExpr(containedExpr));
    }
    return exprTypes;
  }

  /**
   * s. {@link #assertRangeContainsIntegrals(ASTSetValueRange)}
   */
  protected boolean assertRangesContainIntegrals(ASTSetEnumeration expr) {
    for (ASTSetCollectionItem cItem : expr.getSetCollectionItemList()) {
      if (SetExpressionsMill.typeDispatcher()
          .isSetExpressionsASTSetValueRange(cItem)
      ) {
        ASTSetValueRange valueRange = SetExpressionsMill.typeDispatcher()
            .asSetExpressionsASTSetValueRange(cItem);
        if (!assertRangeContainsIntegrals(valueRange)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Logs an error if the lower and upper bounds
   * of the range are not integral types.
   *
   * @return true iff no error occurred.
   */
  protected boolean assertRangeContainsIntegrals(ASTSetValueRange range) {
    // each contained type has to be numeric
    SymTypeExpression lowerRangeType =
        getType4Ast().getPartialTypeOfExpr(range.getLowerBound());
    SymTypeExpression upperRangeType =
        getType4Ast().getPartialTypeOfExpr(range.getUpperBound());
    if (lowerRangeType.isObscureType() || upperRangeType.isObscureType()) {
      return true;
    }
    else if (!isIntegralType(normalize(lowerRangeType))
        || !isIntegralType(normalize(upperRangeType))) {
      Log.error(
          "0xFD551 expected integral types in value range, "
              + "but got "
              + lowerRangeType.printFullName()
              + " and "
              + upperRangeType.printFullName(),
          range.get_SourcePositionStart(),
          range.get_SourcePositionEnd()
      );
      return false;
    }
    return true;
  }
}
