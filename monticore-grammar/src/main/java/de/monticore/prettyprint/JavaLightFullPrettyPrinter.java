/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTJavaLightNode;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.statements.prettyprint.MCArrayStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class JavaLightFullPrettyPrinter {

  private JavaLightTraverser traverser;

  protected IndentPrinter printer;

  public JavaLightFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    this.traverser = JavaLightMill.traverser();

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCCommonStatementsPrettyPrinter commonStatements = new MCCommonStatementsPrettyPrinter(printer);
    traverser.add4MCCommonStatements(commonStatements);
    traverser.setMCCommonStatementsHandler(commonStatements);

    AssignmentExpressionsPrettyPrinter assignmentExpressions = new AssignmentExpressionsPrettyPrinter(printer);
    traverser.add4AssignmentExpressions(assignmentExpressions);
    traverser.setAssignmentExpressionsHandler(assignmentExpressions);

    MCVarDeclarationStatementsPrettyPrinter varDecl = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.add4MCVarDeclarationStatements(varDecl);
    traverser.setMCVarDeclarationStatementsHandler(varDecl);

    JavaLightPrettyPrinter javaLight = new JavaLightPrettyPrinter(printer);
    traverser.add4JavaLight(javaLight);
    traverser.setJavaLightHandler(javaLight);

    JavaClassExpressionsPrettyPrinter javaClassExpressions = new JavaClassExpressionsPrettyPrinter(printer);
    traverser.add4JavaClassExpressions(javaClassExpressions);
    traverser.setJavaClassExpressionsHandler(javaClassExpressions);

    MCArrayStatementsPrettyPrinter arrayStatements = new MCArrayStatementsPrettyPrinter(printer);
    traverser.add4MCArrayStatements(arrayStatements);
    traverser.setMCArrayStatementsHandler(arrayStatements);

    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));
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
