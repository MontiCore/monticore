/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base class of the generated {@code <Grammar>FullReteFactEmitter}s, which
 * report the facts of whole AST subtrees of a language to a
 * {@link ReteNetwork}. The generated subclass registers the
 * {@link ReteFactEmitter}s of the language and all its super languages in an
 * inheritance traverser of the language.
 * <p>
 * The facts of a subtree are the AST nodes it contains and the values of all
 * their features. The fact linking the subtree's root to its parent belongs to
 * the parent; it is reported by the variants of {@link #addSubtree} and
 * {@link #removeSubtree} taking the parent and the feature.
 * <p>
 * The feature values are reported by the emitters of the languages, the nodes
 * by this class: the inheritance traverser visits each node once as
 * {@link ASTNode} (via {@link IVisitor}), but possibly several times as a
 * concrete AST class if a sub language overrides a production.
 */
public abstract class FullReteFactEmitter {

  protected final ReteNetwork network;

  protected final ITraverser traverser;

  protected final List<ReteFactEmitter> emitters = new ArrayList<>();

  /**
   * Whether facts are reported as inserted (true) or deleted (false)
   */
  protected boolean insert = true;

  /**
   * @param traverser an inheritance traverser of the language, to which the
   *                  subclass adds the emitters
   */
  protected FullReteFactEmitter(ReteNetwork network, ITraverser traverser) {
    this.network = Objects.requireNonNull(network);
    this.traverser = Objects.requireNonNull(traverser);
    traverser.add4IVisitor(new IVisitor() {
      @Override
      public void visit(ASTNode node) {
        FullReteFactEmitter.this.node(node);
      }
    });
  }

  public ReteNetwork getNetwork() {
    return network;
  }

  public ITraverser getTraverser() {
    return traverser;
  }

  public List<ReteFactEmitter> getEmitters() {
    return Collections.unmodifiableList(emitters);
  }

  /**
   * Registers an emitter, so that its direction is set before each traversal.
   * The emitter has to be added to the traverser by the subclass as well.
   */
  protected void addEmitter(ReteFactEmitter emitter) {
    emitters.add(emitter);
  }

  /**
   * Reports all facts of the subtree as inserted, within one transaction. The
   * fact linking the root to its parent is not reported.
   *
   * @throws IllegalStateException if an AST node of the subtree has already
   *                               been reported
   */
  public void addSubtree(ASTNode root) {
    emit(root, true);
  }

  /**
   * Reports all facts of the subtree as deleted, within one transaction. The
   * subtree must be unchanged since its facts were reported (or all changes
   * must have been reported). The fact linking the root to its parent is not
   * reported.
   *
   * @throws IllegalStateException if a fact of the subtree is not known to the network
   */
  public void removeSubtree(ASTNode root) {
    emit(root, false);
  }

  /**
   * Reports all facts of the subtree and the fact linking it to its parent
   * (e.g. a transition added to an automaton) as inserted, within one
   * transaction.
   *
   * @param feature the feature of the parent containing the root
   */
  public <P extends ASTNode, C extends ASTNode> void addSubtree(C root, P parent,
      Feature<P, ? super C> feature) {
    network.transaction(() -> {
      emit(root, true);
      network.featureValueAdded(feature, parent, root);
    });
  }

  /**
   * Reports all facts of the subtree and the fact linking it to its parent as
   * deleted, within one transaction.
   *
   * @param feature the feature of the parent containing the root
   */
  public <P extends ASTNode, C extends ASTNode> void removeSubtree(C root, P parent,
      Feature<P, ? super C> feature) {
    network.transaction(() -> {
      emit(root, false);
      network.featureValueRemoved(feature, parent, root);
    });
  }

  /**
   * Traverses the subtree, reporting the facts in the given direction.
   */
  protected void emit(ASTNode root, boolean insert) {
    network.transaction(() -> {
      this.insert = insert;
      emitters.forEach(emitter -> emitter.setInsert(insert));
      root.accept(traverser);
    });
  }

  /**
   * Reports an AST node as added or removed.
   */
  protected void node(ASTNode node) {
    if (insert) {
      network.nodeAdded(node);
    } else {
      network.nodeRemoved(node);
    }
  }
}
