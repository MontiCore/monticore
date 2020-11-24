/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTJavaLightNode;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.statements.prettyprint.MCArrayStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCCommonStatementsFullPrettyPrinter;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class JavaLightFullPrettyPrinter extends MCCommonStatementsFullPrettyPrinter {

  protected JavaLightTraverser traverser;

  public JavaLightFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = JavaLightMill.traverser();

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.addMCBasicTypesVisitor(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.addExpressionsBasisVisitor(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCCommonStatementsPrettyPrinter commonStatements = new MCCommonStatementsPrettyPrinter(printer);
    traverser.addMCCommonStatementsVisitor(commonStatements);
    traverser.setMCCommonStatementsHandler(commonStatements);

    AssignmentExpressionsPrettyPrinter assignmentExpressions = new AssignmentExpressionsPrettyPrinter(printer);
    traverser.addAssignmentExpressionsVisitor(assignmentExpressions);
    traverser.setAssignmentExpressionsHandler(assignmentExpressions);

    MCVarDeclarationStatementsPrettyPrinter varDecl = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.addMCVarDeclarationStatementsVisitor(varDecl);
    traverser.setMCVarDeclarationStatementsHandler(varDecl);

    JavaLightPrettyPrinter javaLight = new JavaLightPrettyPrinter(printer);
    traverser.addJavaLightVisitor(javaLight);
    traverser.setJavaLightHandler(javaLight);

    JavaClassExpressionsPrettyPrinter javaClassExpressions = new JavaClassExpressionsPrettyPrinter(printer);
    traverser.addJavaClassExpressionsVisitor(javaClassExpressions);
    traverser.setJavaClassExpressionsHandler(javaClassExpressions);

    MCArrayStatementsPrettyPrinter arrayStatements = new MCArrayStatementsPrettyPrinter(printer);
    traverser.addMCArrayStatementsVisitor(arrayStatements);
    traverser.setMCArrayStatementsHandler(arrayStatements);

    traverser.addMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTJavaLightNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public JavaLightTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(JavaLightTraverser traverser) {
    this.traverser = traverser;
  }
}
