/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsTraverser;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class MCCommonStatementsFullPrettyPrinter {

  private MCCommonStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCCommonStatementsFullPrettyPrinter(IndentPrinter printer) {
    this.traverser = MCCommonStatementsMill.traverser();
    this.printer = printer;

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.addExpressionsBasisVisitor(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.addMCBasicTypesVisitor(basicTypes);

    MCCommonStatementsPrettyPrinter commonStatements = new MCCommonStatementsPrettyPrinter(printer);
    traverser.setMCCommonStatementsHandler(commonStatements);
    traverser.addMCCommonStatementsVisitor(commonStatements);

    MCVarDeclarationStatementsPrettyPrinter varDecl = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.setMCVarDeclarationStatementsHandler(varDecl);
    traverser.addMCVarDeclarationStatementsVisitor(varDecl);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
  }

  public MCCommonStatementsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCCommonStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTMCCommonStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCVarDeclarationStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
}
