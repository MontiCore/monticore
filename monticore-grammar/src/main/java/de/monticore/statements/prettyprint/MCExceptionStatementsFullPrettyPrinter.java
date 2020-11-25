/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mcexceptionstatements.MCExceptionStatementsMill;
import de.monticore.statements.mcexceptionstatements._ast.ASTMCExceptionStatementsNode;
import de.monticore.statements.mcexceptionstatements._visitor.MCExceptionStatementsTraverser;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class MCExceptionStatementsFullPrettyPrinter extends MCCommonStatementsFullPrettyPrinter {

  private MCExceptionStatementsTraverser traverser;

  public MCExceptionStatementsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCExceptionStatementsMill.traverser();

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

    MCExceptionStatementsPrettyPrinter exceptionStatements = new MCExceptionStatementsPrettyPrinter(printer);
    traverser.setMCExceptionStatementsHandler(exceptionStatements);
    traverser.addMCExceptionStatementsVisitor(exceptionStatements);
  }

  @Override
  public MCExceptionStatementsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCExceptionStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public String prettyprint(ASTMCExceptionStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
}
