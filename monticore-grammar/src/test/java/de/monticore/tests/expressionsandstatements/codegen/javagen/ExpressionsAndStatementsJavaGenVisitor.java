// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsInheritanceHandler;

/**
 * prints a list of Java statements for tests
 */
public class ExpressionsAndStatementsJavaGenVisitor
    extends ExpressionsAndStatementsInheritanceHandler
    implements JavaGenVisitor {

  protected IndentPrinter printer;

  public ExpressionsAndStatementsJavaGenVisitor(IndentPrinter printer) {
    this.printer = Preconditions.checkNotNull(printer);
  }

  @Override
  public IndentPrinter getPrinter() {
    return printer;
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
      endStatement();
    }
  }

}
