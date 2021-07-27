/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.util;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.literals.prettyprint.MCJavaLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.JavaLightPrettyPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;
import de.monticore.tf.odrules.HierarchyHelper;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._ast.ASTODRulesNode;
import de.monticore.tf.odrules._visitor.ODRulesTraverser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by
 *
 */
public class TFExpressionFullPrettyPrinter
         {

  protected List<String> lhsObjects;
  private HierarchyHelper helper;
  private IndentPrinter printer;
  private ODRulesTraverser traverser;

  public TFExpressionFullPrettyPrinter(IndentPrinter out) {
    this(out, Lists.newArrayList(), new HierarchyHelper());
  }

  public TFExpressionFullPrettyPrinter(IndentPrinter out,  List<ASTMatchingObject> objects, HierarchyHelper helper) {
    this.traverser = ODRulesMill.traverser();

    MCBasicsPrettyPrinter mcBasicsPrettyPrinter = new MCBasicsPrettyPrinter(out);
    traverser.add4MCBasics(mcBasicsPrettyPrinter);

    MCCommonLiteralsPrettyPrinter mcCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(out);
    traverser.add4MCCommonLiterals(mcCommonLiteralsPrettyPrinter);
    traverser.setMCCommonLiteralsHandler(mcCommonLiteralsPrettyPrinter);
    MCJavaLiteralsPrettyPrinter mcJavaLiteralsPrettyPrinter = new MCJavaLiteralsPrettyPrinter(out);
    traverser.add4MCJavaLiterals(mcJavaLiteralsPrettyPrinter);
    traverser.setMCJavaLiteralsHandler(mcJavaLiteralsPrettyPrinter);

    MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(out);
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    MCCollectionTypesPrettyPrinter mcCollectionTypesPrettyPrinter = new MCCollectionTypesPrettyPrinter(out);
    traverser.setMCCollectionTypesHandler(mcCollectionTypesPrettyPrinter);
    traverser.add4MCCollectionTypes(mcCollectionTypesPrettyPrinter);
    MCSimpleGenericTypesPrettyPrinter mcSimpleGenericTypesPrettyPrinter = new MCSimpleGenericTypesPrettyPrinter(out);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesPrettyPrinter);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesPrettyPrinter);

    MCCommonStatementsPrettyPrinter mcCommonStatementsPrettyPrinter = new MCCommonStatementsPrettyPrinter(out);
    traverser.add4MCCommonStatements(mcCommonStatementsPrettyPrinter);
    traverser.setMCCommonStatementsHandler(mcCommonStatementsPrettyPrinter);
    MCVarDeclarationStatementsPrettyPrinter mcVarDeclarationStatementsPrettyPrinter = new MCVarDeclarationStatementsPrettyPrinter(out);
    traverser.add4MCVarDeclarationStatements(mcVarDeclarationStatementsPrettyPrinter);
    traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatementsPrettyPrinter);

    CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new CommonExpressionsPrettyPrinter(out);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    AssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter = new AssignmentExpressionsPrettyPrinter(out);
    traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);

    JavaLightPrettyPrinter javaLightPrettyPrinter = new JavaLightPrettyPrinter(out);
    traverser.setJavaLightHandler(javaLightPrettyPrinter);
    traverser.add4JavaLight(javaLightPrettyPrinter);

    JavaClassExpressionsPrettyPrinter javaClassExpressionsPrettyPrinter = new JavaClassExpressionsPrettyPrinter(out);
    traverser.setJavaClassExpressionsHandler(javaClassExpressionsPrettyPrinter);
    traverser.add4JavaClassExpressions(javaClassExpressionsPrettyPrinter);


    // Custom handling of the name expression
    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter =
    new ExpressionsBasisPrettyPrinter(out){
      @Override
      public void handle(ASTNameExpression node) {
        if(node.getName().startsWith("$") && lhsObjects.contains(node.getName())
                && !helper.isLhsListChild(node.getName()) && !helper.isRhsListChild(node.getName())){
          getPrinter().print("m.");
        }
        if(node.getName().startsWith("$") && lhsObjects.contains(node.getName())
                && (helper.isLhsListChild(node.getName()) || helper.isRhsListChild(node.getName()))){
          getPrinter().print("l.");
        }
        printNode(node.getName());
      }
    };
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);

    this.printer = out;
    this.helper = helper;
    this.lhsObjects = objects.stream().map(ASTMatchingObject::getObjectName).collect(Collectors.toList());
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  protected void printNode(String s) {
    getPrinter().print(s);
  }


  public String prettyprint(ASTODRulesNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCCommonStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpressionsBasisNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCVarDeclarationStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public ODRulesTraverser getTraverser() {
    return traverser;
  }
}
