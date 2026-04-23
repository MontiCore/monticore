// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.uglyexpressions.codegen.javagen;

import de.monticore.codegen.javagen.AbstractJavaGenVisitor;
import de.monticore.expressions.uglyexpressions._ast.ASTArrayCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTArrayDimensionByExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTClassCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTCreatorExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTInstanceofExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsHandler;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.symTypeFromAST;
import static de.monticore.types3.TypeCheck3.typeOf;

public class UglyExpressionsJavaGenVisitor extends AbstractJavaGenVisitor
    implements UglyExpressionsHandler {

  // Traverser

  protected UglyExpressionsTraverser traverser;

  @Override
  public UglyExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(UglyExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public UglyExpressionsJavaGenVisitor(IndentPrinter printer) {
    super(printer);
  }

  // CodeGen

  @Override
  public void handle(ASTTypeCastExpression node) {
    SymTypeExpression targetType = normalize(symTypeFromAST(node.getMCType()));
    SymTypeExpression sourceType = normalize(typeOf(node.getExpression()));
    printConverted(getPrinter(),
        targetType,
        sourceType,
        (p) -> node.getExpression().accept(traverser)
    );
  }

  @Override
  public void handle(ASTInstanceofExpression node) {
    _willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void handle(ASTCreatorExpression node) {
    _willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void handle(ASTClassCreator node) {
    _willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void handle(ASTArrayCreator node) {
    _willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void handle(ASTArrayDimensionByExpression node) {
    _willBeRemoved_logUnimplemented(node);
  }

}
