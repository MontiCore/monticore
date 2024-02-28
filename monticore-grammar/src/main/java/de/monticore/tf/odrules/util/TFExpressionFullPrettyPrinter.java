/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.util;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;
import de.monticore.tf.odrules.HierarchyHelper;
import de.monticore.tf.odrules._ast.ASTODRulesNode;
import de.monticore.tf.odrules._prettyprint.ODRulesFullPrettyPrinter;
import de.monticore.tf.odrules._visitor.ODRulesTraverser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This is an ODFullPrettyPrinter which adds m. / l. when printing NameExpressions
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
    // Use the (generated) ODRulesFullPrettyPrinter
    ODRulesFullPrettyPrinter fpp = new ODRulesFullPrettyPrinter(out, true);
    this.traverser = fpp.getTraverser();

    // All the individual pretty printer are added by the ODRulesFullPrettyPrinter

    // Custom handling of the name expression - replace the ExpressionBasisPrettyPrinter
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
    traverser.getExpressionsBasisVisitorList().clear();
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
