/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.AbstractCodeGenVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getAsJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.printJavaType;
import static de.monticore.types3.SymTypeRelations.normalize;

public abstract class AbstractJavaGenVisitor
    extends AbstractCodeGenVisitor {

  protected AbstractJavaGenVisitor(IndentPrinter printer) {
    super(printer);
  }

  // helper
  protected void startParentheses() {
    getPrinter().print("(");
  }

  protected void endParentheses() {
    getPrinter().print(")");
  }

  protected void endStatement() {
    getPrinter().print(";");
  }

  protected void printExpressionBeginLambda(SymTypeExpression type) {
    this.getPrinter().print("((java.util.function.Supplier<");
    this.getPrinter().print(printJavaType(getAsJavaType(normalize(type))));
    this.getPrinter().println(">)()->{");
    this.getPrinter().indent();
  }

  /**
   * prints the end of the lambda
   * which returns the result of a Java code block
   * s.a {@link #printExpressionBeginLambda}.
   */
  protected void printExpressionEndLambda() {
    this.getPrinter().unindent();
    this.getPrinter().print("}).get()");
  }

  // temporary

  /**
   * deprecated in the sense that this is only temporary
   *     and will be removed once all ASTNodes that should be supported are.
   */
  protected void _willBeRemoved_logUnimplemented(ASTNode node) {
    Log.error("0xFD124 Java code generation for "
            + node.getClass().getSimpleName()
            + " has not been implemented.",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
    );
  }

}
