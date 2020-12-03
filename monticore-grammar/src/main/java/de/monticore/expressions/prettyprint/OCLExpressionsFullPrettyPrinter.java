package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.oclexpressions.OCLExpressionsMill;
import de.monticore.expressions.oclexpressions._ast.*;
import de.monticore.expressions.oclexpressions._visitor.OCLExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class OCLExpressionsFullPrettyPrinter extends ExpressionsBasisFullPrettyPrinter {

  private OCLExpressionsTraverser traverser;
  
  public OCLExpressionsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = OCLExpressionsMill.traverser();

    ExpressionsBasisPrettyPrinter basisExpression = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.add4ExpressionsBasis(basisExpression);
    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
    OCLExpressionsPrettyPrinter oclExpressions = new OCLExpressionsPrettyPrinter(printer);
    traverser.setOCLExpressionsHandler(oclExpressions);
    traverser.add4OCLExpressions(oclExpressions);
  }

  @Override
  public OCLExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(OCLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTThenExpressionPart node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTElseExpressionPart node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTOCLQualification node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTOCLComprehensionExpr node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTOCLCollectionItem node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
