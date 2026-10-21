/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

/**
 * Semi join or anti join of two {@link Indexer}s. Contains the left tuples
 * for which a right tuple with an equal key exists (semi join) or does not
 * exist (anti join, {@code negative == true}). The latter implements
 * negative application conditions.
 */
public class ExistenceNode extends AbstractSourceNode implements IndexerListener {

  protected final Indexer left;

  protected final Indexer right;

  protected final boolean negative;

  /**
   * Creates the node and registers it as listener of both indexers. The
   * output columns are the columns of the left input.
   *
   * @param left     indexer of the tuples to filter
   * @param right    indexer of the tuples whose existence is checked
   * @param negative {@code true} for an anti join, {@code false} for a semi join
   * @throws IllegalArgumentException if both indexers are identical, the keys
   *                                  differ in size or can never match
   */
  public ExistenceNode(ReteNetwork network, Indexer left, Indexer right, boolean negative) {
    super(network, left.getSource().getColumnTypes());
    checkSameNetwork(left);
    checkSameNetwork(right);
    if (left == right) {
      throw new IllegalArgumentException("Left and right indexer of an existence node "
          + "must be different");
    }
    if (left.getMask().getSize() != right.getMask().getSize()) {
      throw new IllegalArgumentException("Keys of different size: " + left.getMask() + " and "
          + right.getMask());
    }
    for (int i = 0; i < left.getMask().getSize(); i++) {
      Class<?> lt = left.getKeyTypes().get(i);
      Class<?> rt = right.getKeyTypes().get(i);
      if (!ColumnTypes.mayOverlap(lt, rt)) {
        throw new IllegalArgumentException("Key column " + i + " can never match: "
            + lt.getName() + " vs. " + rt.getName());
      }
    }
    this.left = left;
    this.right = right;
    this.negative = negative;
    left.addListener(this);
    right.addListener(this);
  }

  public Indexer getLeft() {
    return left;
  }

  public Indexer getRight() {
    return right;
  }

  public boolean isNegative() {
    return negative;
  }

  /**
   * A change on the left side is passed on if the existence condition holds
   * for its key. A change on the right side only matters if it changes the
   * existence of its key (multiplicity 0 to 1 or 1 to 0); then all left
   * tuples with that key are inserted or deleted.
   */
  @Override
  public void indexerUpdated(Indexer indexer, Direction direction, Tuple tuple, Tuple key) {
    if (indexer == left) {
      if ((right.count(key) > 0) != negative) {
        propagate(direction, tuple);
      }
    } else {
      int count = right.count(key);
      boolean existenceChanged = direction == Direction.INSERT ? count == 1 : count == 0;
      Multiset<Tuple> leftTuples = left.lookup(key);
      if (existenceChanged && leftTuples != null) {
        Direction out = negative ? direction.opposite() : direction;
        for (Multiset.Entry<Tuple> entry : leftTuples.entrySet()) {
          propagate(out, entry.getElement(), entry.getCount());
        }
      }
    }
  }

  /**
   * Adds all left tuples whose key satisfies the existence condition.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Tuple key : left.getKeys()) {
      if ((right.count(key) > 0) != negative) {
        for (Multiset.Entry<Tuple> entry : left.lookup(key).entrySet()) {
          collector.add(entry.getElement(), entry.getCount());
        }
      }
    }
  }

  /**
   * Unregisters this node from both indexers.
   */
  @Override
  protected void detachFromParents() {
    left.removeListener(this);
    right.removeListener(this);
  }
}
