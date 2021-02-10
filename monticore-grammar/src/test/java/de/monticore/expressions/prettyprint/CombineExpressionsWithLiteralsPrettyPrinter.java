// (c) https://github.com/MontiCore/monticore

package de.monticore.expressions.prettyprint;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

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

    JavaClassExpressionsPrettyPrinter javaClassExpressions = new JavaClassExpressionsPrettyPrinter(printer);
    traverser.add4JavaClassExpressions(javaClassExpressions);
    traverser.setJavaClassExpressionsHandler(javaClassExpressions);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    MCSimpleGenericTypesPrettyPrinter simpleGenericTypes = new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.add4MCSimpleGenericTypes(simpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.add4MCCollectionTypes(collectionTypes);
    traverser.setMCCollectionTypesHandler(collectionTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
  }

  public CombineExpressionsWithLiteralsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CombineExpressionsWithLiteralsTraverser traverser) {
    this.traverser = traverser;
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
}
