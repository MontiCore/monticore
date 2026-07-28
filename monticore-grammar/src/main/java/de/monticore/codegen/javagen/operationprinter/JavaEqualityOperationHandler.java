/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.operationprinter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenOperationHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.EQUALS;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.NOT_EQUALS;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.generatesToJavaPrimitive;
import static de.monticore.types3.SymTypeRelations.isSubTypeOf;

// may need to be split in the future

/**
 * Handles ==, != for any input
 */
public class JavaEqualityOperationHandler
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
    if (operator == EQUALS) {
      printEquals(
          printer, leftType, rightType,
          leftExprPrintAction, rightExprPrintAction
      );
      return true;
    }
    else if (operator == NOT_EQUALS) {
      printer.print("!(");
      printEquals(
          printer, leftType, rightType,
          leftExprPrintAction, rightExprPrintAction
      );
      printer.print(")");
      return true;
    }
    else {
      return false;
    }
  }

  public void printEquals(
      IndentPrinter printer,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    // note:
    // convert to same type beforehand to assure implicit conversion happens
    // (e.g., SI Units)

    // slight optimization for numbers:
    if (generatesToJavaPrimitive(leftType) && generatesToJavaPrimitive(rightType)) {
      printWithEqualsOperator(
          printer, leftType, rightType,
          leftExprPrintAction, rightExprPrintAction
      );
    }
    // needs extension for OCL Collection types
    // Object identity
    else if ((leftType.isObjectType() || leftType.isGenericType())
        && (rightType.isObjectType() || rightType.isGenericType())) {
      printWithEqualsOperator(
          printer, leftType, rightType,
          leftExprPrintAction, rightExprPrintAction
      );
    }
    // tuples, arrays, etc.
    else {
      printer.print("java.util.Objects.equals(");
      printLeftConverted(printer, leftType, rightType, leftExprPrintAction);
      printer.print(", ");
      printRightConverted(printer, leftType, rightType, rightExprPrintAction);
      printer.print(")");
    }
  }

  protected void printWithEqualsOperator(
      IndentPrinter printer,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    printer.print("(");
    printLeftConverted(printer, leftType, rightType, leftExprPrintAction);
    printer.print(") == (");
    printRightConverted(printer, leftType, rightType, rightExprPrintAction);
    printer.print(")");
  }

  protected void printLeftConverted(
      IndentPrinter printer,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction
  ) {
    if (isSubTypeOf(leftType, rightType) && !isSubTypeOf(rightType, leftType)) {
      printConverted(printer, rightType, leftType, leftExprPrintAction);
    }
    else {
      leftExprPrintAction.print(printer);
    }
  }

  protected void printRightConverted(
      IndentPrinter printer,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction rightExprPrintAction
  ) {
    if (isSubTypeOf(rightType, leftType)) {
      printConverted(printer, leftType, rightType, rightExprPrintAction);
    }
    else {
      rightExprPrintAction.print(printer);
    }
  }
}
