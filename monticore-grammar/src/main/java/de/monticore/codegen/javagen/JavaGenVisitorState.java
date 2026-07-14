/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.CodeGenVisitorState;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getBoxedJavaTypePrint;
import static de.monticore.types3.SymTypeRelations.normalize;

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
