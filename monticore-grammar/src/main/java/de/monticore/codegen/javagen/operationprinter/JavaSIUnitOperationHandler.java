/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.operationprinter;

import de.monticore.codegen.CodeGenOperationPrinter;
import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenOperationHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.util.SIUnitTypeRelations;

import java.math.BigDecimal;
import java.util.Optional;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.getSIUnitValueType;
import static de.monticore.types3.SymTypeRelations.isBoolean;
import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.SymTypeRelations.numericPromotion;
import static de.monticore.types3.util.SIUnitTypeRelations.getSIUnit;
import static de.monticore.types3.util.SIUnitTypeRelations.hasSIUnit;

/**
 * Operations on SIUnits, e.g., {@code [km]<double> + [m]<double>},
 * by delegating to the handlers of the numeric types.
 * For +, -, %, and comparisons, the operands are converted to a common unit,
 * e.g., {@code 5km + 3m -> 5 + 3 / 1000.0}.
 * For * and /, the values are calculated in the units of the operands,
 * and the result is converted afterward,
 * e.g., {@code 2km * 3m -> 2 * 3 * 1000.0} (in m^2).
 */
public class JavaSIUnitOperationHandler implements ICodeGenOperationHandler {

  @Override
  public boolean tryPrint(
      BinaryOperator operator,
      IndentPrinter printer,
      SymTypeExpression resultType,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    if (!(hasSIUnit(leftType) || hasSIUnit(rightType))
        || !isNumericType(getSIUnitValueType(leftType))
        || !isNumericType(getSIUnitValueType(rightType))
    ) {
      return false;
    }
    switch (operator) {
      case PLUS:
      case MINUS:
      case MODULO:
        if (!isNumericType(getSIUnitValueType(resultType))) {
          return false;
        }
        printInCommonUnit(operator, printer, resultType,
            getSIUnit(resultType), leftType, rightType,
            leftExprPrintAction, rightExprPrintAction
        );
        return true;
      case EQUALS:
      case NOT_EQUALS:
      case GREATER_THAN:
      case LESS_THAN:
      case GREATER_EQUALS:
      case LESS_EQUALS:
        if (!isBoolean(resultType)) {
          return false;
        }
        printInCommonUnit(operator, printer, resultType,
            SIUnitTypeRelations.getLargerUnit(
                getSIUnit(leftType), getSIUnit(rightType)),
            leftType, rightType,
            leftExprPrintAction, rightExprPrintAction
        );
        return true;
      case MULTIPLY:
      case DIVIDE:
        if (!isNumericType(getSIUnitValueType(resultType))) {
          return false;
        }
        printInOperandUnits(operator, printer, resultType,
            leftType, rightType,
            leftExprPrintAction, rightExprPrintAction
        );
        return true;
      default:
        return false;
    }
  }

  /**
   * converts the operands to the unit, then calculates the result
   */
  protected void printInCommonUnit(
      BinaryOperator operator,
      IndentPrinter printer,
      SymTypeExpression resultType,
      SymTypeOfSIUnit unit,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    SymTypeExpression leftValueType = getValueTypeInUnit(leftType, unit);
    SymTypeExpression rightValueType = getValueTypeInUnit(rightType, unit);
    CodeGenPrintAction leftInUnit = p -> printConverted(p,
        createNumericWithSIUnit(unit, leftValueType),
        leftType, leftExprPrintAction
    );
    CodeGenPrintAction rightInUnit = p -> printConverted(p,
        createNumericWithSIUnit(unit, rightValueType),
        rightType, rightExprPrintAction
    );
    if (isBoolean(resultType)) {
      CodeGenOperationPrinter.print(operator, printer, resultType,
          leftValueType, rightValueType, leftInUnit, rightInUnit
      );
    }
    else {
      SymTypeExpression calculationType =
          numericPromotion(leftValueType, rightValueType);
      printConverted(printer, getSIUnitValueType(resultType), calculationType,
          p -> CodeGenOperationPrinter.print(operator, p, calculationType,
              leftValueType, rightValueType, leftInUnit, rightInUnit
          )
      );
    }
  }

  /**
   * calculates the result in the units of the operands,
   * then converts the result, e.g., {@code [km*m] -> [m^2]}
   */
  protected void printInOperandUnits(
      BinaryOperator operator,
      IndentPrinter printer,
      SymTypeExpression resultType,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    SymTypeExpression leftValueType = getSIUnitValueType(leftType);
    SymTypeExpression rightValueType = getSIUnitValueType(rightType);
    SymTypeExpression calculationType =
        numericPromotion(leftValueType, rightValueType);
    SymTypeOfSIUnit rightUnit = operator == BinaryOperator.DIVIDE ?
        SIUnitTypeRelations.invert(getSIUnit(rightType)) :
        getSIUnit(rightType);
    SymTypeOfSIUnit calculationUnit =
        SIUnitTypeRelations.multiply(getSIUnit(leftType), rightUnit);
    printConverted(printer, resultType,
        createNumericWithSIUnit(calculationUnit, calculationType),
        p -> CodeGenOperationPrinter.print(operator, p, calculationType,
            leftValueType, rightValueType,
            leftExprPrintAction, rightExprPrintAction
        )
    );
  }

  // Helper

  /**
   * the value type after converting to the unit;
   * double if the value has to be scaled
   */
  protected SymTypeExpression getValueTypeInUnit(
      SymTypeExpression type,
      SymTypeOfSIUnit unit
  ) {
    Optional<BigDecimal> factor =
        SIUnitTypeRelations.getConversionFactor(getSIUnit(type), unit);
    if (factor.isPresent() && factor.get().compareTo(BigDecimal.ONE) != 0) {
      return numericPromotion(getSIUnitValueType(type),
          SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE)
      );
    }
    return getSIUnitValueType(type);
  }

  protected SymTypeExpression createNumericWithSIUnit(
      SymTypeOfSIUnit unit,
      SymTypeExpression valueType
  ) {
    return SymTypeExpressionFactory.createNumericWithSIUnit(unit, valueType);
  }

}
