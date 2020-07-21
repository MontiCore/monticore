/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mcexceptionstatements._ast.ASTMCExceptionStatementsNode;
import de.monticore.statements.mcexceptionstatements._visitor.MCExceptionStatementsDelegatorVisitor;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class MCExceptionStatementsPrettyPrinterDelegator extends MCExceptionStatementsDelegatorVisitor {

  protected MCExceptionStatementsDelegatorVisitor realThis;

  protected IndentPrinter printer;

  public MCExceptionStatementsPrettyPrinterDelegator(IndentPrinter printer) {
    this.realThis = this;
    this.printer = printer;
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setMCCommonStatementsVisitor(new MCCommonStatementsPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsPrettyPrinter(printer));
    setMCExceptionStatementsVisitor(new MCExceptionStatementsPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTMCExceptionStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public MCExceptionStatementsDelegatorVisitor getRealThis() {
    return realThis;
  }
}
