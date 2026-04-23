package de.monticore.expressions.bitexpressions.codegen.javagen;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.codegen.javagen.AbstractJavaGenVisitor;
import de.monticore.codegen.javagen.JavaOperationPrinter;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

public class BitExpressionsJavaGenVisitor extends AbstractJavaGenVisitor
    implements BitExpressionsHandler {

  // Traverser
  protected BitExpressionsTraverser traverser;

  public BitExpressionsJavaGenVisitor(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public BitExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(BitExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTLeftShiftExpression node) {
    SymTypeExpression resulType = normalize(typeOf(node));
    SymTypeExpression leftType = normalize(typeOf(node.getLeft()));
    SymTypeExpression rightType = normalize(typeOf(node.getRight()));
    // missing: create correct method and replace here
    //JavaOperationPrinter.printLessEqual(
    //    getPrinter(), resulType, leftType, rightType,
    //    p -> node.getLeft().accept(getTraverser()),
    //    p -> node.getRight().accept(getTraverser())
    //);
    _willBeRemoved_logUnimplemented(node);
  }

  // todo fill in the rest

  @Override
  public void handle(ASTRightShiftExpression node) {
    _willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void handle(ASTLogicalRightShiftExpression node) {
    _willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void handle(ASTBinaryAndExpression node) {
    _willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void handle(ASTBinaryXorExpression node) {
    _willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void handle(ASTBinaryOrOpExpression node) {
    _willBeRemoved_logUnimplemented(node);
  }

}
