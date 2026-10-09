/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.CodeGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getBoxedJavaTypePrint;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Common data and functionality shared between all Java Generation visitors
 */
public class JavaGenVisitorState
    extends CodeGenVisitorState {

  public JavaGenVisitorState(IndentPrinter printer) {
    super(printer);
  }

  // common cases

  /**
   * prints the beginning of the lambda
   * which returns the result of a Java code block.
   * s.a {@link #printExpressionEndLambda}.
   *
   * @param modelType the (model) type returned by the lambda
   */
  public void printExpressionBeginLambda(SymTypeExpression modelType) {
    this.getPrinter().print("((java.util.function.Supplier<");
    this.getPrinter().print(getBoxedJavaTypePrint(normalize(modelType)));
    this.getPrinter().println(">) () -> {");
    this.getPrinter().indent();
  }

  /**
   * prints the end of the lambda
   * which returns the result of a Java code block.
   * s.a {@link #printExpressionBeginLambda}.
   */
  public void printExpressionEndLambda() {
    this.getPrinter().unindent();
    this.getPrinter().print("}).get()");
  }

  public void startParentheses() {
    getPrinter().print("(");
  }

  public void endParentheses() {
    getPrinter().print(")");
  }

  public void startStatementBlock() {
    getPrinter().println("{");
    getPrinter().indent();
  }

  public void endStatementBlock() {
    getPrinter().unindent();
    getPrinter().println("}");
  }

  public void endStatement() {
    getPrinter().println(";");
  }

  // operands

  /**
   * s.a. {@link JavaGenSymTypeRelations#getOperandType}
   */
  public void printOperand(
      ASTExpression operand,
      ExpressionsBasisTraverser traverser
  ) {
    SymTypeExpression operandType = typeOf(operand);
    printConverted(getPrinter(),
        JavaGenSymTypeRelations.getOperandType(operandType),
        operandType,
        p -> operand.accept(traverser)
    );
  }

  // values not to be converted

  /**
   * e.g., `x = 2;` or the target of `x[0] = 2`,
   * as a cast is neither a valid statement nor an assignment target
   */
  protected Set<ASTExpression> expressionsWithoutValueConversion =
      Collections.newSetFromMap(new IdentityHashMap<>());

  public void printWithoutValueConversion(
      ASTExpression expr,
      ExpressionsBasisTraverser traverser
  ) {
    expressionsWithoutValueConversion.add(expr);
    expr.accept(traverser);
    expressionsWithoutValueConversion.remove(expr);
  }

  public boolean isWithoutValueConversion(ASTExpression expr) {
    return expressionsWithoutValueConversion.contains(expr);
  }

  // temporary

  /**
   * deprecated in the sense that this is only temporary
   * and will be removed once all ASTNodes that should be supported are.
   */
  public void _willBeRemoved_logUnimplemented(ASTNode node) {
    Log.error("0xFD124 Java code generation for "
            + node.getClass().getSimpleName()
            + " has not been implemented.",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
    );
  }

}
