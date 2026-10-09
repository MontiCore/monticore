/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.operationprinter;

import de.monticore.codegen.CodeGenOperationPrinter;
import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenOperationHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.getSIUnitValueType;
import static de.monticore.types3.SymTypeRelations.isBoolean;
import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.SymTypeRelations.numericPromotion;
import static de.monticore.types3.util.SIUnitTypeRelations.hasSIUnit;

/**
 * Operations on SIUnits, e.g., [m]<int> + [m]<int>,
 * by delegating to the handlers of the numeric types.
 * The operands are expected to be converted to their normalized units,
 * s.a. {@link de.monticore.codegen.javagen.JavaGenVisitorState#printOperand}.
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
    if (!(hasSIUnit(leftType) || hasSIUnit(rightType))) {
      return false;
    }
    SymTypeExpression leftValueType = getSIUnitValueType(leftType);
    SymTypeExpression rightValueType = getSIUnitValueType(rightType);
    SymTypeExpression resultValueType = getSIUnitValueType(resultType);
    if (!isNumericType(leftValueType) || !isNumericType(rightValueType)) {
      return false;
    }

    switch (operator) {
      case PLUS:
      case MINUS:
      case MULTIPLY:
      case DIVIDE:
      case MODULO:
        if (!isNumericType(resultValueType)) {
          return false;
        }
        SymTypeExpression calculationType =
            numericPromotion(leftValueType, rightValueType);
        printConverted(printer, resultValueType, calculationType, p ->
            CodeGenOperationPrinter.print(operator, p, calculationType,
                leftValueType, rightValueType,
                leftExprPrintAction, rightExprPrintAction
            )
        );
        return true;
      case EQUALS:
      case NOT_EQUALS:
      case GREATER_THAN:
      case LESS_THAN:
      case GREATER_EQUALS:
      case LESS_EQUALS:
        if (!isBoolean(resultValueType)) {
          return false;
        }
        CodeGenOperationPrinter.print(operator, printer, resultValueType,
            leftValueType, rightValueType,
            leftExprPrintAction, rightExprPrintAction
        );
        return true;
      default:
        return false;
    }
  }

}
