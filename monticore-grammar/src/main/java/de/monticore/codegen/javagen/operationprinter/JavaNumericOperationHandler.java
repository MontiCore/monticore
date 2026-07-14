/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.operationprinter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenOperationHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.ICodeGenOperationHandler.BinaryOperator.*;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.generatesToJavaNumeric;
import static de.monticore.types3.SymTypeRelations.isNumericType;

/**
 * Supports numbers,
 * Does NOT handle (in)equality / assignment operations
 */
public class JavaNumericOperationHandler implements ICodeGenOperationHandler {

  protected static Map<BinaryOperator, String> BINARY_OPERATOR_2_JAVA;

  static {
    Map<BinaryOperator, String> binaryOperator2JavaTmp = new HashMap<>();
    binaryOperator2JavaTmp.put(PLUS, "+");
    binaryOperator2JavaTmp.put(MINUS, "-");
    binaryOperator2JavaTmp.put(MULTIPLY, "*");
    binaryOperator2JavaTmp.put(DIVIDE, "/");
    binaryOperator2JavaTmp.put(MODULO, "%");
    binaryOperator2JavaTmp.put(BITWISE_AND, "&");
    binaryOperator2JavaTmp.put(BITWISE_OR, "|");
    binaryOperator2JavaTmp.put(BITWISE_XOR, "^");
    binaryOperator2JavaTmp.put(LEFT_SHIFT, "<<");
    binaryOperator2JavaTmp.put(RIGHT_SHIFT_SIGNED, ">>");
    binaryOperator2JavaTmp.put(RIGHT_SHIFT_UNSIGNED, ">>>");
    binaryOperator2JavaTmp.put(GREATER_THAN, ">");
    binaryOperator2JavaTmp.put(LESS_THAN, "<");
    binaryOperator2JavaTmp.put(GREATER_EQUALS, ">=");
    binaryOperator2JavaTmp.put(LESS_EQUALS, "<=");
    BINARY_OPERATOR_2_JAVA =
        Collections.unmodifiableMap(binaryOperator2JavaTmp);
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
    if (!(isSupported(leftType) && isSupported(rightType))) {
      return false;
    }

    switch (operator) {
      case PLUS:
      case MINUS:
      case MULTIPLY:
      case DIVIDE:
      case MODULO:
      case BITWISE_AND:
      case BITWISE_OR:
      case BITWISE_XOR:
      case LEFT_SHIFT:
      case RIGHT_SHIFT_SIGNED:
      case RIGHT_SHIFT_UNSIGNED:
        printSimpleInfixOperator(
            printer, resultType, leftType, rightType,
            leftExprPrintAction, rightExprPrintAction,
            getJavaOperator(operator)
        );
        break;

      case GREATER_THAN:
      case LESS_THAN:
      case GREATER_EQUALS:
      case LESS_EQUALS:
        printBooleanInfix(
            printer, leftExprPrintAction, rightExprPrintAction,
            getJavaOperator(operator)
        );
        break;
      default:
        return false;
    }
    return true;
  }

  protected void printBooleanInfix(IndentPrinter printer,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction,
      String javaInfixOp
  ) {
    printer.print("(");
    leftExprPrintAction.print(printer);
    printer.print(")");
    printer.print(" " + javaInfixOp + " ");
    printer.print("(");
    rightExprPrintAction.print(printer);
    printer.print(")");
  }

  protected void printSimpleInfixOperator(
      IndentPrinter printer,
      SymTypeExpression resultType,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction,
      String javaInfixOp
  ) {
    printer.print("(");
    printConverted(printer, resultType, leftType, leftExprPrintAction);
    printer.print(")");
    printer.print(" " + javaInfixOp + " ");
    printer.print("(");
    printConverted(printer, resultType, rightType, rightExprPrintAction);
    printer.print(")");
  }

  protected boolean isSupported(SymTypeExpression modelType) {
    // specifically check the Java types,
    // in case that further primitives have been added.
    return isNumericType(modelType) && generatesToJavaNumeric(modelType);
  }

  protected String getJavaOperator(BinaryOperator operator) {
    return BINARY_OPERATOR_2_JAVA.get(operator);
  }

}
