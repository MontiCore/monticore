/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.operationprinter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenOperationHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.EQUALS;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.NOT_EQUALS;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaType;
import static de.monticore.types3.SymTypeRelations.isBoolean;
import static de.monticore.types3.SymTypeRelations.isByte;
import static de.monticore.types3.SymTypeRelations.isChar;
import static de.monticore.types3.SymTypeRelations.isDouble;
import static de.monticore.types3.SymTypeRelations.isFloat;
import static de.monticore.types3.SymTypeRelations.isInt;
import static de.monticore.types3.SymTypeRelations.isLong;
import static de.monticore.types3.SymTypeRelations.isShort;
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
      printer.print(NOT_EQUALS);
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
    SymTypeExpression leftJavaType = getJavaType(leftType);
    SymTypeExpression rightJavaType = getJavaType(rightType);

    // note:
    // convert to same type beforehand to assure implicit conversion happens
    // (e.g., SI Units)

    // slight optimization for numbers:
    if (isJavaPrimitive(leftJavaType) && isJavaPrimitive(rightJavaType)) {
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

  // helper

  protected boolean isJavaPrimitive(SymTypeExpression javaType) {
    return javaType.isPrimitive() && (
        isBoolean(javaType)
            || isByte(javaType)
            || isShort(javaType)
            || isChar(javaType)
            || isInt(javaType)
            || isLong(javaType)
            || isFloat(javaType)
            || isDouble(javaType)
    );
  }

}
