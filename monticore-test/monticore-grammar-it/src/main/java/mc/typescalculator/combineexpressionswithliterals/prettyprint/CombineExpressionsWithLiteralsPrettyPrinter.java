/* (c) https://github.com/MontiCore/monticore */

package mc.typescalculator.combineexpressionswithliterals.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.*;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import mc.typescalculator.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;

@Deprecated(forRemoval = true)
public class CombineExpressionsWithLiteralsPrettyPrinter {

  protected IndentPrinter printer;
  private CombineExpressionsWithLiteralsTraverser traverser;

  public CombineExpressionsWithLiteralsPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = CombineExpressionsWithLiteralsMill.traverser();

    AssignmentExpressionsPrettyPrinter assignmentExpressions = new AssignmentExpressionsPrettyPrinter(printer);
    traverser.add4AssignmentExpressions(assignmentExpressions);
    traverser.setAssignmentExpressionsHandler(assignmentExpressions);

    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    traverser.add4CommonExpressions(commonExpressions);
    traverser.setCommonExpressionsHandler(commonExpressions);

    BitExpressionsPrettyPrinter bitExpressions = new BitExpressionsPrettyPrinter(printer);
    traverser.add4BitExpressions(bitExpressions);
    traverser.setBitExpressionsHandler(bitExpressions);

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);
  }

  public String prettyprint(ASTExpression node) {
    this.printer.clearBuffer();
    node.accept(getTraverser());
    return this.printer.getContent();
  }

  public String prettyprint(ASTLiteral node) {
    this.printer.clearBuffer();
    node.accept(getTraverser());
    return this.printer.getContent();
  }

  public CombineExpressionsWithLiteralsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CombineExpressionsWithLiteralsTraverser traverser) {
    this.traverser = traverser;
  }
}
