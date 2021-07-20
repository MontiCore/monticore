/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mcarraystatements.MCArrayStatementsMill;
import de.monticore.statements.mcarraystatements._visitor.MCArrayStatementsTraverser;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class MCArrayStatementsFullPrettyPrinter extends MCVarDeclarationStatementsFullPrettyPrinter {

  protected MCArrayStatementsTraverser traverser;

  public MCArrayStatementsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = MCArrayStatementsMill.traverser();

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    MCVarDeclarationStatementsPrettyPrinter varDecl = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.add4MCVarDeclarationStatements(varDecl);
    traverser.setMCVarDeclarationStatementsHandler(varDecl);

    MCArrayStatementsPrettyPrinter arrayStatements = new MCArrayStatementsPrettyPrinter(printer);
    traverser.add4MCArrayStatements(arrayStatements);
    traverser.setMCArrayStatementsHandler(arrayStatements);
  }

  @Override
  public MCArrayStatementsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCArrayStatementsTraverser traverser) {
    this.traverser = traverser;
  }
}
