/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Memory node storing the tuples of a {@link TupleSource} grouped by the key
 * selected by a {@link TupleMask}. Indexers are the inputs of
 * {@link JoinNode}s and {@link ExistenceNode}s and can be shared by several
 * of them.
 * <p>
 * Listeners are notified after the memory has been updated.
 */
public class Indexer extends ReteNode implements Receiver {

  protected final TupleSource source;

  protected final TupleMask mask;

  protected final Map<Tuple, Multiset<Tuple>> memory = new HashMap<>();

  protected final List<IndexerListener> listeners = new ArrayList<>();

  /**
   * Creates an indexer, initializes it from the current contents of
   * {@code source} and attaches it to {@code source}.
   *
   * @param source the indexed node
   * @param mask   selects the key columns of the tuples of {@code source}
   * @throws IllegalArgumentException if the mask does not fit the arity of {@code source}
   */
  public Indexer(ReteNetwork network, TupleSource source, TupleMask mask) {
    super(network);
    checkSameNetwork(source);
    if (mask.getSourceArity() != source.getArity()) {
      throw new IllegalArgumentException("Mask " + mask + " does not fit arity "
          + source.getArity() + " of " + source);
    }
    this.source = source;
    this.mask = mask;
    for (Multiset.Entry<Tuple> entry : pull(source).entrySet()) {
      Tuple tuple = entry.getElement();
      memory.computeIfAbsent(mask.transform(tuple), k -> HashMultiset.create())
          .add(tuple, entry.getCount());
    }
    source.attachChild(this);
  }

  public TupleSource getSource() {
    return source;
  }

  public TupleMask getMask() {
    return mask;
  }

  /**
   * @return the types of the key columns
   */
  public List<Class<?>> getKeyTypes() {
    return mask.transform(source.getColumnTypes());
  }

  /**
   * Updates the memory and afterwards notifies all listeners synchronously.
   * Listeners must not cause further memory updates directly; their output is
   * deferred via the network's propagation queue.
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    Tuple key = mask.transform(tuple);
    if (direction == Direction.INSERT) {
      memory.computeIfAbsent(key, k -> HashMultiset.create()).add(tuple);
    } else {
      Multiset<Tuple> tuples = memory.get(key);
      if (tuples == null || !tuples.remove(tuple)) {
        throw inconsistency(this, tuple);
      }
      if (tuples.isEmpty()) {
        memory.remove(key);
      }
    }
    for (IndexerListener listener : listeners) {
      listener.indexerUpdated(this, direction, tuple, key);
    }
  }

  /**
   * @return the tuples with the given key (unmodifiable, possibly empty)
   */
  public Multiset<Tuple> get(Tuple key) {
    Multiset<Tuple> tuples = memory.get(key);
    return tuples == null ? HashMultiset.create() : Multisets.unmodifiableMultiset(tuples);
  }

  /**
   * @return the internal multiset of tuples with the given key, or {@code null}
   */
  Multiset<Tuple> lookup(Tuple key) {
    return memory.get(key);
  }

  /**
   * @return the number of tuples (with multiplicities) with the given key
   */
  public int count(Tuple key) {
    Multiset<Tuple> tuples = memory.get(key);
    return tuples == null ? 0 : tuples.size();
  }

  /**
   * @return all keys with at least one tuple (unmodifiable view)
   */
  public Set<Tuple> getKeys() {
    return Collections.unmodifiableSet(memory.keySet());
  }

  /**
   * Registers a listener notified after each update of the memory.
   *
   * @throws IllegalStateException if the network is propagating a change or
   *                               this indexer is disposed
   */
  public void addListener(IndexerListener listener) {
    network.checkStructuralChangeAllowed();
    if (disposed) {
      throw new IllegalStateException(this + " is disposed");
    }
    listeners.add(listener);
  }

  /**
   * Unregisters a listener.
   *
   * @throws IllegalStateException if the network is propagating a change
   */
  public void removeListener(IndexerListener listener) {
    network.checkStructuralChangeAllowed();
    listeners.remove(listener);
  }

  public List<IndexerListener> getListeners() {
    return Collections.unmodifiableList(listeners);
  }

  /**
   * An indexer can be disposed if no listener (join) uses it anymore.
   */
  @Override
  protected void checkDisposable() {
    if (!listeners.isEmpty()) {
      throw new IllegalStateException(this + " still has listeners " + listeners);
    }
  }

  @Override
  protected void detachFromParents() {
    source.detachChild(this);
  }

  @Override
  public String toString() {
    return "Indexer" + mask + " of " + source;
  }
}
