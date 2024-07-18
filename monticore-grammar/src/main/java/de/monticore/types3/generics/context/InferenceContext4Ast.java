// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.context;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores context information for expressions and type identifiers,
 * which are used (in some instances) to calculate the corresponding SymTypes.
 * <p>
 * The Type System uses contextual typing,
 * i.e., the type of an expression can be dependent on the context, e.g.:
 * {@code List<A> as = new ArrayList<>();}
 * {@code List<B> bs = new ArrayList<>();}
 */
public class InferenceContext4Ast {

  protected static final String LOG_NAME = "TypeContext4Ast";

  /**
   * the actual map from context to types,
   * we use ASTNode to support non-ASTExpression Nodes,
   * however, we do NOT support non-expression ASTNodes,
   * e.g., in MyClass.myMethod(),
   * the "MyClass" is not an expression by itself.
   */
  protected Map<ASTNode, InferenceContext> expr2ctx;

  protected Map<ASTNode, InferenceContext> getExpression2Context() {
    return expr2ctx;
  }

  public InferenceContext4Ast() {
    reset();
  }

  public void reset() {
    expr2ctx = new HashMap<>();
  }

  /**
   * This removes the stored values of
   * every node below and including the provided root.
   * This can be required to,
   * e.g., rerun the type checker multiple times during type inference.
   */
  public void reset(ASTNode rootNode) {
    IVisitor mapReseter = new IVisitor() {
      @Override
      public void visit(ASTNode node) {
        getExpression2Context().remove(node);
      }
    };
    ExpressionsBasisTraverser traverser = ExpressionsBasisMill.inheritanceTraverser();
    traverser.add4IVisitor(mapReseter);
    rootNode.accept(traverser);
  }

  public InferenceContext getContextOfExpression(ASTExpression node) {
    if (!getExpression2Context().containsKey(node)) {
      Log.trace("inference context requested but not set for expression "
              + node2InfoString(node)
          , LOG_NAME);
      getExpression2Context().put(node, new InferenceContext());
    }
    return getExpression2Context().get(node);
  }

  /**
   * sets the type information of the expression,
   * information may be partial
   */
  public void setContextOfExpression(
      ASTExpression astExpr,
      InferenceContext context
  ) {
    if (getExpression2Context().containsKey(astExpr)) {
      Log.trace(node2InfoString(astExpr)
              + ": overriding context information",
          LOG_NAME
      );
    }
    else {
      Log.trace(node2InfoString(astExpr)
              + ": setting new context information",
          LOG_NAME
      );
    }
    getExpression2Context().put(astExpr, context);
  }

  /**
   * QOL method: sets the target type for a given expression
   */
  public void setTargetTypeOfExpression(
      ASTExpression astExpr,
      SymTypeExpression targetType
  ) {
    InferenceContext ctx = getContextOfExpression(astExpr);
    ctx.setTargetType(targetType);
    setContextOfExpression(astExpr, ctx);
  }

  // Helper

  /**
   * helps with logging.
   */
  protected String node2InfoString(ASTNode node) {
    // may be moved from here, if required somewhere else as well
    // as multiple expressions can start at the same position,
    // we need to know the end position as well
    // even better would be access to the model source the positions refer to...
    // based on SourcePosition::toString
    StringBuilder result = new StringBuilder();
    if (node.isPresent_SourcePositionStart() &&
        node.isPresent_SourcePositionEnd()) {
      SourcePosition startPos = node.get_SourcePositionStart();
      SourcePosition endPos = node.get_SourcePositionEnd();
      if (startPos.getFileName().isPresent()) {
        result.append(FilenameUtils.getName(startPos.getFileName().get()));
        result.append(":");
      }
      result.append("<" + startPos.getLine() + "," + startPos.getColumn() + ">");
      result.append("-");
      result.append("<" + endPos.getLine() + "," + endPos.getColumn() + ">");
    }
    else {
      result.append("unknown position");
    }
    return result.toString();
  }
}
