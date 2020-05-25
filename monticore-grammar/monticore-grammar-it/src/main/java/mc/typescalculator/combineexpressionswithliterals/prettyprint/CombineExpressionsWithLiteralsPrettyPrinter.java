// (c) https://github.com/MontiCore/monticore

package mc.typescalculator.combineexpressionswithliterals.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.*;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;

public class CombineExpressionsWithLiteralsPrettyPrinter extends CombineExpressionsWithLiteralsDelegatorVisitor {

  protected IndentPrinter printer;
  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  public CombineExpressionsWithLiteralsPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    realThis = this;

    setAssignmentExpressionsVisitor(new AssignmentExpressionsPrettyPrinter(printer));
    setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
    setBitExpressionsVisitor(new BitExpressionsPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
  }

  public String prettyprint(ASTExpression node) {
    this.printer.clearBuffer();
    node.accept(getRealThis());
    return this.printer.getContent();
  }

  public String prettyprint(ASTLiteral node) {
    this.printer.clearBuffer();
    node.accept(getRealThis());
    return this.printer.getContent();
  }
}
