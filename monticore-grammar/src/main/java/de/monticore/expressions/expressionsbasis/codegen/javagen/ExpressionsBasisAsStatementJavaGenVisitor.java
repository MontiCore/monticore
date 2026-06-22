/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.util.Node2Name;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaType;

/**
 * Alpha, proof of concept!
 * <p>
 * A Modification of the code generator to print expressions as statements,
 * i.e., {@code 2 + 5} is printed as (simplified) {@code
 * int l = 2;
 * int r = 5;
 * int sum = l + r;
 * }
 * The statements are additionally wrapped into a lambda to be an expression again.
 * <p>
 * WARNING: This is a proof of concept and is not generally applicable,
 * if the order of evaluation matters, e.g.,
 * {@code false || ++x==2} will increment x even though it should not.
 * Thus, any such case (especially lambdas) need to be printed accordingly
 * for a generalized solution.
 */
public class ExpressionsBasisAsStatementJavaGenVisitor
    extends ExpressionsBasisJavaGenVisitor
    implements ExpressionsBasisVisitor2 {

  public ExpressionsBasisAsStatementJavaGenVisitor(IndentPrinter printer) {
    super(printer);
  }

  // CodeGen

  Optional<ASTExpression> topMostExpr = Optional.empty();
  Map<ASTExpression, String> expr2Stmt = new HashMap<>();
  Stack<String> codeBeforeExpr = new Stack<>();

  @Override
  public void visit(ASTExpression expr) {
    if (topMostExpr.isEmpty()) {
      topMostExpr = Optional.of(expr);
    }

    codeBeforeExpr.push(getPrinter().getContent());
    getPrinter().clearBuffer();

    // start statement for current expr
    // comments may break here
    SymTypeExpression exprType = TypeCheck3.typeOf(expr);
    getPrinter().println("/*" + ExpressionsBasisMill.prettyPrint(expr, false) + "*/");
    getPrinter().print("final ");
    getPrinter().print(convert2JavaType(exprType));
    getPrinter().print(" ");
    getPrinter().print(getVarName(expr));
    getPrinter().print(" = ");
  }

  @Override
  public void endVisit(ASTExpression expr) {
    Preconditions.checkState(topMostExpr.isPresent());

    // finalize and collect statement for current expr
    getPrinter().println(";");
    String codeOfExpr = getPrinter().getContent();
    getPrinter().clearBuffer();
    expr2Stmt.put(expr, codeOfExpr);

    // recreate state from before the expression
    String prefix = codeBeforeExpr.pop();
    getPrinter().print(prefix);

    if (topMostExpr.get() != expr) {
      // replace current expr with corresponding variable
      getPrinter().print(getVarName(expr));
    }
    else {
      // print all statements in order,
      // collected into one expression
      // the lambda call may need to be moved, though,
      // if this concept is being generalized.
      topMostExpr = Optional.empty();

      SymTypeExpression exprType = TypeCheck3.typeOf(expr);
      printExpressionBeginLambda(exprType);

      // print all statements in order
      ExpressionsBasisTraverser traverser = ExpressionsBasisMill.inheritanceTraverser();
      traverser.add4ExpressionsBasis(new ExpressionsBasisVisitor2() {
        @Override
        public void endVisit(ASTExpression node) {
          getPrinter().print(expr2Stmt.get(node));
        }
      });
      expr.accept(traverser);

      getPrinter().println();
      getPrinter().print("return ");
      getPrinter().print(getVarName(expr));
      getPrinter().println(";");
      printExpressionEndLambda();
    }
  }

  // helper

  protected String getVarName(ASTExpression expr) {
    return "var_" + Node2Name.getName(expr);
  }

}
