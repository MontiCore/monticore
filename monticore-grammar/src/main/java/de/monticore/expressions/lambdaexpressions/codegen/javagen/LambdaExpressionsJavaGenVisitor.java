/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.lambdaexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpressionBody;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2BoxedJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaType;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

public class LambdaExpressionsJavaGenVisitor
    extends LambdaExpressionsInheritanceHandler {

  protected JavaGenVisitorState state;

  public LambdaExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTLambdaExpressionBody node) {
    state.startParentheses();
    node.getExpression().accept(getTraverser());
    state.endParentheses();
  }

  @Override
  public void traverse(ASTLambdaExpression node) {
    SymTypeOfFunction funcType = normalize(typeOf(node)).asFunctionType();

    state.startParentheses();

    // cast to Java function type
    getPrinter().print("(");
    getPrinter().print(convert2JavaType(funcType));
    getPrinter().print(") ");

    // parameters
    state.startParentheses();
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
    state.endParentheses();

    getPrinter().print(" -> ");

    // body
    node.getLambdaBody().accept(getTraverser());

    state.endParentheses();
  }

}
