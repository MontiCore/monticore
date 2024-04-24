// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.SymTypeRelations;

import java.util.Arrays;
import java.util.Optional;

/**
 * Implementation of common operators for type visitors,
 * e.g., multiply, add, etc.
 * Implemented here, as some logic is reusable across multiple TypeVisitors,
 * e.g. CommonExpressions, OptionalOperators, AssignmentExpressions, ...
 * Additionally, common logic is shared between the Operators.
 *
 * Return values are empty if the operation is not applicable to the types,
 * no error message will be logged.
 * Return values are not SymTypeOfObscure to make sure
 * that this case is not ignored.
 */
public class TypeVisitorOperatorCalculator {

  // arithmetic: +, -, *, /, %

  public Optional<SymTypeExpression> plus(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculatePlus)
            .apply(left, right);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculatePlus(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result;
    // if one part of the expression is a String
    // then the whole expression is a String
    if (SymTypeRelations.isString(left)) {
      result = SymTypeExpressionFactory.createTypeObject(left.getTypeInfo());
    }
    else if (SymTypeRelations.isString(right)) {
      result = SymTypeExpressionFactory.createTypeObject(right.getTypeInfo());
    }
    // no String in the expression
    // -> use the normal calculation for the basic arithmetic operators
    else {
      result = calculatePlusMinusModulo(left, right);
    }
    return result;
  }

  public Optional<SymTypeExpression> minus(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculatePlusMinusModulo)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> modulo(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculatePlusMinusModulo)
            .apply(left, right);
    return obscure2Empty(result);
  }

  /**
   * calculates +,-,%
   * without support for String
   */
  protected SymTypeExpression calculatePlusMinusModulo(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result;
    Optional<SymTypeOfSIUnit> resultSI = Optional.empty();
    Optional<SymTypeExpression> resultV = Optional.empty();

    Optional<SymTypeOfSIUnit> leftSI = getSIUnit(left);
    Optional<SymTypeOfSIUnit> rightSI = getSIUnit(right);
    Optional<SymTypeExpression> leftV = getValueType(left);
    Optional<SymTypeExpression> rightV = getValueType(right);

    // check if SIUnits are compatible
    if (leftSI.isPresent() && rightSI.isPresent()) {
      if (SymTypeRelations.isCompatible(leftSI.get(), rightSI.get())
          || SymTypeRelations.isCompatible(rightSI.get(), leftSI.get())) {
        resultSI = Optional.of(leftSI.get().deepClone());
      }
    }

    // check if value types fit the operators
    if (leftV.isPresent() && rightV.isPresent()) {
      resultV = obscure2Empty(
          calculateArithmeticExpressionNumeric(leftV.get(), rightV.get())
      );
    }

    if (hasSIUnit(left, right) && resultSI.isEmpty()) {
      result = SymTypeExpressionFactory.createObscureType();
    }
    else {
      result = fuseSIUnitWithValueType(resultSI, resultV);
    }

    return result;
  }

  public Optional<SymTypeExpression> multiply(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateMultiply)
            .apply(left, right);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateMultiply(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result;
    Optional<SymTypeOfSIUnit> resultSI;
    Optional<SymTypeExpression> resultV;

    Optional<SymTypeOfSIUnit> leftSI = getSIUnit(left);
    Optional<SymTypeOfSIUnit> rightSI = getSIUnit(right);
    Optional<SymTypeExpression> leftV = getValueType(left);
    Optional<SymTypeExpression> rightV = getValueType(right);

    // multiply SIUnits
    if (leftSI.isPresent() && rightSI.isPresent()) {
      resultSI = Optional.of(
          SIUnitTypeRelations.multiply(leftSI.get(), rightSI.get())
      );
    }
    else if (leftSI.isPresent()) {
      resultSI = leftSI;
    }
    else if (rightSI.isPresent()) {
      resultSI = rightSI;
    }
    else {
      resultSI = Optional.empty();
    }

    // check if value types fit the operators
    if (leftV.isPresent() && rightV.isPresent()) {
      resultV = obscure2Empty(
          calculateArithmeticExpressionNumeric(leftV.get(), rightV.get())
      );
    }
    else {
      resultV = Optional.empty();
    }

    result = fuseSIUnitWithValueType(resultSI, resultV);

    return result;
  }

  public Optional<SymTypeExpression> divide(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateDivide)
            .apply(left, right);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateDivide(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result;
    Optional<SymTypeOfSIUnit> resultSI;
    Optional<SymTypeExpression> resultV;

    Optional<SymTypeOfSIUnit> leftSI = getSIUnit(left);
    Optional<SymTypeOfSIUnit> rightSI = getSIUnit(right);
    Optional<SymTypeExpression> leftV = getValueType(left);
    Optional<SymTypeExpression> rightV = getValueType(right);

    // divide SIUnits
    if (leftSI.isPresent() && rightSI.isPresent()) {
      resultSI = Optional.of(
          SIUnitTypeRelations.multiply(leftSI.get(),
              SIUnitTypeRelations.invert(rightSI.get()))
      );
    }
    else if (leftSI.isPresent()) {
      resultSI = leftSI;
    }
    else if (rightSI.isPresent()) {
      resultSI = Optional.of(SIUnitTypeRelations.invert(rightSI.get()));
    }
    else {
      resultSI = Optional.empty();
    }

    // check if value types fit the operators
    if (leftV.isPresent() && rightV.isPresent()) {
      resultV = obscure2Empty(
          calculateArithmeticExpressionNumeric(leftV.get(), rightV.get())
      );
    }
    else {
      resultV = Optional.empty();
    }

    result = fuseSIUnitWithValueType(resultSI, resultV);

    return result;
  }

  /**
   * calculates +,-,*,/,%
   * without support for String and SIUnits
   */
  protected SymTypeExpression calculateArithmeticExpressionNumeric(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    if (SymTypeRelations.isNumericType(left) &&
        SymTypeRelations.isNumericType(right)) {
      return SymTypeRelations.numericPromotion(left, right);
    }
    else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  // numeric prefixes: +, -

  public Optional<SymTypeExpression> plusPrefix(SymTypeExpression inner) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculatePlusMinusPrefix)
            .apply(inner);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> minusPrefix(SymTypeExpression inner) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculatePlusMinusPrefix)
            .apply(inner);
    return obscure2Empty(result);
  }

  /**
   * calculates unary +,-
   */
  protected SymTypeExpression calculatePlusMinusPrefix(SymTypeExpression inner) {
    SymTypeExpression result;
    Optional<SymTypeExpression> resultV;

    if (hasValueType(inner)) {
      if (!SymTypeRelations.isNumericType(getValueType(inner).get())) {
        resultV = Optional.of(SymTypeExpressionFactory.createObscureType());
      }
      else {
        // in Java, an evaluation of the actual value
        // would take place (if possible)
        resultV = getValueType(inner).map(SymTypeRelations::numericPromotion);
      }
    }
    else {
      resultV = Optional.empty();
    }

    result = fuseSIUnitWithValueType(getSIUnit(inner), resultV);
    return result;
  }

  // equality: ==, !=

  public Optional<SymTypeExpression> equality(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateEqualityInequality)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> inequality(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateEqualityInequality)
            .apply(left, right);
    return obscure2Empty(result);
  }

  /**
   * calculates ==, !=
   */
  protected SymTypeExpression calculateEqualityInequality(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    boolean compatibleForComparison = true;

    Optional<SymTypeOfSIUnit> leftSI = getSIUnit(left);
    Optional<SymTypeOfSIUnit> rightSI = getSIUnit(right);
    Optional<SymTypeExpression> leftV = getValueType(left);
    Optional<SymTypeExpression> rightV = getValueType(right);

    // check SIUnits
    if (leftSI.isPresent() && rightSI.isPresent()) {
      if (!SymTypeRelations.isCompatible(leftSI.get(), rightSI.get())
          && !SymTypeRelations.isCompatible(rightSI.get(), leftSI.get())) {
        compatibleForComparison = false;
      }
    }
    else if (leftSI.isPresent() || rightSI.isPresent()) {
      // SIUnits of Dimension 1 do not exist here, wherefore:
      compatibleForComparison = false;
    }

    // check value types
    boolean needToCheckValueTypes = true;
    if (leftV.isPresent() && rightV.isPresent()) {
      // skip unboxing + numeric promotion if applicable
      if (leftV.get().isPrimitive() && rightV.get().isPrimitive()) {
        if (SymTypeRelations.isNumericType(leftV.get()) &&
            SymTypeRelations.isNumericType(rightV.get())
        ) {
          // deliberate no-op
          needToCheckValueTypes = false;
        }
        else {
          leftV = leftV.map(SymTypeRelations::unbox);
          rightV = rightV.map(SymTypeRelations::unbox);
        }
      }
    }
    if (needToCheckValueTypes) {
      compatibleForComparison =
          SymTypeRelations.isCompatible(leftV.get(), rightV.get()) ||
              SymTypeRelations.isCompatible(rightV.get(), leftV.get());
    }

    if (compatibleForComparison) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  // numeric comparison: <, <=, >, >=

  public Optional<SymTypeExpression> lessThan(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateNumericComparison)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> lessEqual(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateNumericComparison)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> greaterThan(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateNumericComparison)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> greaterEqual(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateNumericComparison)
            .apply(left, right);
    return obscure2Empty(result);
  }

  /**
   * calculates <, <=, >, =>
   */
  protected SymTypeExpression calculateNumericComparison(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    boolean compatibleForComparison = true;

    Optional<SymTypeOfSIUnit> leftSI = getSIUnit(left);
    Optional<SymTypeOfSIUnit> rightSI = getSIUnit(right);
    Optional<SymTypeExpression> leftV = getValueType(left);
    Optional<SymTypeExpression> rightV = getValueType(right);

    // check SIUnits
    if (leftSI.isPresent() && rightSI.isPresent()) {
      if (!SymTypeRelations.isCompatible(leftSI.get(), rightSI.get())
          && !SymTypeRelations.isCompatible(rightSI.get(), leftSI.get())) {
        compatibleForComparison = false;
      }
    }
    else if (leftSI.isPresent() || rightSI.isPresent()) {
      // SIUnits of Dimension 1 do not exist here, wherefore:
      compatibleForComparison = false;
    }

    // check if value types fit the operators
    if (leftV.isPresent() && rightV.isPresent()) {
      if (!SymTypeRelations.isNumericType(leftV.get())
          || !SymTypeRelations.isNumericType(rightV.get()))
        compatibleForComparison = false;
    }

    if (compatibleForComparison) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  // boolean operators: &&, ||, !

  public Optional<SymTypeExpression> booleanAnd(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateConditionalBooleanOp)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> booleanOr(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateConditionalBooleanOp)
            .apply(left, right);
    return obscure2Empty(result);
  }

  /**
   * calculates &&, ||
   */
  protected SymTypeExpression calculateConditionalBooleanOp(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    if (SymTypeRelations.isBoolean(left) && SymTypeRelations.isBoolean(right)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  public Optional<SymTypeExpression> logicalNot(SymTypeExpression inner) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateLogicalNot)
            .apply(inner);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateLogicalNot(SymTypeExpression inner) {
    if (SymTypeRelations.isBoolean(inner)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  // bitwise / binary: &, |, ^, ~

  public Optional<SymTypeExpression> binaryAnd(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateBinaryInfixOp)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> binaryOr(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateBinaryInfixOp)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> binaryXor(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateBinaryInfixOp)
            .apply(left, right);
    return obscure2Empty(result);
  }

  /**
   * calculates &, |, ^
   */
  protected SymTypeExpression calculateBinaryInfixOp(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result;
    //only defined on boolean - boolean and integral type - integral type
    if (SymTypeRelations.isBoolean(left) && SymTypeRelations.isBoolean(right)) {
      result = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else if (SymTypeRelations.isIntegralType(left)
        && SymTypeRelations.isIntegralType(right)) {
      result = SymTypeRelations.numericPromotion(left, right);
    }
    else {
      result = SymTypeExpressionFactory.createObscureType();
    }
    return result;
  }

  public Optional<SymTypeExpression> bitwiseComplement(SymTypeExpression inner) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateBitwiseComplement)
            .apply(inner);
    return obscure2Empty(result);
  }

  /**
   * calculates ~
   */
  protected SymTypeExpression calculateBitwiseComplement(SymTypeExpression inner) {
    // JLS 20, 15.15.5
    if (SymTypeRelations.isIntegralType(inner)) {
      return SymTypeRelations.numericPromotion(inner);
    }
    else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  // shifts: <<, >>, >>>

  public Optional<SymTypeExpression> leftShift(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateShift)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> signedRightShift(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateShift)
            .apply(left, right);
    return obscure2Empty(result);
  }

  public Optional<SymTypeExpression> unsignedRightShift(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateShift)
            .apply(left, right);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateShift(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    // JLS 20, 15.19
    if (SymTypeRelations.isIntegralType(left) &&
        SymTypeRelations.isIntegralType(right)
    ) {
      return SymTypeRelations.numericPromotion(left);
    }
    else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  // assignment: =

  public Optional<SymTypeExpression> assignment(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateAssignment)
            .apply(left, right);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateAssignment(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result;
    if (SymTypeRelations.isCompatible(left, right)) {
      result = left.deepClone();
    }
    else {
      result = SymTypeExpressionFactory.createObscureType();
    }
    return result;
  }

  // cast: (.).
  // not an operator, but casting is used for some operators' calculation

  public Optional<SymTypeExpression> cast(
      SymTypeExpression target,
      SymTypeExpression source) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateCast)
            .apply(target, source);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateCast(
      SymTypeExpression target,
      SymTypeExpression source
  ) {
    SymTypeExpression result;
    // allow to cast numbers down, e.g., (int) 5.0 or (byte) 5
    if (SymTypeRelations.isNumericType(target) && SymTypeRelations.isNumericType(source)) {
      result = target;
    }
    // check typecast is possible
    else if (
        SymTypeRelations.isSubTypeOf(target, source) || // downcast
            SymTypeRelations.isSubTypeOf(source, target) // upcast
    ) {
      result = target;
    }
    else {
      result = SymTypeExpressionFactory.createObscureType();
    }
    return result;
  }

  // Helper

  protected boolean hasSIUnit(SymTypeExpression... types) {
    return Arrays.stream(types)
        .anyMatch(t -> t.isSIUnitType() || t.isNumericWithSIUnitType());
  }

  protected Optional<SymTypeOfSIUnit> getSIUnit(SymTypeExpression type) {
    if (type.isSIUnitType()) {
      return Optional.of(type.asSIUnitType());
    }
    else if (type.isNumericWithSIUnitType()) {
      return Optional.of(type.asNumericWithSIUnitType().getSIUnitType());
    }
    else {
      return Optional.empty();
    }
  }

  protected boolean hasValueType(SymTypeExpression... types) {
    return Arrays.stream(types).anyMatch(t -> !t.isSIUnitType());
  }

  protected Optional<SymTypeExpression> getValueType(SymTypeExpression type) {
    if (type.isSIUnitType()) {
      return Optional.empty();
    }
    else if (type.isNumericWithSIUnitType()) {
      return Optional.of(type.asNumericWithSIUnitType().getNumericType());
    }
    else {
      return Optional.of(type);
    }
  }

  protected Optional<SymTypeExpression> obscure2Empty(SymTypeExpression type) {
    if (type.isObscureType()) {
      return Optional.empty();
    }
    else {
      return Optional.of(type);
    }
  }

  protected SymTypeExpression fuseSIUnitWithValueType(
      Optional<SymTypeOfSIUnit> siUnitType,
      Optional<SymTypeExpression> valueType
  ) {
    if (valueType.isPresent() && valueType.get().isObscureType()) {
      return SymTypeExpressionFactory.createObscureType();
    }
    else if (siUnitType.isPresent() && valueType.isPresent()) {
      return SymTypeExpressionFactory
          .createNumericWithSIUnit(siUnitType.get(), valueType.get());
    }
    else if (siUnitType.isPresent() && valueType.isEmpty()) {
      return siUnitType.get();
    }
    else if (siUnitType.isEmpty() && valueType.isPresent()) {
      return valueType.get();
    }
    else {
      return SymTypeExpressionFactory.createObscureType();
    }
  }

}
