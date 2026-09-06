// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsInheritanceHandler;

/**
 * prints a list of Java statements for tests
 */
public class ExpressionsAndStatementsJavaGenVisitor
    extends ExpressionsAndStatementsInheritanceHandler {

  protected JavaGenVisitorState state;

  public ExpressionsAndStatementsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTBehaviorInput node) {
    for (ASTMCBlockStatement stmt : node.getMCBlockStatementList()) {
      stmt.accept(getTraverser());
    }
    if (node.isPresentExpression()) {
      getPrinter().print("return ");
      node.getExpression().accept(getTraverser());
      state.endStatement();
    }
  }

}
