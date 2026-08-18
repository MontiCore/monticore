package de.monticore.expressions.bitexpressions.codegen.javagen;/* (c) https://github.com/MontiCore/monticore */

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.JavaOperationPrinter;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

public class BitExpressionsJavaGenVisitor
    extends BitExpressionsInheritanceHandler {

  protected JavaGenVisitorState state;

  public BitExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTLeftShiftExpression node) {
    SymTypeExpression resulType = normalize(typeOf(node));
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));
    JavaOperationPrinter.printLeftShift(
        getPrinter(), resulType, leftType, rightType,
        p -> node.getLeft().accept(getTraverser()),
        p -> node.getRight().accept(getTraverser())
    );
  }

  @Override
  public void traverse(ASTRightShiftExpression node) {
    SymTypeExpression resulType = normalize(typeOf(node));
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));
    JavaOperationPrinter.printRightShiftSigned(
        getPrinter(), resulType, leftType, rightType,
        p -> node.getLeft().accept(getTraverser()),
        p -> node.getRight().accept(getTraverser())
    );
  }

  @Override
  public void traverse(ASTLogicalRightShiftExpression node) {
    SymTypeExpression resulType = normalize(typeOf(node));
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));
    JavaOperationPrinter.printRightShiftUnsigned(
        getPrinter(), resulType, leftType, rightType,
        p -> node.getLeft().accept(getTraverser()),
        p -> node.getRight().accept(getTraverser())
    );
  }

  @Override
  public void traverse(ASTBinaryAndExpression node) {
    SymTypeExpression resulType = normalize(typeOf(node));
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));
    JavaOperationPrinter.printBitwiseAnd(
        getPrinter(), resulType, leftType, rightType,
        p -> node.getLeft().accept(getTraverser()),
        p -> node.getRight().accept(getTraverser())
    );
  }

  @Override
  public void traverse(ASTBinaryXorExpression node) {
    SymTypeExpression resulType = normalize(typeOf(node));
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));
    JavaOperationPrinter.printBitwiseXor(
        getPrinter(), resulType, leftType, rightType,
        p -> node.getLeft().accept(getTraverser()),
        p -> node.getRight().accept(getTraverser())
    );
  }

  @Override
  public void traverse(ASTBinaryOrOpExpression node) {
    SymTypeExpression resulType = normalize(typeOf(node));
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));
    JavaOperationPrinter.printBitwiseOr(
        getPrinter(), resulType, leftType, rightType,
        p -> node.getLeft().accept(getTraverser()),
        p -> node.getRight().accept(getTraverser())
    );
  }

}
