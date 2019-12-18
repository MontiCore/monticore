// (c) https://github.com/MontiCore/monticore

package de.monticore.expressions.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.CombineExpressionsDelegatorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;

public class ExpressionsPrettyPrinterDelegator extends CombineExpressionsDelegatorVisitor {

  protected CombineExpressionsDelegatorVisitor realThis;
  protected IndentPrinter printer;

  public ExpressionsPrettyPrinterDelegator(IndentPrinter printer){
    this.realThis = this;
    this.printer = printer;

    setAssignmentExpressionsVisitor(new AssignmentExpressionsPrettyPrinter(printer));
    setBitExpressionsVisitor(new BitExpressionsPrettyPrinter(printer));
    setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setJavaClassExpressionsVisitor(new JavaClassExpressionsPrettyPrinter(printer));
    setSetExpressionsVisitor(new SetExpressionsPrettyPrinter(printer));
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter(){
    return this.printer;
  }

  public String prettyprint(ASTExpression expr){
    getPrinter().clearBuffer();
    expr.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public CombineExpressionsDelegatorVisitor getRealThis(){
    return realThis;
  }
}
