/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.tupleexpressions.codegen.javagen;

import de.monticore.codegen.javagen.AbstractJavaGenVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.tupleexpressions._ast.ASTTupleExpression;
import de.monticore.expressions.tupleexpressions._visitor.TupleExpressionsHandler;
import de.monticore.expressions.tupleexpressions._visitor.TupleExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types3.TypeCheck3;

import java.util.Iterator;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getAsJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.printJavaType;

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
    SymTypeExpression tupleType = TypeCheck3.typeOf(node);
    SymTypeOfGenerics tupleJavaType = getAsJavaType(tupleType).asGenericType();
    getPrinter().print(tupleJavaType.getTypeConstructorFullName());

    getPrinter().print(".<");
    for (int i = 0; i < tupleJavaType.sizeArguments(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      getPrinter().print(printJavaType(tupleJavaType.getArgument(i)));
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
