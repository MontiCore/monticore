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

  protected MCSynchronizedStatementsTraverser traverser;

  public MCSynchronizedStatementsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCSynchronizedStatementsMill.traverser();

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);

    MCCommonStatementsPrettyPrinter commonStatements = new MCCommonStatementsPrettyPrinter(printer);
    traverser.setMCCommonStatementsHandler(commonStatements);
    traverser.add4MCCommonStatements(commonStatements);

    MCVarDeclarationStatementsPrettyPrinter varDecl = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.setMCVarDeclarationStatementsHandler(varDecl);
    traverser.add4MCVarDeclarationStatements(varDecl);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);

    MCSynchronizedStatementsPrettyPrinter synchronizedStatements = new MCSynchronizedStatementsPrettyPrinter(printer);
    traverser.setMCSynchronizedStatementsHandler(synchronizedStatements);
    traverser.add4MCSynchronizedStatements(synchronizedStatements);
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
