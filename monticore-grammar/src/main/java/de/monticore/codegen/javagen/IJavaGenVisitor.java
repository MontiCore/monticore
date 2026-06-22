/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.ICodeGenVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2BoxedJavaType;
import static de.monticore.types3.SymTypeRelations.normalize;

/**
 * Common functionality shared between all Java Generation visitors
 */
public interface IJavaGenVisitor
    extends ICodeGenVisitor {

  /**
   * prints the beginning of the lambda
   * which returns the result of a Java code block.
   * s.a {@link #printExpressionEndLambda}.
   *
   * @param modelType the (model) type returned by the lambda
   */
  default void printExpressionBeginLambda(SymTypeExpression modelType) {
    this.getPrinter().print("((java.util.function.Supplier<");
    this.getPrinter().print(convert2BoxedJavaType(normalize(modelType)));
    this.getPrinter().println(">) () -> {");
    this.getPrinter().indent();
  }

  /**
   * prints the end of the lambda
   * which returns the result of a Java code block.
   * s.a {@link #printExpressionBeginLambda}.
   */
  default void printExpressionEndLambda() {
    this.getPrinter().unindent();
    this.getPrinter().print("}).get()");
  }

  // common cases

  default void startParentheses() {
    getPrinter().print("(");
  }

  default void endParentheses() {
    getPrinter().print(")");
  }

  default void endStatement() {
    getPrinter().println(";");
  }

  // temporary

  /**
   * deprecated in the sense that this is only temporary
   * and will be removed once all ASTNodes that should be supported are.
   */
  default void _willBeRemoved_logUnimplemented(ASTNode node) {
    Log.error("0xFD124 Java code generation for "
            + node.getClass().getSimpleName()
            + " has not been implemented.",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
    );
  }

}
