// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mclowlevelstatements.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabel;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabelledBreakStatement;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsInheritanceHandler;

/**
 * Provides Java code generations for MCLowLevelStatements
 * <p>
 * Alpha version:
 * Currently, this is first and foremost a "pretty printer"
 * without regard to edge cases.
 */
public class MCLowLevelStatementsJavaGenVisitor
    extends MCLowLevelStatementsInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCLowLevelStatementsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTLabelledBreakStatement node) {
    getPrinter().print("break");
    if (node.isPresentLabel()) {
      getPrinter().print(" ");
      getPrinter().print(node.getLabel());
    }
    state.endStatement();
  }

  @Override
  public void traverse(ASTContinueStatement node) {
    getPrinter().print("continue");
    if (node.isPresentLabel()) {
      getPrinter().print(" ");
      getPrinter().print(node.getLabel());
    }
    state.endStatement();
  }

  @Override
  public void traverse(ASTLabel node) {
    getPrinter().print(node.getName());
    getPrinter().print(": ");
    node.getMCStatement().accept(getTraverser());
  }

}
