/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.streamexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendAbsentStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendTickStreamExpression;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.SymTypeRelations.normalize;

/* Where is the StreamExpressionsCTTIVisitor?

   Where is none (anymore), and for good reason;
   There are multiple Stream types (Event, Topt, etc.),
   optionally, there is a "Stream" supertype.
   While most expressions work well with type inference
   (constructor, appending), concatenation does not.
   Imagine a^^b for two expressions a and b.
   What is the function representing ^^?

   Option A: It is an overloaded function
   <T> (EventStream<T>, EventStream<T>) -> EventStream<T>,
   <T> (SyncStream<T>, SyncStream<T>) -> SyncStream<T>, etc.
   choosing the correct method depends on the argument types,
   but the argument types, in this case, are not pertinent to applicability.
   As such, the overload resolution cannot occur.
   (Any attempt at this would presumably be exponentially complex in runtime)

   Option B: <T, S extends Stream<T>> (S, S) -> S
   This function is not overloaded, but requires the common "Stream" SuperType.
   Thus, streams are concatenated that should not be,
   e.g., Event<1>^^Sync<2>,
   then this case will not be an error, as simply the type
   (Stream<int>, Stream<int>) -> Stream<int> will be calculated.
   As Stream is a valid SuperType in Option B,
   one cannot simply log an error on calculating this type.
   As the sub-expressions of the concatenation can be arbitrarily complex,
   creation of a corresponding CoCo is not feasible.
*/

public class StreamExpressionsTypeVisitor extends AbstractTypeVisitor
    implements StreamExpressionsVisitor2 {

  @Override
  public void endVisit(ASTStreamConstructorExpression expr) {
    SymTypeExpression result;
    List<SymTypeExpression> containedExprTypes = expr.getExpressionList().stream()
        .map(e -> getType4Ast().getPartialTypeOfExpr(e))
        .collect(Collectors.toList());
    Optional<SymTypeExpression> givenElementType =
        expr.isPresentMCTypeArgument() ?
            Optional.of(getType4Ast().getPartialTypeOfTypeId(expr.getMCTypeArgument())) :
            Optional.empty();
    if (containedExprTypes.stream().anyMatch(SymTypeExpression::isObscureType) ||
        givenElementType.stream().anyMatch(SymTypeExpression::isObscureType)) {
      result = createObscureType();
    }
    else if (getType4Ast().hasTypeOfExpression(expr)) {
      // type already calculated
      return;
    }
    else {
      if (containedExprTypes.isEmpty() && givenElementType.isEmpty()) {
        Log.error("0xFD577 empty stream without"
                + " explicit element type argument is not supported."
                + " Add a type argument (e.g., '<>' -> '<int><>')",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
        result = createObscureType();
      }
      else {
        boolean hasBadElem = false;
        if (givenElementType.isPresent()) {
          for (SymTypeExpression containedExprType : containedExprTypes) {
            if (!StreamSymTypeRelations.isCompatible(givenElementType.get(), containedExprType)) {
              Log.error("0xFD578 " +
                      "stream with explicit element type "
                      + givenElementType.get().printFullName()
                      + " contains incompatible expression of type "
                      + containedExprType.printFullName(),
                  expr.get_SourcePositionStart(),
                  expr.get_SourcePositionEnd()
              );
              hasBadElem = true;
            }
          }
        }
        if (hasBadElem) {
          result = createObscureType();
        }
        else {
          SymTypeExpression elementType = givenElementType
              .orElseGet(() -> createUnion(Set.copyOf(containedExprTypes)));
          if (expr.isEventTimed()) {
            result = StreamSymTypeFactory.createEventStream(elementType);
          }
          else if (expr.isSyncTimed()) {
            result = StreamSymTypeFactory.createSyncStream(elementType);
          }
          else if (expr.isToptTimed()) {
            result = StreamSymTypeFactory.createToptStream(elementType);
          }
          else {
            result = StreamSymTypeFactory.createUntimedStream(elementType);
          }
        }
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
  public void endVisit(ASTAppendAbsentStreamExpression expr) {
    if (type4Ast.hasTypeOfExpression(expr)) {
      // already calculated
      return;
    }
    SymTypeExpression result;
    SymTypeExpression streamType =
        normalize(getType4Ast().getPartialTypeOfExpr(expr.getStream()));
    if (streamType.isObscureType()) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else if (!assertIsStream(expr.getStream(), streamType)) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else if (StreamSymTypeRelations.isToptStream(streamType)) {
      Log.error("0xFD573 expected a ToptStream, but got "
              + streamType.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      result = createObscureType();
    }
    else {
      result = streamType.deepClone();
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTAppendTickStreamExpression expr) {
    if (type4Ast.hasTypeOfExpression(expr)) {
      // already calculated
      return;
    }
    SymTypeExpression result;
    SymTypeExpression streamType =
        normalize(getType4Ast().getPartialTypeOfExpr(expr.getStream()));
    if (streamType.isObscureType()) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else if (!assertIsStream(expr.getStream(), streamType)) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else if (StreamSymTypeRelations.isEventStream(streamType)) {
      Log.error("0xFD574 expected an EventStream, but got "
              + streamType.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      result = createObscureType();
    }
    else {
      result = streamType.deepClone();
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTConcatStreamExpression expr) {
    if (type4Ast.hasTypeOfExpression(expr)) {
      // todo FDr: check that inference did not calculate Stream, but a specific subtype. otherwise error
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
      if (StreamSymTypeRelations.isEventStream(leftType) &&
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
        Log.error("0xFD571 got two (potentially) "
                + "incompatible stream types for concatenation: "
                + leftType.printFullName() + " and "
                + rightType.printFullName(),
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
