// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mclowlevelstatements.MCLowLevelStatementsMill;
import de.monticore.statements.mclowlevelstatements._ast.ASTMCLowLevelStatementsNode;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsTraverser;

public class MCLowLevelStatementsFullPrettyPrinter {

  private MCLowLevelStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCLowLevelStatementsFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = MCLowLevelStatementsMill.traverser();

    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));

    MCLowLevelStatementsPrettyPrinter lowLevel = new MCLowLevelStatementsPrettyPrinter(printer);
    traverser.setMCLowLevelStatementsHandler(lowLevel);
    traverser.add4MCLowLevelStatements(lowLevel);
  }

  public void setTraverser(MCLowLevelStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public MCLowLevelStatementsTraverser getTraverser() {
    return traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCLowLevelStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
}
