/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import de.monticore.ast.ASTNode;

import java.util.Collection;
import java.util.Objects;

/**
 * Base class of the generated fact emitters of a language
 * ({@code <Grammar>ReteFactEmitter}). A fact emitter is a visitor that reports
 * the facts of each visited AST node to a {@link ReteNetwork}: the node itself
 * and the values of the features declared by the visited type.
 * <p>
 * Each generated emitter only handles the types of its own language. Use it
 * with an inheritance traverser, which visits a node once for its concrete
 * class and once for each super type, also for super types of super
 * languages. The generated {@code <Grammar>FullReteFactEmitter} combines the
 * emitters of a language and all its super languages in such a traverser.
 * <p>
 * Whether facts are reported as inserted or deleted is controlled by
 * {@link #setInsert(boolean)}.
 */
public abstract class ReteFactEmitter {

  protected final ReteNetwork network;

  /**
   * Whether facts are reported as inserted (true) or deleted (false)
   */
  protected boolean insert = true;

  protected ReteFactEmitter(ReteNetwork network) {
    this.network = Objects.requireNonNull(network);
  }

  public ReteNetwork getNetwork() {
    return network;
  }

  public boolean isInsert() {
    return insert;
  }

  /**
   * @param insert whether facts are reported as inserted (true) or deleted (false)
   */
  public void setInsert(boolean insert) {
    this.insert = insert;
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

  /**
   * Reports a value of a feature as added or removed.
   */
  protected <O extends ASTNode, V> void fact(Feature<O, V> feature, O owner, V value) {
    if (insert) {
      network.featureValueAdded(feature, owner, value);
    } else {
      network.featureValueRemoved(feature, owner, value);
    }
  }

  /**
   * Reports all values of a list-valued feature as added or removed.
   */
  protected <O extends ASTNode, V> void facts(Feature<O, V> feature, O owner,
      Collection<? extends V> values) {
    for (V value : values) {
      fact(feature, owner, value);
    }
  }
}
