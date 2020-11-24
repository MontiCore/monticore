/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mcsynchronizedstatements.MCSynchronizedStatementsMill;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTMCSynchronizedStatementsNode;
import de.monticore.statements.mcsynchronizedstatements._visitor.MCSynchronizedStatementsTraverser;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class MCSynchronizedStatementsFullPrettyPrinter extends MCCommonStatementsFullPrettyPrinter {

  private MCSynchronizedStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCSynchronizedStatementsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCSynchronizedStatementsMill.traverser();

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

    MCSynchronizedStatementsPrettyPrinter synchronizedStatements = new MCSynchronizedStatementsPrettyPrinter(printer);
    traverser.setMCSynchronizedStatementsHandler(synchronizedStatements);
    traverser.addMCSynchronizedStatementsVisitor(synchronizedStatements);
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTMCSynchronizedStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  @Override
  public MCSynchronizedStatementsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCSynchronizedStatementsTraverser traverser) {
    this.traverser = traverser;
  }
}
