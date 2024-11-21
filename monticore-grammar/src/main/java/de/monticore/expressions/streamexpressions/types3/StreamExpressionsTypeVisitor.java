package de.monticore.expressions.streamexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTConcatStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTLengthStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.streams.StreamSymTypeFactory;
import de.monticore.types3.streams.StreamSymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.SymTypeRelations.normalize;

public class StreamExpressionsTypeVisitor extends AbstractTypeVisitor
    implements StreamExpressionsVisitor2 {

  @Override
  public void endVisit(ASTStreamConstructorExpression expr) {
    if (type4Ast.hasTypeOfExpression(expr)) {
      // already calculated
      return;
    }
    SymTypeExpression result;
    List<SymTypeExpression> containedExprTypes = expr.getExpressionList().stream()
        .map(e -> getType4Ast().getPartialTypeOfExpr(e))
        .collect(Collectors.toList());
    if (containedExprTypes.stream().anyMatch(SymTypeExpression::isObscureType)) {
      result = createObscureType();
    }
    else if (getType4Ast().hasTypeOfExpression(expr)) {
      // type already calculated
      return;
    }
    else {
      SymTypeExpression elementType = createUnion(Set.copyOf(containedExprTypes));
      if (expr.isEventTimed()) {
        result = StreamSymTypeFactory.createEventStream(elementType);
      }
      else if (expr.isSyncTimed()) {
        result = StreamSymTypeFactory.createSyncStream(elementType);
      }
      else if (expr.isToptTimed()) {
        result = StreamSymTypeFactory.createToptStream(elementType);
      }
      else if (expr.isUntimed()) {
        result = StreamSymTypeFactory.createUntimedStream(elementType);
      }
      else {
        result = StreamSymTypeFactory.createStream(elementType);
      }
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTAppendStreamExpression expr) {
    if (type4Ast.hasTypeOfExpression(expr)) {
      // already calculated
      return;
    }
    SymTypeExpression result;
    SymTypeExpression leftType =
        normalize(getType4Ast().getPartialTypeOfExpr(expr.getLeft()));
    SymTypeExpression rightType =
        normalize(getType4Ast().getPartialTypeOfExpr(expr.getRight()));
    if (leftType.isObscureType() || rightType.isObscureType()) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else if (!assertIsStream(expr.getRight(), rightType)) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else {
      SymTypeExpression rightElementType =
          StreamSymTypeRelations.getStreamElementType(rightType);
      SymTypeExpression fusedElementType = SymTypeExpressionFactory
          .createUnion(leftType, rightElementType);
      if (StreamSymTypeRelations.isEventStream(rightType)) {
        result = StreamSymTypeFactory.createEventStream(fusedElementType);
      }
      else if (StreamSymTypeRelations.isSyncStream(rightType)) {
        result = StreamSymTypeFactory.createSyncStream(fusedElementType);
      }
      else if (StreamSymTypeRelations.isToptStream(rightType)) {
        result = StreamSymTypeFactory.createToptStream(fusedElementType);
      }
      else if (StreamSymTypeRelations.isUntimedStream(rightType)) {
        result = StreamSymTypeFactory.createUntimedStream(fusedElementType);
      }
      else {
        result = StreamSymTypeFactory.createStream(fusedElementType);
      }
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTConcatStreamExpression expr) {
    if (type4Ast.hasTypeOfExpression(expr)) {
      // already calculated
      return;
    }
    SymTypeExpression result;
    SymTypeExpression leftType =
        normalize(getType4Ast().getPartialTypeOfExpr(expr.getLeft()));
    SymTypeExpression rightType =
        normalize(getType4Ast().getPartialTypeOfExpr(expr.getRight()));
    if (leftType.isObscureType() || rightType.isObscureType()) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else if (!assertIsStream(expr.getLeft(), leftType)
        || !assertIsStream(expr.getRight(), rightType)) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else {
      SymTypeExpression leftElementType =
          StreamSymTypeRelations.getStreamElementType(leftType);
      SymTypeExpression rightElementType =
          StreamSymTypeRelations.getStreamElementType(rightType);
      SymTypeExpression fusedElementType =
          SymTypeExpressionFactory.createUnion(
              leftElementType, rightElementType
          );
      if (StreamSymTypeRelations.isStreamOfUnknownSubType(leftType) ||
          StreamSymTypeRelations.isStreamOfUnknownSubType(rightType)) {
        result = StreamSymTypeFactory.createStream(fusedElementType);
      }
      else if (StreamSymTypeRelations.isEventStream(leftType) &&
          StreamSymTypeRelations.isEventStream(rightType)
      ) {
        result = StreamSymTypeFactory.createEventStream(fusedElementType);
      }
      else if (StreamSymTypeRelations.isSyncStream(leftType) &&
          StreamSymTypeRelations.isSyncStream(rightType)
      ) {
        result = StreamSymTypeFactory.createSyncStream(fusedElementType);
      }
      else if (StreamSymTypeRelations.isToptStream(leftType) &&
          StreamSymTypeRelations.isToptStream(rightType)
      ) {
        result = StreamSymTypeFactory.createToptStream(fusedElementType);
      }
      else if (StreamSymTypeRelations.isUntimedStream(leftType) &&
          StreamSymTypeRelations.isUntimedStream(rightType)
      ) {
        result = StreamSymTypeFactory.createUntimedStream(fusedElementType);
      }
      else {
        // alternatively, one could return Stream, but the question is,
        // whether the user wants this.
        // -> can be extended conservatively if required
        Log.error("0xFD571 got two incompatible stream types" +
                " for concatenation: " + leftType.printFullName()
                + " and " + rightType.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
        result = SymTypeExpressionFactory.createObscureType();
      }
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTLengthStreamExpression expr) {
    SymTypeExpression result;
    SymTypeExpression streamType =
        normalize(getType4Ast().getPartialTypeOfExpr(expr.getExpression()));
    if (streamType.isObscureType()) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else if (!assertIsStream(expr.getExpression(), streamType)) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else {
      result = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG);
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // Helper

  protected boolean assertIsStream(ASTExpression expr, SymTypeExpression type) {
    if (!StreamSymTypeRelations.isStream(type)) {
      Log.error("0xFD572 expected a Stream, but got "
              + type.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return false;
    }
    return true;
  }

}
