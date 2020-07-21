/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode;
import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.javalight._ast.ASTJavaLightNode;
import de.monticore.javalight._visitor.JavaLightDelegatorVisitor;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class JavaLightPrettyPrinterDelegator extends JavaLightDelegatorVisitor {

  protected JavaLightDelegatorVisitor realThis;

  protected IndentPrinter printer;

  public JavaLightPrettyPrinterDelegator(IndentPrinter printer) {
    this.realThis = this;
    this.printer = printer;
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCCommonStatementsVisitor(new MCCommonStatementsPrettyPrinter(printer));
    setAssignmentExpressionsVisitor(new AssignmentExpressionsPrettyPrinter(printer));
    setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
    setMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsPrettyPrinter(printer));
    setJavaLightVisitor(new JavaLightPrettyPrinter(printer));
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTJavaLightNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public JavaLightDelegatorVisitor getRealThis() {
    return realThis;
  }
}
