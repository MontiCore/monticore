package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mcexceptionstatements._ast.ASTMCExceptionStatementsNode;
import de.monticore.statements.mcexceptionstatements._visitor.MCExceptionStatementsDelegatorVisitor;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTMCSynchronizedStatementsNode;
import de.monticore.statements.mcsynchronizedstatements._visitor.MCSynchronizedStatementsDelegatorVisitor;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class MCSynchronizedStatementsPrettyPrinterDelegator extends MCSynchronizedStatementsDelegatorVisitor {

  protected MCSynchronizedStatementsDelegatorVisitor realThis;

  protected IndentPrinter printer;

  public MCSynchronizedStatementsPrettyPrinterDelegator(IndentPrinter printer) {
    this.realThis = this;
    this.printer = printer;
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setMCCommonStatementsVisitor(new MCCommonStatementsPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCSynchronizedStatementsVisitor(new MCSynchronizedStatementsPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTMCSynchronizedStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public MCSynchronizedStatementsDelegatorVisitor getRealThis() {
    return realThis;
  }

}
