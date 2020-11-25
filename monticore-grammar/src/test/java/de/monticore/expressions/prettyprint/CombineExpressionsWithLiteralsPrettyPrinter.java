// (c) https://github.com/MontiCore/monticore

package de.monticore.expressions.prettyprint;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

public class CombineExpressionsWithLiteralsPrettyPrinter {

  protected IndentPrinter printer;

  private CombineExpressionsWithLiteralsTraverser traverser;

  public CombineExpressionsWithLiteralsPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = CombineExpressionsWithLiteralsMill.traverser();

    AssignmentExpressionsPrettyPrinter assignmentExpressions = new AssignmentExpressionsPrettyPrinter(printer);
    traverser.addAssignmentExpressionsVisitor(assignmentExpressions);
    traverser.setAssignmentExpressionsHandler(assignmentExpressions);

    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    traverser.addCommonExpressionsVisitor(commonExpressions);
    traverser.setCommonExpressionsHandler(commonExpressions);

    BitExpressionsPrettyPrinter bitExpressions = new BitExpressionsPrettyPrinter(printer);
    traverser.addBitExpressionsVisitor(bitExpressions);
    traverser.setBitExpressionsHandler(bitExpressions);

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.addExpressionsBasisVisitor(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    JavaClassExpressionsPrettyPrinter javaClassExpressions = new JavaClassExpressionsPrettyPrinter(printer);
    traverser.addJavaClassExpressionsVisitor(javaClassExpressions);
    traverser.setJavaClassExpressionsHandler(javaClassExpressions);

    SetExpressionsPrettyPrinter setExpressions = new SetExpressionsPrettyPrinter(printer);
    traverser.addSetExpressionsVisitor(setExpressions);
    traverser.setSetExpressionsHandler(setExpressions);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.addMCCommonLiteralsVisitor(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    MCSimpleGenericTypesPrettyPrinter simpleGenericTypes = new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.addMCSimpleGenericTypesVisitor(simpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.addMCCollectionTypesVisitor(collectionTypes);
    traverser.setMCCollectionTypesHandler(collectionTypes);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.addMCBasicTypesVisitor(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
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
