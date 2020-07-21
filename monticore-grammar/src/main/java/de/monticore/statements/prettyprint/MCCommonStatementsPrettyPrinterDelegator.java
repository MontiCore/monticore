/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsDelegatorVisitor;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatementsBasisNode;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class MCCommonStatementsPrettyPrinterDelegator extends MCCommonStatementsDelegatorVisitor {

  protected MCCommonStatementsDelegatorVisitor realThis;

  protected IndentPrinter printer;

  public MCCommonStatementsPrettyPrinterDelegator(IndentPrinter printer) {
    this.realThis = this;
    this.printer = printer;
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setMCCommonStatementsVisitor(new MCCommonStatementsPrettyPrinter(printer));
    setMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTMCCommonStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCVarDeclarationStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public MCCommonStatementsDelegatorVisitor getRealThis() {
    return realThis;
  }
}
