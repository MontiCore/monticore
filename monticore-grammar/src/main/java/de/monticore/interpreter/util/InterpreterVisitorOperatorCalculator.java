// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationDouble;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueObject;

import java.util.function.BiPredicate;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;

import static de.monticore.types3.SymTypeRelations.isBoolean;
import static de.monticore.types3.SymTypeRelations.isIntegralType;
import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.SymTypeRelations.isStringOrSubType;

/**
 * Reusable implementation for the visitor for common operators, e.g., '+'.
 * The implementations are abstracted from the ast.
 */
public class InterpreterVisitorOperatorCalculator {

  public MICalculation handlePlus(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    MICalculation res;
    if (isStringOrSubType(leftType) || isStringOrSubType(rightType)) {
      res = getPlusStringCalc(
          leftCalc, rightCalc,
          leftType, rightType
      );
    }
    else {
      res = getArithmeticBinaryCalc(
          leftCalc, rightCalc,
          Integer::sum, Double::sum,
          leftType, rightType, exprType,
          "+"
      );
    }
    return res;
  }

  public MICalculation handleMinus(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getArithmeticBinaryCalc(
        leftCalc, rightCalc,
        (l, r) -> l - r,
        (l, r) -> l - r,
        leftType, rightType, exprType,
        "-"
    );
  }

  public MICalculation handleMultiply(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getArithmeticBinaryCalc(
        leftCalc, rightCalc,
        (l, r) -> l * r,
        (l, r) -> l * r,
        leftType, rightType, exprType,
        "*"
    );
  }

  public MICalculation handleDivide(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getArithmeticBinaryCalc(
        leftCalc, rightCalc,
        (l, r) -> l / r,
        (l, r) -> l / r,
        leftType, rightType, exprType,
        "/"
    );
  }

  public MICalculation handleModulo(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getArithmeticBinaryCalc(
        leftCalc, rightCalc,
        (l, r) -> l % r,
        (l, r) -> l % r,
        leftType, rightType, exprType,
        "%"
    );
  }

  public MICalculation handlePlusPrefix(
      MICalculation innerCalc,
      SymTypeExpression innerType,
      SymTypeExpression exprType
  ) {
    // can only be done as our smallest number type is int,
    // otherwise, numeric promotion becomes necessary.
    return innerCalc;
  }

  public MICalculation handleMinusPrefix(
      MICalculation innerCalc,
      SymTypeExpression innerType,
      SymTypeExpression exprType
  ) {
    MICalculation res;
    if (isIntegralType(exprType) && isIntegralType(innerType)) {
      MICalculationInt innerIntCalc = innerCalc.asCalculationInt();
      res = (MICalculationInt) frame ->
          -innerIntCalc.calculate(frame);
    }
    else if (isNumericType(exprType) && isNumericType(innerType)) {
      MICalculationDouble innerDoubleCalc = innerCalc.asCalculationDouble();
      res = (MICalculationDouble) frame ->
          -innerDoubleCalc.calculate(frame);
    }
    else {
      throwUnsupported("-", exprType,
          "inner type: " + innerType.printFullName()
      );
      res = null;
    }
    return res;
  }

  public MICalculationBoolean handleEquals(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getEqualityBinaryCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        "==",
        (l, r) -> l == r,
        (l, r) -> l == r,
        (l, r) -> l == r,
        MCValue::checkEqualityOperator
    );
  }

  public MICalculation handleNotEquals(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getEqualityBinaryCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        "!=",
        (l, r) -> l != r,
        (l, r) -> l != r,
        (l, r) -> l != r,
        (l, r) -> l != r
    );
  }

  public MICalculation handleLessThan(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getNumericComparisonCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        "<",
        (l, r) -> l < r,
        (l, r) -> l < r
    );
  }

  public MICalculation handleLessEqual(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getNumericComparisonCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        "<=",
        (l, r) -> l <= r,
        (l, r) -> l <= r
    );
  }

  public MICalculation handleGreaterThan(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getNumericComparisonCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        ">",
        (l, r) -> l > r,
        (l, r) -> l > r
    );
  }

  public MICalculation handleGreaterEqual(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getNumericComparisonCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        ">=",
        (l, r) -> l >= r,
        (l, r) -> l >= r
    );
  }

  public MICalculation handleBooleanAnd(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    MICalculationBoolean leftBoolCalc = leftCalc.asCalculationBoolean();
    MICalculationBoolean rightBoolCalc = rightCalc.asCalculationBoolean();
    return (MICalculationBoolean) frame ->
        leftBoolCalc.calculate(frame) && rightBoolCalc.calculate(frame);
  }

  public MICalculation handleBooleanOr(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    MICalculationBoolean leftBoolCalc = leftCalc.asCalculationBoolean();
    MICalculationBoolean rightBoolCalc = rightCalc.asCalculationBoolean();
    return (MICalculationBoolean) frame ->
        leftBoolCalc.calculate(frame) || rightBoolCalc.calculate(frame);
  }

  public MICalculation handleBooleanNot(
      MICalculation innerCalc,
      SymTypeExpression innerType,
      SymTypeExpression exprType
  ) {
    MICalculationInt innerIntCalc = innerCalc.asCalculationInt();
    return (MICalculationInt) frame -> ~innerIntCalc.calculate(frame);
  }

  public MICalculation handleLogicalNot(
      MICalculation innerCalc,
      SymTypeExpression innerType,
      SymTypeExpression exprType
  ) {
    MICalculationBoolean innerBooleanCalc = innerCalc.asCalculationBoolean();
    return (MICalculationBoolean) frame -> !innerBooleanCalc.calculate(frame);
  }

  public MICalculation handleLeftShift(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getBitBinaryCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        "<<",
        (l, r) -> l << r
    );
  }

  public MICalculation handleRightShift(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getBitBinaryCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        ">>",
        (l, r) -> l >> r
    );
  }

  public MICalculation handleLogicalRightShift(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getBitBinaryCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        ">>>",
        (l, r) -> l >>> r
    );
  }

  public MICalculation handleBinaryAnd(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getBitBinaryCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        "&",
        (l, r) -> l & r
    );
  }

  public MICalculation handleBinaryXor(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getBitBinaryCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        "^",
        (l, r) -> l ^ r
    );
  }

  public MICalculation handleBinaryOr(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType
  ) {
    return getBitBinaryCalc(
        leftCalc,
        rightCalc,
        leftType,
        rightType,
        exprType,
        "|",
        (l, r) -> l | r
    );
  }

  // note: this is slightly non-optimal performance wise,
  // but inlining the BinaryOperators would be a LOT of code,
  // even if done properly,
  // thus only inline if REALLY required
  protected MICalculation getArithmeticBinaryCalc(
      MICalculation leftCalc,
      MICalculation rightCalc,
      IntBinaryOperator opInt,
      DoubleBinaryOperator opDouble,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType,
      String opStr
  ) {
    MICalculation res;
    if (isIntegralType(leftType) && isIntegralType(rightType)) {
      MICalculationInt leftIntCalc = leftCalc.asCalculationInt();
      MICalculationInt rightIntCalc = rightCalc.asCalculationInt();
      res = (MICalculationInt) frame -> opInt.applyAsInt(
          leftIntCalc.calculate(frame),
          rightIntCalc.calculate(frame)
      );
    }
    else if (isNumericType(leftType) && isNumericType(rightType)) {
      MICalculationDouble leftDoubleCalc = leftCalc.asCalculationDouble();
      MICalculationDouble rightDoubleCalc = rightCalc.asCalculationDouble();
      res = (MICalculationDouble) frame -> opDouble.applyAsDouble(
          leftDoubleCalc.calculate(frame),
          rightDoubleCalc.calculate(frame)
      );
    }
    else {
      throwUnsupported(opStr, exprType,
          "left type: " + leftType.printFullName()
              + " right type: " + rightType.printFullName()
      );
      res = null;
    }
    return res;
  }

  protected MICalculationValue getPlusStringCalc(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType
  ) {
    MICalculationValue leftCalcValue = leftCalc.asCalculationValue();
    MICalculationValue rightCalcValue = rightCalc.asCalculationValue();
    return frame -> new MCValueObject(
        leftCalcValue.calculate(frame).asString() +
            rightCalcValue.calculate(frame).asString()
    );
  }

  protected MICalculationBoolean getNumericComparisonCalc(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType,
      String opStr,
      IntBiPredicate opInt,
      DoubleBiPredicate opDouble
  ) {
    MICalculationBoolean res;
    if (isIntegralType(leftType) && isIntegralType(rightType)) {
      MICalculationInt leftIntCalc = leftCalc.asCalculationInt();
      MICalculationInt rightIntCalc = rightCalc.asCalculationInt();
      res = frame -> opInt.test(
          leftIntCalc.calculate(frame),
          rightIntCalc.calculate(frame)
      );
    }
    else if (isNumericType(leftType) && isNumericType(rightType)) {
      MICalculationDouble leftDoubleCalc = leftCalc.asCalculationDouble();
      MICalculationDouble rightDoubleCalc = rightCalc.asCalculationDouble();
      res = frame -> opDouble.test(
          leftDoubleCalc.calculate(frame),
          rightDoubleCalc.calculate(frame)
      );
    }
    else {
      throwUnsupported(opStr, exprType,
          "left type: " + leftType.printFullName()
              + " right type: " + rightType.printFullName()
      );
      res = null;
    }
    return res;
  }

  protected MICalculationBoolean getEqualityBinaryCalc(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType,
      String opStr,
      BooleanBiPredicate opBoolean,
      IntBiPredicate opInt,
      DoubleBiPredicate opDouble,
      BiPredicate<MCValue, MCValue> opObject
  ) {
    MICalculationBoolean res;
    if (isBoolean(leftType) && isBoolean(rightType)) {
      MICalculationBoolean leftBooleanCalc = leftCalc.asCalculationBoolean();
      MICalculationBoolean rightBooleanCalc = rightCalc.asCalculationBoolean();
      res = frame -> opBoolean.test(
          leftBooleanCalc.calculate(frame),
          rightBooleanCalc.calculate(frame)
      );
    }
    else if (isIntegralType(leftType) && isIntegralType(rightType)) {
      MICalculationInt leftIntCalc = leftCalc.asCalculationInt();
      MICalculationInt rightIntCalc = rightCalc.asCalculationInt();
      res = frame -> opInt.test(
          leftIntCalc.calculate(frame),
          rightIntCalc.calculate(frame)
      );
    }
    else if (isNumericType(leftType) && isNumericType(rightType)) {
      MICalculationDouble leftDoubleCalc = leftCalc.asCalculationDouble();
      MICalculationDouble rightDoubleCalc = rightCalc.asCalculationDouble();
      res = frame -> opDouble.test(
          leftDoubleCalc.calculate(frame),
          rightDoubleCalc.calculate(frame)
      );
    }
    else {
      MICalculationValue leftValueCalc = leftCalc.asCalculationValue();
      MICalculationValue rightValueCalc = rightCalc.asCalculationValue();
      res = frame -> opObject.test(
          leftValueCalc.calculate(frame),
          rightValueCalc.calculate(frame)
      );
    }
    return res;
  }

  protected MICalculationInt getBitBinaryCalc(
      MICalculation leftCalc,
      MICalculation rightCalc,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      SymTypeExpression exprType,
      String opStr,
      IntBinaryOperator op
  ) {
    MICalculationInt leftIntCalc = leftCalc.asCalculationInt();
    MICalculationInt rightIntCalc = rightCalc.asCalculationInt();
    return frame -> op.applyAsInt(
        leftIntCalc.calculate(frame),
        rightIntCalc.calculate(frame)
    );
  }

  protected void throwUnsupported(
      String operator,
      SymTypeExpression exprType,
      String furtherInfo
  ) throws UnsupportedOperationException {
    throw new UnsupportedOperationException(
        "Unsupported type for operator " + operator
            + " with type " + exprType.printFullName()
            + " : " + furtherInfo
    );
  }

  // internal interfaces

  @FunctionalInterface
  protected interface BooleanBiPredicate {
    boolean test(boolean left, boolean right);
  }

  @FunctionalInterface
  protected interface IntBiPredicate {
    boolean test(int left, int right);
  }

  @FunctionalInterface
  protected interface DoubleBiPredicate {
    boolean test(double left, double right);
  }

}
