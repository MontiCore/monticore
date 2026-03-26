/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.tupleexpressions.codegen.javagen;

import de.monticore.codegen.javagen.AbstractJavaGenVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.tupleexpressions._ast.ASTTupleExpression;
import de.monticore.expressions.tupleexpressions._visitor.TupleExpressionsHandler;
import de.monticore.expressions.tupleexpressions._visitor.TupleExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types3.TypeCheck3;

import java.util.Iterator;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2BoxedJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypeConstructor;

public class TupleExpressionsJavaGenVisitor extends AbstractJavaGenVisitor
    implements TupleExpressionsHandler {

  // Traverser
  protected TupleExpressionsTraverser traverser;

  public TupleExpressionsJavaGenVisitor(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public TupleExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(TupleExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  // CodeGen

  @Override
  public void handle(ASTTupleExpression node) {
    SymTypeOfTuple tupleType = TypeCheck3.typeOf(node).asTupleType();
    getPrinter().print(getJavaTypeConstructor(tupleType));

    getPrinter().print(".<");
    for (int i = 0; i < tupleType.sizeTypes(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      getPrinter().print(convert2BoxedJavaType(tupleType.getType(i)));
    }
    getPrinter().print(">of");

    startParentheses();
    Iterator<ASTExpression> expressionIterator = node.getExpressionList().iterator();
    while (expressionIterator.hasNext()) {
      ASTExpression expression = expressionIterator.next();
      expression.accept(traverser);
      if (expressionIterator.hasNext()) {
        getPrinter().print(", ");
      }
    }
    endParentheses();
  }

}
