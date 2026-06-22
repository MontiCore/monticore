/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.tupleexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.tupleexpressions._ast.ASTTupleExpression;
import de.monticore.expressions.tupleexpressions._visitor.TupleExpressionsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types3.TypeCheck3;

import java.util.Iterator;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2BoxedJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaTypeConstructor;

public class TupleExpressionsJavaGenVisitor
    extends TupleExpressionsInheritanceHandler {

  protected JavaGenVisitorState state;

  public TupleExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTTupleExpression node) {
    SymTypeOfTuple tupleType = TypeCheck3.typeOf(node).asTupleType();
    getPrinter().print(convert2JavaTypeConstructor(tupleType));

    getPrinter().print(".<");
    for (int i = 0; i < tupleType.sizeTypes(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      getPrinter().print(convert2BoxedJavaType(tupleType.getType(i)));
    }
    getPrinter().print(">of");

    state.startParentheses();
    Iterator<ASTExpression> expressionIterator = node.getExpressionList().iterator();
    while (expressionIterator.hasNext()) {
      ASTExpression expression = expressionIterator.next();
      expression.accept(traverser);
      if (expressionIterator.hasNext()) {
        getPrinter().print(", ");
      }
    }
    state.endParentheses();
  }

}
