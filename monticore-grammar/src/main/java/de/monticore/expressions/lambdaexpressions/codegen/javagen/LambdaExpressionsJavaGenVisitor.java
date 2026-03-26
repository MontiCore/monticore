/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.lambdaexpressions.codegen.javagen;

import de.monticore.codegen.javagen.AbstractJavaGenVisitor;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpressionBody;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsHandler;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2BoxedJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaType;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

public class LambdaExpressionsJavaGenVisitor extends AbstractJavaGenVisitor
    implements LambdaExpressionsHandler {

  // Traverser
  protected LambdaExpressionsTraverser traverser;

  @Override
  public LambdaExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(LambdaExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public LambdaExpressionsJavaGenVisitor(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTLambdaExpressionBody node) {
    startParentheses();
    node.getExpression().accept(getTraverser());
    endParentheses();
  }

  @Override
  public void handle(ASTLambdaExpression node) {
    SymTypeOfFunction funcType = normalize(typeOf(node)).asFunctionType();

    startParentheses();

    // cast to Java function type
    getPrinter().print("(");
    getPrinter().print(convert2JavaType(funcType));
    getPrinter().print(") ");

    // parameters
    startParentheses();
    for (int i = 0; i < node.getLambdaParameters().sizeLambdaParameters(); i++) {
      ASTLambdaParameter par = node.getLambdaParameters().getLambdaParameter(i);
      String parName = par.getName();
      SymTypeExpression parType = funcType.getArgumentType(i);
      if (i != 0) {
        getPrinter().print(", ");
      }
      getPrinter().print(convert2BoxedJavaType(parType));
      getPrinter().print(" ");
      getPrinter().print(parName);
    }
    endParentheses();

    getPrinter().print(" -> ");

    // body
    node.getLambdaBody().accept(getTraverser());

    endParentheses();
  }

}
