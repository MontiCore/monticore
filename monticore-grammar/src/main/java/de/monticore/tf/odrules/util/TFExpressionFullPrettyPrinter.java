/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.util;

import com.google.common.collect.Lists;
import de.monticore.expressions.assignmentexpressions._prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.javaclassexpressions._prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.javalight._prettyprint.JavaLightPrettyPrinter;
import de.monticore.literals.mccommonliterals._prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.literals.mcjavaliterals._prettyprint.MCJavaLiteralsPrettyPrinter;
import de.monticore.mcbasics._prettyprint.MCBasicsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mccommonstatements._prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.statements.mcvardeclarationstatements._prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;
import de.monticore.tf.odrules.HierarchyHelper;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._ast.ASTODRulesNode;
import de.monticore.tf.odrules._visitor.ODRulesTraverser;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by
 *
 */
public class TFExpressionFullPrettyPrinter {

  protected List<String> lhsObjects;
  protected final HierarchyHelper helper;
  protected final IndentPrinter printer;
  protected final ODRulesTraverser traverser;

  public TFExpressionFullPrettyPrinter(IndentPrinter out) {
    this(out, Lists.newArrayList(), new HierarchyHelper());
  }

  public TFExpressionFullPrettyPrinter(IndentPrinter out,  List<ASTMatchingObject> objects, HierarchyHelper helper) {
    this.traverser = ODRulesMill.traverser();

    MCBasicsPrettyPrinter mcBasicsPrettyPrinter = new MCBasicsPrettyPrinter(out, true);
    traverser.add4MCBasics(mcBasicsPrettyPrinter);

    MCCommonLiteralsPrettyPrinter mcCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(out, true);
    traverser.add4MCCommonLiterals(mcCommonLiteralsPrettyPrinter);
    traverser.setMCCommonLiteralsHandler(mcCommonLiteralsPrettyPrinter);
    de.monticore.literals.mcjavaliterals._prettyprint.MCJavaLiteralsPrettyPrinter mcJavaLiteralsPrettyPrinter = new MCJavaLiteralsPrettyPrinter(out, true); //b
    traverser.add4MCJavaLiterals(mcJavaLiteralsPrettyPrinter);
    traverser.setMCJavaLiteralsHandler(mcJavaLiteralsPrettyPrinter);

    de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(out, true); // a
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    MCCollectionTypesPrettyPrinter mcCollectionTypesPrettyPrinter = new MCCollectionTypesPrettyPrinter(out, true);
    traverser.setMCCollectionTypesHandler(mcCollectionTypesPrettyPrinter);
    traverser.add4MCCollectionTypes(mcCollectionTypesPrettyPrinter);
    MCSimpleGenericTypesPrettyPrinter mcSimpleGenericTypesPrettyPrinter = new MCSimpleGenericTypesPrettyPrinter(out, true);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesPrettyPrinter);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesPrettyPrinter);

    MCCommonStatementsPrettyPrinter mcCommonStatementsPrettyPrinter = new MCCommonStatementsPrettyPrinter(out, true);
    traverser.add4MCCommonStatements(mcCommonStatementsPrettyPrinter);
    traverser.setMCCommonStatementsHandler(mcCommonStatementsPrettyPrinter);
    MCVarDeclarationStatementsPrettyPrinter mcVarDeclarationStatementsPrettyPrinter = new MCVarDeclarationStatementsPrettyPrinter(out, true);
    traverser.add4MCVarDeclarationStatements(mcVarDeclarationStatementsPrettyPrinter);
    traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatementsPrettyPrinter);

    CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new CommonExpressionsPrettyPrinter(out, true);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    AssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter = new AssignmentExpressionsPrettyPrinter(out, true);
    traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);

    JavaLightPrettyPrinter javaLightPrettyPrinter = new JavaLightPrettyPrinter(out, true);
    traverser.setJavaLightHandler(javaLightPrettyPrinter);
    traverser.add4JavaLight(javaLightPrettyPrinter);

    JavaClassExpressionsPrettyPrinter javaClassExpressionsPrettyPrinter = new JavaClassExpressionsPrettyPrinter(out, true);
    traverser.setJavaClassExpressionsHandler(javaClassExpressionsPrettyPrinter);
    traverser.add4JavaClassExpressions(javaClassExpressionsPrettyPrinter);


    // Custom handling of the name expression
    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter =
    new ExpressionsBasisPrettyPrinter(out, true){
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
        getPrinter().print(node.getName());
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

  public String prettyprint(ASTODRulesNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent().stripTrailing();
  }

  public String prettyprint(ASTMCCommonStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent().stripTrailing();
  }

  public String prettyprint(ASTExpressionsBasisNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent().stripTrailing();
  }

  public String prettyprint(ASTMCVarDeclarationStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent().stripTrailing();
  }

  public ODRulesTraverser getTraverser() {
    return traverser;
  }
}
