/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;

import static de.monticore.types3.SymTypeRelations.normalize;

public abstract class CodeGenOperationPrinter {

  protected static CodeGenOperationPrinter delegate;

  protected Collection<ICodeGenOperationHandler> operatorHandlers;

  protected Collection<ICodeGenOperationHandler> getOperatorHandlers() {
    return this.operatorHandlers;
  }

  protected void setOperatorHandlers(Collection<ICodeGenOperationHandler> conversionHandlers) {
    this.operatorHandlers = conversionHandlers;
  }

  // interface

  public static void printPlus(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.PLUS,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printMinus(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.MINUS,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printMultiply(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction left, CodeGenPrintAction right) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.MULTIPLY,
        printer, resultType, leftType, rightType, left, right);
  }

  public static void printDivide(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.DIVIDE,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printModulo(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.MODULO,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printEquals(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.EQUALS,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printNotEquals(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.NOT_EQUALS,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printGreaterThan(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.GREATER_THAN,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printLessThan(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.LESS_THAN,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printGreaterEqual(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.GREATER_EQUALS,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printLessEqual(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.LESS_EQUALS,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printBitwiseAnd(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.BITWISE_AND,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printBitwiseOr(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.BITWISE_OR,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printBitwiseXor(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.BITWISE_XOR,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printLeftShift(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.LEFT_SHIFT,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printRightShiftSigned(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.RIGHT_SHIFT_SIGNED,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  public static void printRightShiftUnsigned(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.RIGHT_SHIFT_UNSIGNED,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  // needs extension: add missing functions,
  // see ICodeGenOperationHandler

  public static void printAssignment(
      IndentPrinter printer, SymTypeExpression resultType,
      SymTypeExpression leftType, SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    getDelegate().printOperation(
        ICodeGenOperationHandler.BinaryOperator.ASSIGNMENT,
        printer, resultType, leftType, rightType,
        leftExprPrintAction, rightExprPrintAction
    );
  }

  protected void printOperation(
      ICodeGenOperationHandler.BinaryOperator op,
      IndentPrinter printer,
      SymTypeExpression resultType,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  ) {
    final SymTypeExpression resultTypeNormalized = normalize(resultType);
    final SymTypeExpression leftTypeNormalized = normalize(leftType);
    final SymTypeExpression rightTypeNormalized = normalize(rightType);

    int numPrinted = 0;
    for (ICodeGenOperationHandler handler : getOperatorHandlers()) {
      if (handler.tryPrint(op, printer,
          resultTypeNormalized, leftTypeNormalized, rightTypeNormalized,
          leftExprPrintAction, rightExprPrintAction
      )) {
        numPrinted++;
      }
    }
    if (numPrinted == 0) {
      Log.error("0xFD247 None of the operation printers supports " + op
          + " with result type " + resultTypeNormalized.printFullName()
          + " and operand types " + leftTypeNormalized.printFullName() + ", "
          + rightTypeNormalized.printFullName()
      );
    }
    else if (numPrinted > 1) {
      Log.error("0xFD278 internal error:"
          + " multiple operation printers claim support for " + op
          + " with result type " + resultTypeNormalized.printFullName()
          + " and operand types " + leftTypeNormalized.printFullName() + ", "
          + rightTypeNormalized.printFullName() + System.lineSeparator()
          + "Operation printers must be mutually exclusive."
      );
    }
  }

  // static delegate
  public static void reset() {
    CodeGenOperationPrinter.delegate = null;
  }

  protected static void setDelegate(CodeGenOperationPrinter newDelegate) {
    CodeGenOperationPrinter.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static CodeGenOperationPrinter getDelegate() {
    if (delegate == null) {
      throw new NullPointerException(
          "0xFDB11 internal error: "
              + "no CodeGenOperatorConverter has been init()-ialized."
      );
    }
    return delegate;
  }
}
