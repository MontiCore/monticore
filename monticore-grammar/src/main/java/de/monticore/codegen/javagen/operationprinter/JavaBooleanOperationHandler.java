/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.operationprinter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenOperationHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import java.util.Map;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.BITWISE_AND;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.BITWISE_OR;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.BITWISE_XOR;
import static de.monticore.types3.SymTypeRelations.isBoolean;

/**
 * Handles the non-short-circuit operators "&", "|", and "^" on booleans
 */
public class JavaBooleanOperationHandler implements ICodeGenOperationHandler {

  protected static final Map<BinaryOperator, String> BINARY_OPERATOR_2_JAVA;

  static {
    BINARY_OPERATOR_2_JAVA =
        Map.ofEntries(
            Map.entry(BITWISE_AND, "&"),
            Map.entry(BITWISE_OR, "|"),
            Map.entry(BITWISE_XOR, "^")
        );
  }

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
    if (!BINARY_OPERATOR_2_JAVA.containsKey(operator) ||
        !isBoolean(leftType) ||
        !isBoolean(rightType)
    ) {
      return false;
    }
    printer.print("(");
    printConverted(printer, resultType, leftType, leftExprPrintAction);
    printer.print(")");
    printer.print(" " + BINARY_OPERATOR_2_JAVA.get(operator) + " ");
    printer.print("(");
    printConverted(printer, resultType, rightType, rightExprPrintAction);
    printer.print(")");
    return true;
  }

}
