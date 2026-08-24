// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.ast.util.ParentNodeTracker;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * Handles the replacement of nodes after the typecheck did run over the AST.
 * Mostly used to replace
 * {@link de.monticore.expressions.expressionsbasis._ast.ASTNameExpression} and
 * {@link de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression}.
 * <p>
 * The TypeCheck will use {@link #addReplacement(ASTNode, ASTNode)},
 * after the CoCos did run that use the TypeCheck,
 * call {@link #replace(Supplier, ASTNode)}.
 */
public class PostTypeCheckNodeReplacer {

  protected static final String LOG_NAME =
      PostTypeCheckNodeReplacer.class.getSimpleName();

  protected static PostTypeCheckNodeReplacer delegate;

  /**
   * Stores the replacements that need to be done
   * for, e.g., ASTQualifiedNameExpressions,
   * if they happen to be no qualified names
   * but have been parsed as such.
   */
  protected Map<ASTNode, ASTNode> replacements;

  protected PostTypeCheckNodeReplacer() {
    replacements = new WeakHashMap<>();
  }

  // methods

  /**
   * This removes the stored values of
   * every node below and including the provided root.
   * This can be required to,
   * e.g., rerun the type checker multiple times during type inference.
   */
  public static void reset(ASTNode rootNode) {
    getDelegate()._reset(rootNode);
  }

  protected void _reset(ASTNode rootNode) {
    IVisitor mapReseter = new IVisitor() {
      @Override
      public void visit(ASTNode node) {
        replacements.remove(node);
      }
    };
    ExpressionsBasisTraverser traverser =
        ExpressionsBasisMill.inheritanceTraverser();
    traverser.add4IVisitor(mapReseter);
    rootNode.accept(traverser);
  }

  /**
   * Stores a replacement to be applied by {@link #replace(Supplier, ASTNode)}.
   *
   * @param replacee    node that is in the model and is to be replaced.
   * @param replacement node that is to be in the model after replacing.
   */
  public static void addReplacement(ASTNode replacee, ASTNode replacement) {
    getDelegate()._addReplacement(replacee, replacement);
  }

  protected void _addReplacement(ASTNode replacee, ASTNode replacement) {
    Preconditions.checkNotNull(replacee);
    Preconditions.checkNotNull(replacement);
    if (replacements.containsValue(replacee)) {
      Log.trace("Potential Replacement Overriden: "
              + replacee.get_SourcePositionStart() + "-"
              + replacement.get_SourcePositionEnd(),
          LOG_NAME
      );
    }
    replacements.put(replacee, replacement);
  }

  /**
   * Replaces the nodes according to the stored replacements.
   * <p>
   * Can be called multiple times without issues.
   *
   * @param inheritanceTraverserSupplier Supplies the inheritance-traverser
   *                                     of the current language.
   * @param rootNode                     The root node of the current model.
   */
  public static void replace(
      Supplier<ITraverser> inheritanceTraverserSupplier,
      ASTNode rootNode
  ) {
    getDelegate()._replace(inheritanceTraverserSupplier, rootNode);
  }

  protected void _replace(
      Supplier<ITraverser> inheritanceTraverserSupplier,
      ASTNode rootNode
  ) {
    ITraverser inheritanceTraverser = inheritanceTraverserSupplier.get();
    ParentNodeTracker parentNodeTracker = new ParentNodeTracker();
    NodeReplacer nodeReplacer = new NodeReplacer(parentNodeTracker);
    inheritanceTraverser.add4IVisitor(nodeReplacer);
    inheritanceTraverser.add4IVisitor(parentNodeTracker);
    rootNode.accept(inheritanceTraverser);
  }

  /**
   * The Visitor that actually handles the replacement of nodes.
   */
  protected class NodeReplacer implements IVisitor {

    protected ParentNodeTracker parentNodeTracker;

    protected Map<ASTNode, Set<Map.Entry<ASTNode, ASTNode>>> parent2Replacement;

    public NodeReplacer(ParentNodeTracker parentNodeTracker) {
      this.parentNodeTracker = parentNodeTracker;
      parent2Replacement = new HashMap<>();
    }

    @Override
    public void visit(ASTNode node) {
      if (replacements.containsKey(node)) {
        Optional<ASTNode> parentNode =
            parentNodeTracker.getCurrentParentNode();
        if (parentNode.isPresent()) {
          parent2Replacement
              .computeIfAbsent(parentNode.get(), n -> new LinkedHashSet<>())
              .add(Map.entry(node, replacements.get(node)));
        }
        else {
          Log.warn("0xFD200 could not replace"
                  + " the (presumably top-most) node"
                  + ", as only inner nodes can be replaced.",
              node.get_SourcePositionStart(),
              node.get_SourcePositionEnd()
          );
        }
      }
    }

    @Override
    public void endVisit(ASTNode node) {
      if (parent2Replacement.containsKey(node)) {
        Collection<Map.Entry<ASTNode, ASTNode>> currentReplacements =
            parent2Replacement.get(node);
        for (Map.Entry<ASTNode, ASTNode> replacement : currentReplacements) {
          node.replaceChild(replacement.getKey(), replacement.getValue());
          Log.trace("replaced node "
                  + node.get_SourcePositionStart().toString() + "-"
                  + node.get_SourcePositionEnd().toString(),
              LOG_NAME
          );
        }
      }
    }

  }

  // static delegate

  public static void init() {
    Log.trace("init default PostTypeCheckNodeReplacer", "TypeCheck setup");
    setDelegate(new PostTypeCheckNodeReplacer());
  }

  public static void reset() {
    PostTypeCheckNodeReplacer.delegate = null;
  }

  protected static void setDelegate(PostTypeCheckNodeReplacer newDelegate) {
    PostTypeCheckNodeReplacer.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static PostTypeCheckNodeReplacer getDelegate() {
    if (PostTypeCheckNodeReplacer.delegate == null) {
      init();
    }
    return PostTypeCheckNodeReplacer.delegate;
  }

}
