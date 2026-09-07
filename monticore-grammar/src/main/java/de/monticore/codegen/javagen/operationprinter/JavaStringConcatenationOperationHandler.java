/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.operationprinter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenOperationHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.PLUS;
import static de.monticore.types3.SymTypeRelations.isStringOrSubType;

/**
 * Handles "+"-operator for String concatenation
 */
public class JavaStringConcatenationOperationHandler
    implements ICodeGenOperationHandler {

  @Override
  public boolean tryPrint(BinaryOperator operator,
      IndentPrinter printer,
      SymTypeExpression resultType,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    if (operator == PLUS
        && (isStringOrSubType(leftType) || isStringOrSubType(rightType))
    ) {
      printConvertedToString(printer, leftExprPrintAction);
      printer.print(" + ");
      printConvertedToString(printer, rightExprPrintAction);
      return true;
    }
    return false;
  }

  /**
   * conversion is required for weird cases with generics, e.g., {@code
   * <T extends String> String f(T t1, T t2) {
   * return String.valueOf(t1) + String.valueOf(t2);
   * }
   * }
   */
  protected void printConvertedToString(
      IndentPrinter printer,
      CodeGenPrintAction exprPrintAction
  ) {
    printer.print("String.valueOf(");
    exprPrintAction.print(printer);
    printer.print(")");
  }

}
