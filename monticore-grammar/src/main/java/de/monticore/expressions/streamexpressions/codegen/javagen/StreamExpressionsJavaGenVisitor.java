/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.streamexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.expressions.streamexpressions._ast.ASTAppendAbsentStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendTickStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTConcatStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTLengthStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorElement;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;

import java.util.Iterator;
import java.util.List;

/**
 * Provides Java code generations for StreamExpressions
 */
public class StreamExpressionsJavaGenVisitor extends StreamExpressionsInheritanceHandler {

  protected JavaGenVisitorState state;

  public StreamExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTStreamConstructorExpression node) {

    SymTypeExpression type = TypeCheck3.typeOf(node);
    String streamClassName = SymTypeExpression2JavaConverter.getJavaTypeQName(type);
    getPrinter().print(streamClassName + ".of");
    getPrinter().print("(");

    List<ASTStreamConstructorElement> elems = node.getStreamConstructorElementList();

    if (!elems.isEmpty()) {
      if (streamClassName.endsWith("ToptStream")) {
        for (Iterator<ASTStreamConstructorElement> it = elems.iterator(); it.hasNext(); ) {
          ASTStreamConstructorElement el = it.next();

          if (el.isPresentAbsent()) {
            getPrinter().print("java.util.Optional.empty()");
          }
          else if (el.isPresentExpression()) {
            getPrinter().print("java.util.Optional.of(");
            el.getExpression().accept(traverser);
            getPrinter().print(")");
          }
          else {
            getPrinter().print("java.util.Optional.empty()");
          }

          if (it.hasNext()) getPrinter().print(", ");
        }
      }
      else if (streamClassName.endsWith("EventStream")) {
        boolean firstArg = true;
        int i = 0;

        while (i < elems.size()) {
          ASTStreamConstructorElement el = elems.get(i);
          i++;

          if (el.isPresentTick()) {
            // If two tick are followed after another or the tick is at the start or end insert an empty tick
            if (i == 1) {
              if (!firstArg) getPrinter().print(", ");
              firstArg = false;
              getPrinter().print("de.monticore.rte.streams.UntimedStream.of()");
            }
            if (i == elems.size() || elems.get(i).isPresentTick()) {
              getPrinter().print(", ");
              getPrinter().print("de.monticore.rte.streams.UntimedStream.of()");
            }
          }

          if (el.isPresentExpression()) {
            if (!firstArg) getPrinter().print(", ");
            firstArg = false;
            getPrinter().print("de.monticore.rte.streams.UntimedStream.of(");

            el.getExpression().accept(traverser);

            while (i < elems.size() && elems.get(i).isPresentExpression()) {
              getPrinter().print(", ");
              elems.get(i).getExpression().accept(traverser);
              i++;
            }

            getPrinter().print(")");
          }
        }
      }
      else {
        for (Iterator<ASTStreamConstructorElement> it = elems.iterator(); it.hasNext(); ) {
          ASTStreamConstructorElement el = it.next();

          if (el.isPresentExpression()) {
            el.getExpression().accept(traverser);
          }

          if (it.hasNext()) getPrinter().print(", ");
        }
      }
    }
    getPrinter().print(")");
  }

  @Override
  public void traverse(ASTStreamConstructorElement node) {
    node.getExpression().accept(traverser);
  }

  @Override
  public void traverse(ASTAppendStreamExpression node) {
    node.getRight().accept(traverser);

    getPrinter().print(".append(");
    node.getLeft().accept(traverser);
    getPrinter().print(")");

  }

  @Override
  public void traverse(ASTAppendAbsentStreamExpression node) {
    node.getStream().accept(traverser);
    getPrinter().print(".append(java.util.Optional.empty())");
  }

  @Override
  public void traverse(ASTAppendTickStreamExpression node) {
    node.getStream().accept(traverser);
    getPrinter().print(".append(de.monticore.rte.streams.UntimedStream.of())");
  }

  @Override
  public void traverse(ASTConcatStreamExpression node) {
    SymTypeExpression type = TypeCheck3.typeOf(node);
    String streamClassName = SymTypeExpression2JavaConverter.getJavaTypeQName(type);

    getPrinter().print(streamClassName + ".concat(");

    node.getLeft().accept(traverser);
    getPrinter().print(", ");
    node.getRight().accept(traverser);

    getPrinter().print(")");
  }

  @Override
  public void traverse(ASTLengthStreamExpression node) {
    node.getExpression().accept(traverser);
    getPrinter().print(".len()");
  }
}
