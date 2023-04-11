/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.expressions.javaclassexpressions._ast.ASTTypePattern;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcstatementsbasis._prettyprint.MCStatementsBasisPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

@Deprecated(forRemoval = true)
public class JavaClassExpressionsFullJavaPrinter extends CommonExpressionsFullJavaPrinter {
  
  protected JavaClassExpressionsTraverser traverser;
  
  @Override
  public JavaClassExpressionsTraverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(JavaClassExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  
  public JavaClassExpressionsFullJavaPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = JavaClassExpressionsMill.traverser();
    
    LegacyCommonExpressionsJavaPrinter commonExpression = new LegacyCommonExpressionsJavaPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpression);
    traverser.add4CommonExpressions(commonExpression);
    ExpressionsBasisJavaPrinter expressionBasis = new ExpressionsBasisJavaPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionBasis);
    traverser.add4ExpressionsBasis(expressionBasis);
    JavaClassExpressionsJavaPrinter javaClassExpression = new JavaClassExpressionsJavaPrinter(printer);
    traverser.setJavaClassExpressionsHandler(javaClassExpression);
    traverser.add4JavaClassExpressions(javaClassExpression);

    MCBasicTypesPrettyPrinter mcBasicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mcBasicTypes);
    traverser.add4MCBasicTypes(mcBasicTypes);

    MCStatementsBasisPrettyPrinter mcStatementsBasis = new MCStatementsBasisPrettyPrinter(printer, true);
    traverser.setMCStatementsBasisHandler(mcStatementsBasis);
    traverser.add4MCStatementsBasis(mcStatementsBasis);

    MCVarDeclarationStatementsPrettyPrinter mcVarDeclarationStatements = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatements);
    traverser.add4MCVarDeclarationStatements(mcVarDeclarationStatements);
  }
  
  public String print(ASTGenericInvocationSuffix node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String print(ASTTypePattern node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

}
