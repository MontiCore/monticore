package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mcvardeclarationstatements.MCVarDeclarationStatementsMill;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsTraverser;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class MCVarDeclarationStatementsFullPrettyPrinter {

  private MCVarDeclarationStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCVarDeclarationStatementsFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = MCVarDeclarationStatementsMill.traverser();

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.addExpressionsBasisVisitor(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.addMCBasicTypesVisitor(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    MCVarDeclarationStatementsPrettyPrinter varDecl = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.addMCVarDeclarationStatementsVisitor(varDecl);
    traverser.setMCVarDeclarationStatementsHandler(varDecl);
  }

  public MCVarDeclarationStatementsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCVarDeclarationStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public String prettyprint(ASTMCVarDeclarationStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
}
