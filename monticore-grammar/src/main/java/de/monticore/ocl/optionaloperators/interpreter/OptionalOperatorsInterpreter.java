// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.optionaloperators.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.expressions.interpreter.util.InterpreterOperatorTraverser;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationDouble;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.interpreter.util.InterpreterVisitorOperatorCalculator;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalEqualsExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalExpressionPrefix;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalGreaterEqualExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalGreaterThanExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalLessEqualExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalLessThanExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalNotEqualsExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalNotSimilarExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalSimilarExpression;
import de.monticore.ocl.optionaloperators._visitor.OptionalOperatorsInheritanceHandler;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueFactory;

import java.util.Optional;

import static de.monticore.interpreter.util.NativeStorageSelector.switchByFormat;
import static de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations.isOptional;
import static de.monticore.types3.SymTypeRelations.isBoolean;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Interpreter Visitor for OptionalOperators
 */
public class OptionalOperatorsInterpreter
    extends OptionalOperatorsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  protected InterpreterVisitorOperatorCalculator opCalculator
      = new InterpreterVisitorOperatorCalculator();
  protected InterpreterOperatorTraverser opTraverser =
      new InterpreterOperatorTraverser();

  public OptionalOperatorsInterpreter(InterpreterDataForBasicSymbols iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTOptionalExpressionPrefix node) {
    SymTypeExpression exprType = normalize(typeOf(node));
    // must be an Optional
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    Preconditions.checkState(isOptional(leftType));
    node.getLeft().accept(getTraverser());
    MICalculationValue leftCalc =
        iData.popCalculation().asCalculationValue();
    node.getRight().accept(getTraverser());
    MICalculationValue rightCalc =
        iData.popCalculation().asCalculationValue();

    MICalculationValue valueCalc = frame -> {
      final Optional<?> opt =
          (Optional<?>) leftCalc.calculate(frame).asNativeObject();
      if (opt.isPresent()) {
        // We may need to know the specific type that is required.
        // alternative would be to insert them as MIValue only,
        // but it is somewhat odd to wrap them only for Collection types
        // This may need to be extended for SIUnits.
        return MCValueFactory.createMIValueOfNativeObject(opt.get());
      }
      else {
        return rightCalc.calculate(frame);
      }
    };
    MICalculation calc = switchByFormat(
        exprType,
        (MICalculationBoolean) frame -> valueCalc.calculate(frame).asBoolean(),
        (MICalculationInt) frame -> valueCalc.calculate(frame).asInt(),
        (MICalculationDouble) frame -> valueCalc.calculate(frame).asDouble(),
        valueCalc
    );
    iData.putCalculation(calc);
  }

  @Override
  public void traverse(ASTOptionalLessEqualExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        liftForLeftOptional(opCalculator::handleLessThan)
    );
  }

  @Override
  public void traverse(ASTOptionalGreaterEqualExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        liftForLeftOptional(opCalculator::handleGreaterEqual)
    );
  }

  @Override
  public void traverse(ASTOptionalLessThanExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        liftForLeftOptional(opCalculator::handleLessThan)
    );
  }

  @Override
  public void traverse(ASTOptionalGreaterThanExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        liftForLeftOptional(opCalculator::handleGreaterThan)
    );
  }

  @Override
  public void traverse(ASTOptionalEqualsExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        liftForLeftOptional(opCalculator::handleEquals)
    );
  }

  @Override
  public void traverse(ASTOptionalNotEqualsExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        liftForLeftOptional(opCalculator::handleNotEquals)
    );
  }

  @Override
  public void traverse(ASTOptionalSimilarExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        this::handleIsSimilarOpt
    );
  }

  protected MICalculationBoolean handleIsSimilarOpt(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    Preconditions.checkState(isBoolean(exprType));
    Preconditions.checkState(isOptional(leftType));
    MICalculationValue leftCalcValue = leftCalc.asCalculationValue();
    MICalculationValue rightCalcValue = rightCalc.asCalculationValue();
    MICalculationBoolean calc = frame -> {
      final Optional<?> leftOpt =
          (Optional<?>) leftCalcValue.calculate(frame).asNativeObject();
      final Object rightVal = rightCalcValue.calculate(frame).asNativeObject();
      // is equals enough?
      return leftOpt.map(l -> l.equals(rightVal)).orElse(false);
    };
    return calc;
  }

  @Override
  public void traverse(ASTOptionalNotSimilarExpression node) {
    opTraverser.traverseBinaryOperator(
        getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        this::handleIsNotSimilarOpt
    );
  }

  protected MICalculationBoolean handleIsNotSimilarOpt(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    Preconditions.checkState(isBoolean(exprType));
    Preconditions.checkState(isOptional(leftType));
    MICalculationValue leftCalcValue = leftCalc.asCalculationValue();
    MICalculationValue rightCalcValue = rightCalc.asCalculationValue();
    MICalculationBoolean calc = frame -> {
      final Optional<?> leftOpt =
          (Optional<?>) leftCalcValue.calculate(frame).asNativeObject();
      final Object rightVal = rightCalcValue.calculate(frame).asNativeObject();
      // is equals enough?
      return leftOpt.map(l -> !l.equals(rightVal)).orElse(false);
    };
    return calc;
  }

  /**
   * lifts for Optional on the left side, e.g., {@code a < b}
   * is lifted to
   * {@code a.isPresent() && a.get() < b}
   *
   * @param innerHandler the handler for the inner calculation.
   *                     This must return a {@link MICalculationBoolean}!
   * @return an OperationHandler that lifts for Optional on the left side
   */
  protected InterpreterOperatorTraverser.BinaryOperationHandler liftForLeftOptional(
      InterpreterOperatorTraverser.BinaryOperationHandler innerHandler
  ) {
    return (MICalculation leftCalc,
        MICalculation rightCalc,
        SymTypeExpression leftType,
        SymTypeExpression rightType,
        SymTypeExpression exprType) ->
    {
      if (!isOptional(leftType)) {
        throw new IllegalArgumentException(
            "Expected an Optional, but got " + leftType.printFullName()
        );
      }
      SymTypeExpression elemType = MCCollectionSymTypeRelations
          .getCollectionElementType(leftType);

      // temporary storage for data exchange between calculations.
      // this method can very likely be simplified _severely_, if need arises.
      final boolean[] booleanStorage = new boolean[1];
      final int[] intStorage = new int[1];
      final double[] doubleStorage = new double[1];
      final MCValue[] MCValueStorage = new MCValue[1];

      MICalculationValue leftOptCalc = leftCalc.asCalculationValue();
      // Calculation that returns if the Optional is present.
      // If it is, it also stores the value in the corresponding storage.
      MICalculationBoolean leftIsPresentAndStoreCalc =
          switchByFormat(elemType,
              frame -> {
                @SuppressWarnings("unchecked") final Optional<Boolean> optValue =
                    (Optional<Boolean>) leftOptCalc.calculate(frame).asNativeObject();
                optValue.ifPresent(b -> booleanStorage[0] = b);
                return optValue.isPresent();
              },
              frame -> {
                @SuppressWarnings("unchecked") final Optional<Integer> optValue =
                    (Optional<Integer>) leftOptCalc.calculate(frame).asNativeObject();
                optValue.ifPresent(i -> intStorage[0] = i);
                return optValue.isPresent();
              },
              frame -> {
                @SuppressWarnings("unchecked") final Optional<Double> optValue =
                    (Optional<Double>) leftOptCalc.calculate(frame).asNativeObject();
                optValue.ifPresent(d -> doubleStorage[0] = d);
                return optValue.isPresent();
              },
              frame -> {
                final Optional<?> optValue =
                    (Optional<?>) leftOptCalc.calculate(frame).asNativeObject();
                optValue.ifPresent(v ->
                    MCValueStorage[0] = MCValueFactory.createMIValueOfNativeObject(v)
                );
                return optValue.isPresent();
              }
          );
      MICalculation leftUnwrappedLoadCalc = switchByFormat(
          elemType,
          (MICalculationBoolean) frame -> booleanStorage[0],
          (MICalculationInt) frame -> intStorage[0],
          (MICalculationDouble) frame -> doubleStorage[0],
          (MICalculationValue) frame -> MCValueStorage[0]
      );

      // handles the actual operation if left is not empty
      MICalculationBoolean innerCalc = innerHandler.getCalc(
          leftUnwrappedLoadCalc, rightCalc,
          elemType, rightType, exprType
      ).asCalculationBoolean();

      MICalculationBoolean liftedCalc = frame -> {
        final boolean isPresent = leftIsPresentAndStoreCalc.calculate(frame);
        return isPresent && innerCalc.calculate(frame);
      };
      return liftedCalc;
    };
  }

}


