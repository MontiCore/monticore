/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mcreturnstatements.MCReturnStatementsMill;
import de.monticore.statements.mcreturnstatements._ast.ASTMCReturnStatementsNode;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsTraverser;

public class MCReturnStatementsFullPrettyPrinter {

  private MCReturnStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCReturnStatementsFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = MCReturnStatementsMill.traverser();

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCReturnStatementsPrettyPrinter returnStatements = new MCReturnStatementsPrettyPrinter(printer);
    traverser.add4MCReturnStatements(returnStatements);
    traverser.setMCReturnStatementsHandler(returnStatements);

    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));
  }

  public MCReturnStatementsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCReturnStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCReturnStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
  
}
