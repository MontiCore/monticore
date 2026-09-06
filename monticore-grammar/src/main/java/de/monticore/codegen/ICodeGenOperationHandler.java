/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

public interface ICodeGenOperationHandler {

  enum BinaryOperator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,

    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_EQUALS,
    LESS_EQUALS,

    BOOLEAN_AND,
    BOOLEAN_OR,

    BITWISE_AND,
    BITWISE_OR,
    BITWISE_XOR,

    LEFT_SHIFT,
    RIGHT_SHIFT_SIGNED,
    RIGHT_SHIFT_UNSIGNED,

    ASSIGNMENT,
  }

  boolean tryPrint(
      BinaryOperator operator,
      IndentPrinter printer,
      SymTypeExpression resultType,
      SymTypeExpression leftType,
      SymTypeExpression rightType,
      CodeGenPrintAction leftExprPrintAction,
      CodeGenPrintAction rightExprPrintAction
  );

}
