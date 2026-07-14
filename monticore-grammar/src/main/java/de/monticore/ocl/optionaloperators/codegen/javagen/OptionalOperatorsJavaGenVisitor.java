// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.optionaloperators.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalEqualsExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalExpressionPrefix;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalGreaterEqualExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalGreaterThanExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalLessEqualExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalLessThanExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalNotEqualsExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalNotSimilarExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalSimilarExpression;
import de.monticore.ocl.optionaloperators._visitor.OptionalOperatorsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations.isOptional;
import static de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory.createOptional;
import static de.monticore.types3.SymTypeRelations.isBoolean;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Provides Java code generations for OptionalOperators
 */
public class OptionalOperatorsJavaGenVisitor
    extends OptionalOperatorsInheritanceHandler {

  protected JavaGenVisitorState state;

  public IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  public OptionalOperatorsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  @Override
  public void traverse(ASTOptionalExpressionPrefix node) {
    SymTypeExpression exprType = normalize(typeOf(node));
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));

    // left side as Optional
    state.startParentheses();
    printConverted(
        getPrinter(),
        createOptional(exprType),
        leftType,
        p -> node.getLeft().accept(getTraverser())
    );
    getPrinter().println();
    getPrinter().indent();
    // right side
    getPrinter().print(".orElseGet(() -> ");
    printConverted(
        getPrinter(),
        exprType,
        rightType,
        p -> node.getRight().accept(getTraverser())
    );
    getPrinter().print(")");
    getPrinter().unindent();
    getPrinter().println();
    state.endParentheses();
  }

  @Override
  public void traverse(ASTOptionalLessEqualExpression node) {
    printOptionalFirstParameterComparison(node.getLeft(), node.getRight(), "<=");
  }

  @Override
  public void traverse(ASTOptionalGreaterEqualExpression node) {
    printOptionalFirstParameterComparison(node.getLeft(), node.getRight(), ">=");
  }

  @Override
  public void traverse(ASTOptionalLessThanExpression node) {
    printOptionalFirstParameterComparison(node.getLeft(), node.getRight(), "<");
  }

  @Override
  public void traverse(ASTOptionalGreaterThanExpression node) {
    printOptionalFirstParameterComparison(node.getLeft(), node.getRight(), ">");
  }

  @Override
  public void traverse(ASTOptionalEqualsExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(".map(v -> v.equals(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")).orElse(false))");
  }

  @Override
  public void traverse(ASTOptionalNotEqualsExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(".map(v -> !v.equals(");
    node.getRight().accept(getTraverser());
    getPrinter().print(")).orElse(false))");
  }

  protected void printOptionalFirstParameterComparison(
      de.monticore.expressions.expressionsbasis._ast.ASTExpression left,
      de.monticore.expressions.expressionsbasis._ast.ASTExpression right,
      String operator
  ) {
    getPrinter().print("(");
    left.accept(getTraverser());
    getPrinter().print(".map(v -> v ");
    getPrinter().print(operator);
    getPrinter().print(" ");
    right.accept(getTraverser());
    getPrinter().print(").orElse(false))");
  }

  @Override
  public void traverse(ASTOptionalSimilarExpression node) {
    ASTExpression leftExpr = node.getLeft();
    ASTExpression rightExpr = node.getRight();
    SymTypeExpression leftType = typeOf(node.getLeft());
    typeOf(node.getRight());
    Preconditions.checkState(isBoolean(typeOf(node)));
    Preconditions.checkState(isOptional(leftType));
    state.startParentheses();
    leftExpr.accept(getTraverser());
    state.endParentheses();
    getPrinter().print(".isPresent() && ");

    state.startParentheses();
    leftExpr.accept(getTraverser());
    state.endParentheses();
    getPrinter().print(".get()");
    getPrinter().print(".equals(");
    rightExpr.accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void traverse(ASTOptionalNotSimilarExpression node) {
    state.startParentheses();
    ASTExpression leftExpr = node.getLeft();
    ASTExpression rightExpr = node.getRight();
    SymTypeExpression leftType = typeOf(node.getLeft());
    typeOf(node.getRight());
    Preconditions.checkState(isBoolean(typeOf(node)));
    Preconditions.checkState(isOptional(leftType));
    state.startParentheses();
    leftExpr.accept(getTraverser());
    state.endParentheses();
    getPrinter().print(".isPresent() && !");

    state.startParentheses();
    leftExpr.accept(getTraverser());
    state.endParentheses();
    getPrinter().print(".get()");

    // is equals enough?
    getPrinter().print(".equals(");
    rightExpr.accept(getTraverser());
    getPrinter().print(")");
    state.endParentheses();
  }
}
