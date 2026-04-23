/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.operationprinter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenOperationHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.ASSIGNMENT;

/**
 * Handles assignments for any input
 */
public class JavaAssignmentOperationHandler
    implements ICodeGenOperationHandler {

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
    if (operator != ASSIGNMENT) {
      return false;
    }

    printer.print("(");
    leftExprPrintAction.print(printer);
    printer.print(")");
    printer.print(" = ");
    printer.print("(");
    printConverted(printer, leftType, rightType, rightExprPrintAction);
    printer.print(")");

    return true;
  }

}
