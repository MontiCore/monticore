/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;

import java.util.ArrayList;
import java.util.List;

/**
 * Natural join of two {@link Indexer}s: for each pair of a left tuple
 * {@code l} and a right tuple {@code r} with equal keys, the node contains
 * {@code l ++ rightOutputMask(r)}.
 * <p>
 * By default, the right output mask is the complement of the right key mask,
 * i.e. the key columns of the right tuple are omitted as they are equal to
 * the left key columns.
 * <p>
 * Keys containing {@link Tuple#ABSENT} join with nothing (see
 * {@link Indexer#isJoinable}), so the key columns of the output never contain
 * {@code ABSENT} and are not optional.
 * <p>
 * Both indexers may be the same object (self join on the same key).
 */
public class JoinNode extends AbstractSourceNode implements IndexerListener {

  protected final Indexer left;

  protected final Indexer right;

  protected final TupleMask rightOutputMask;

  /**
   * Creates a join omitting the key columns of the right side in the output.
   *
   * @see #JoinNode(ReteNetwork, Indexer, Indexer, TupleMask)
   */
  public JoinNode(ReteNetwork network, Indexer left, Indexer right) {
    this(network, left, right, right.getMask().complement());
  }

  /**
   * Creates a join and registers it as listener of both indexers. A join
   * keeps no memory of its own, so no initialization is required.
   *
   * @param left            indexer of the left input
   * @param right           indexer of the right input (may be {@code left})
   * @param rightOutputMask selects the columns of right tuples appended to
   *                        the left tuples in the output
   * @throws IllegalArgumentException if the keys differ in size, the key
   *                                  columns can never match or the output
   *                                  mask does not fit the right arity
   */
  public JoinNode(ReteNetwork network, Indexer left, Indexer right, TupleMask rightOutputMask) {
    super(network, computeColumnTypes(left, right, rightOutputMask),
        computeColumnOptionality(left, right, rightOutputMask));
    checkSameNetwork(left);
    checkSameNetwork(right);
    this.left = left;
    this.right = right;
    this.rightOutputMask = rightOutputMask;
    left.addListener(this);
    if (right != left) {
      right.addListener(this);
    }
  }

  /**
   * Validates the join configuration and computes the output column types.
   * As joined key values are instances of both key column types, the key
   * columns are narrowed to the more specific type.
   *
   * @return the left column types followed by the right column types
   * selected by {@code rightOutputMask}
   * @throws IllegalArgumentException if the configuration is invalid
   */
  protected static List<Class<?>> computeColumnTypes(Indexer left, Indexer right,
      TupleMask rightOutputMask) {
    TupleMask leftKey = left.getMask();
    TupleMask rightKey = right.getMask();
    if (leftKey.getSize() != rightKey.getSize()) {
      throw new IllegalArgumentException("Join keys of different size: " + leftKey + " and "
          + rightKey);
    }
    if (rightOutputMask.getSourceArity() != right.getSource().getArity()) {
      throw new IllegalArgumentException("Right output mask " + rightOutputMask
          + " does not fit arity of " + right.getSource());
    }
    List<Class<?>> leftTypes = new ArrayList<>(left.getSource().getColumnTypes());
    List<Class<?>> rightTypes = new ArrayList<>(right.getSource().getColumnTypes());
    for (int i = 0; i < leftKey.getSize(); i++) {
      int l = leftKey.getIndex(i);
      int r = rightKey.getIndex(i);
      Class<?> lt = leftTypes.get(l);
      Class<?> rt = rightTypes.get(r);
      if (!ColumnTypes.mayOverlap(lt, rt)) {
        throw new IllegalArgumentException("Join key column " + i + " can never match: "
            + lt.getName() + " vs. " + rt.getName());
      }
      // values of joined columns are instances of both types
      leftTypes.set(l, ColumnTypes.narrower(lt, rt));
      rightTypes.set(r, ColumnTypes.narrower(rt, lt));
    }
    List<Class<?>> result = new ArrayList<>(leftTypes);
    result.addAll(rightOutputMask.transform(rightTypes));
    return result;
  }

  /**
   * The output columns are optional if the corresponding left column or
   * selected right column is optional, except for the key columns: as keys
   * containing {@link Tuple#ABSENT} join with nothing, joined key values are
   * never {@code ABSENT}. Must be called after {@link #computeColumnTypes},
   * which validates the configuration.
   *
   * @return the optionality of the left columns followed by the optionality
   * of the right columns selected by {@code rightOutputMask}
   */
  protected static List<Boolean> computeColumnOptionality(Indexer left, Indexer right,
      TupleMask rightOutputMask) {
    List<Boolean> leftOptionality = new ArrayList<>(left.getSource().getColumnOptionality());
    List<Boolean> rightOptionality = new ArrayList<>(right.getSource().getColumnOptionality());
    for (int i = 0; i < left.getMask().getSize(); i++) {
      leftOptionality.set(left.getMask().getIndex(i), false);
      rightOptionality.set(right.getMask().getIndex(i), false);
    }
    List<Boolean> result = new ArrayList<>(leftOptionality);
    result.addAll(rightOutputMask.transform(rightOptionality));
    return result;
  }

  public Indexer getLeft() {
    return left;
  }

  public Indexer getRight() {
    return right;
  }

  public TupleMask getRightOutputMask() {
    return rightOutputMask;
  }

  /**
   * Joins the changed tuple with the tuples of the opposite indexer having
   * the same key and propagates the resulting pairs in the same direction.
   * For a self join (both indexers identical), both sides are processed.
   * Tuples whose key is not {@linkplain Indexer#isJoinable joinable} have no
   * partners and produce no output.
   */
  @Override
  public void indexerUpdated(Indexer indexer, Direction direction, Tuple tuple, Tuple key) {
    if (indexer == left) {
      Multiset<Tuple> rightTuples = right.lookupPartners(key);
      if (rightTuples != null) {
        for (Multiset.Entry<Tuple> entry : rightTuples.entrySet()) {
          propagate(direction, tuple.concat(entry.getElement(), rightOutputMask),
              entry.getCount());
        }
      }
    }
    if (indexer == right) {
      Multiset<Tuple> leftTuples = left.lookupPartners(key);
      if (left == right) {
        // a tuple with ABSENT in its key does not even join with itself
        if (Indexer.isJoinable(key)) {
          selfJoinRightUpdate(direction, tuple, leftTuples);
        }
      } else if (leftTuples != null) {
        for (Multiset.Entry<Tuple> entry : leftTuples.entrySet()) {
          propagate(direction, entry.getElement().concat(tuple, rightOutputMask),
              entry.getCount());
        }
      }
    }
  }

  /**
   * The left side of a self join has already been processed against the
   * updated memory. To count each new pair exactly once, the right side has
   * to be processed against the memory as it was before the change: for an
   * insertion, one occurrence of {@code tuple} is ignored; for a deletion,
   * one occurrence of {@code tuple} is added.
   *
   * @param direction  direction of the change
   * @param tuple      the inserted or deleted tuple
   * @param leftTuples the (already updated) tuples with the key of
   *                   {@code tuple}, or {@code null} if there are none
   */
  protected void selfJoinRightUpdate(Direction direction, Tuple tuple, Multiset<Tuple> leftTuples) {
    int correction = direction == Direction.INSERT ? -1 : 1;
    boolean seen = false;
    if (leftTuples != null) {
      for (Multiset.Entry<Tuple> entry : leftTuples.entrySet()) {
        int count = entry.getCount();
        if (entry.getElement().equals(tuple)) {
          count += correction;
          seen = true;
        }
        propagate(direction, entry.getElement().concat(tuple, rightOutputMask), count);
      }
    }
    if (!seen && direction == Direction.DELETE) {
      propagate(direction, tuple.concat(tuple, rightOutputMask), 1);
    }
  }

  /**
   * Computes the join of the current indexer contents. The multiplicity of a
   * result is the product of the multiplicities of the joined tuples; keys
   * that are not {@linkplain Indexer#isJoinable joinable} are skipped.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Tuple key : left.getKeys()) {
      Multiset<Tuple> rightTuples = right.lookupPartners(key);
      if (rightTuples == null) {
        continue;
      }
      for (Multiset.Entry<Tuple> l : left.lookup(key).entrySet()) {
        for (Multiset.Entry<Tuple> r : rightTuples.entrySet()) {
          collector.add(l.getElement().concat(r.getElement(), rightOutputMask),
              l.getCount() * r.getCount());
        }
      }
    }
  }

  /**
   * Unregisters this join from both indexers.
   */
  @Override
  protected void detachFromParents() {
    left.removeListener(this);
    right.removeListener(this);
  }
}
