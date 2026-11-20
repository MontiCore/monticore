/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.Objects;

/**
 * Common base class of all nodes of a {@link ReteNetwork}.
 * <p>
 * Creating a node attaches it to its parents. Stateful nodes initialize
 * themselves from the current contents of their parents, so nodes can be
 * added to a network that already received facts.
 */
public abstract class ReteNode {

  protected final ReteNetwork network;

  protected boolean disposed;

  protected ReteNode(ReteNetwork network) {
    this.network = Objects.requireNonNull(network);
    network.checkStructuralChangeAllowed();
  }

  public ReteNetwork getNetwork() {
    return network;
  }

  public boolean isDisposed() {
    return disposed;
  }

  /**
   * Detaches this node from its parents. Only nodes without children
   * (or listeners) can be disposed.
   *
   * @throws IllegalStateException if this node still has children
   */
  public void dispose() {
    network.checkStructuralChangeAllowed();
    if (!disposed) {
      checkDisposable();
      disposed = true;
      detachFromParents();
    }
  }

  /**
   * Checks whether this node may be disposed, i.e. whether no other node
   * depends on it anymore.
   *
   * @throws IllegalStateException if this node still has children or listeners
   */
  protected abstract void checkDisposable();

  /**
   * Removes this node from all nodes it receives deltas from, so that it is
   * no longer reachable during propagation.
   */
  protected abstract void detachFromParents();

  /**
   * @throws IllegalArgumentException if {@code source} belongs to another network
   */
  protected void checkSameNetwork(TupleSource source) {
    if (source.getNetwork() != network) {
      throw new IllegalArgumentException(source + " belongs to a different network");
    }
  }

  /**
   * @throws IllegalArgumentException if {@code indexer} belongs to another network
   */
  protected void checkSameNetwork(Indexer indexer) {
    if (indexer.getNetwork() != network) {
      throw new IllegalArgumentException(indexer + " belongs to a different network");
    }
  }

  /**
   * @return a new multiset containing the current contents of {@code source}
   */
  protected static Multiset<Tuple> pull(TupleSource source) {
    Multiset<Tuple> contents = HashMultiset.create();
    source.pullInto(contents);
    return contents;
  }

  /**
   * Creates the exception thrown when a node receives the deletion of a tuple
   * it does not contain. This indicates that a filter or eval function
   * behaved non-deterministically, as deltas cannot cancel out otherwise.
   *
   * @param node  the node detecting the inconsistency
   * @param tuple the deleted tuple
   * @return the exception to throw
   */
  protected static IllegalStateException inconsistency(ReteNode node, Tuple tuple) {
    return new IllegalStateException("Inconsistent Rete network: " + node
        + " received deletion of unknown tuple " + tuple
        + ". Are all filter/eval functions deterministic and only depending on tuple values?");
  }
}
