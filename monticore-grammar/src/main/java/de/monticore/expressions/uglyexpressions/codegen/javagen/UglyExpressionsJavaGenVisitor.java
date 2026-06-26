// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.uglyexpressions.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTArrayCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTArrayDimensionByExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTClassCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTCreatorExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTInstanceofExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.generatesToJavaRuntimeIdentifiableType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypeQName;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getTypeErasedJavaTypePrint;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.symTypeFromAST;
import static de.monticore.types3.TypeCheck3.typeOf;

public class UglyExpressionsJavaGenVisitor
    extends UglyExpressionsInheritanceHandler {

  protected JavaGenVisitorState state;

  public UglyExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTTypeCastExpression node) {
    SymTypeExpression targetType = normalize(symTypeFromAST(node.getMCType()));
    SymTypeExpression sourceType = normalize(typeOf(node.getExpression()));
    printConverted(getPrinter(),
        targetType,
        sourceType,
        (p) -> node.getExpression().accept(traverser)
    );
  }

  @Override
  public void traverse(ASTInstanceofExpression node) {
    SymTypeExpression targetType = normalize(symTypeFromAST(node.getMCType()));
    if (!generatesToJavaRuntimeIdentifiableType(targetType)) {
      Log.error(
          "0xFD713 " + targetType.printFullName()
              + " is not compatible with instanceof for Java generation"
              + " due to type erasure.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
    state.startParentheses();
    node.getExpression().accept(getTraverser());
    getPrinter().print(" instanceof ");
    // print it with type erasure anyway,
    // the behavior is simply not always correct
    // and the did log an error already
    getPrinter().print(getTypeErasedJavaTypePrint(targetType));
    state.endParentheses();
  }

  @Override
  public void traverse(ASTCreatorExpression node) {
    state.startParentheses();
    getPrinter().print("new ");
    node.getCreator().accept(getTraverser());
    state.endParentheses();
  }

  @Override
  public void traverse(ASTClassCreator node) {
    SymTypeExpression type = normalize(symTypeFromAST(node.getMCType()));
    getPrinter().print(getJavaTypeQName(type));
    state.startParentheses();
    for (int i = 0; i < node.getArguments().sizeExpressions(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      node.getArguments().getExpression(i).accept(getTraverser());
    }
    state.endParentheses();
  }

  @Override
  public void traverse(ASTArrayCreator node) {
    SymTypeExpression type = normalize(symTypeFromAST(node.getMCType()));
    getPrinter().print(getJavaTypeQName(type));
    node.getArrayDimensionSpecifier().accept(getTraverser());
  }

  @Override
  public void traverse(ASTArrayDimensionByExpression node) {
    for (ASTExpression expr : node.getExpressionList()) {
      getPrinter().print("[");
      expr.accept(getTraverser());
      getPrinter().print("]");
    }
    for (int i = 0; i < node.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
  }

}
