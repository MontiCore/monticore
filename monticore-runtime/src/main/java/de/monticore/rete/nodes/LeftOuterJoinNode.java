/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Left outer join of two {@link Indexer}s, implementing optional pattern
 * elements with the semantics of SPARQL's {@code OPTIONAL}. A right tuple
 * {@code r} is a <em>partner</em> of a left tuple {@code l} if both have equal
 * {@linkplain Indexer#isJoinable joinable} keys and the condition holds for
 * {@code l ++ rightOutputMask(r)}. The node contains
 * <ul>
 *   <li>{@code l ++ rightOutputMask(r)} for each partner {@code r} of
 *   {@code l}, and</li>
 *   <li>{@code l ++ [ABSENT, ..., ABSENT]} for each left tuple {@code l}
 *   without partner,</li>
 * </ul>
 * so each left tuple is kept. The multiplicities are those of an inner join
 * resp. of the left tuple.
 * <p>
 * The condition is part of the join, as conditions spanning left and right
 * columns (e.g. injectivity) have to be checked before deciding that a left
 * tuple has no partner: filtering the output instead would drop left tuples
 * whose candidates all violate the condition, instead of padding them.
 * <p>
 * The selected right columns are optional. Left tuples are passed on with
 * their original values, so in contrast to {@link JoinNode}, the left key
 * columns are neither narrowed nor made non-optional.
 * <p>
 * Both indexers must be different objects: a self outer join is rejected.
 * Indexers of the same source are allowed.
 */
public class LeftOuterJoinNode extends AbstractSourceNode implements IndexerListener {

  protected final Indexer left;

  protected final Indexer right;

  protected final TupleMask rightOutputMask;

  protected final Predicate<Tuple> condition;

  /**
   * The appended columns of left tuples without partner, all
   * {@link Tuple#ABSENT}.
   */
  protected final Tuple padding;

  /**
   * Selects all columns of {@link #padding}.
   */
  protected final TupleMask paddingMask;

  /**
   * For each distinct left tuple, the number of its partners (with
   * multiplicities).
   */
  protected final Map<Tuple, Integer> partnerCounts = new HashMap<>();

  /**
   * Creates a left outer join without condition, omitting the key columns of
   * the right side in the output.
   *
   * @see #LeftOuterJoinNode(ReteNetwork, Indexer, Indexer, TupleMask, Predicate)
   */
  public LeftOuterJoinNode(ReteNetwork network, Indexer left, Indexer right) {
    this(network, left, right, right.getMask().complement(), t -> true);
  }

  /**
   * Creates a left outer join, initializes its partner counts from the
   * current contents of the indexers and registers it as listener of both
   * indexers.
   *
   * @param left            indexer of the left input, whose tuples are all kept
   * @param right           indexer of the optional right input
   * @param rightOutputMask selects the columns of right tuples appended to
   *                        the left tuples in the output
   * @param condition       a deterministic predicate only depending on tuple
   *                        values, evaluated for {@code l ++ rightOutputMask(r)};
   *                        {@link Tuple#ABSENT} values already contained in
   *                        {@code l} or {@code r} are read as {@code null}
   * @throws IllegalArgumentException if both indexers are identical, the keys
   *                                  differ in size, the key columns can never
   *                                  match or the output mask does not fit the
   *                                  right arity
   */
  public LeftOuterJoinNode(ReteNetwork network, Indexer left, Indexer right,
      TupleMask rightOutputMask, Predicate<Tuple> condition) {
    super(network, computeColumnTypes(left, right, rightOutputMask),
        computeColumnOptionality(left, rightOutputMask));
    checkSameNetwork(left);
    checkSameNetwork(right);
    this.left = left;
    this.right = right;
    this.rightOutputMask = rightOutputMask;
    this.condition = condition;
    Object[] absent = new Object[rightOutputMask.getSize()];
    Arrays.fill(absent, Tuple.ABSENT);
    this.padding = Tuple.of(absent);
    this.paddingMask = TupleMask.identity(absent.length);
    for (Tuple key : left.getKeys()) {
      for (Tuple l : left.lookup(key).elementSet()) {
        partnerCounts.put(l, countPartners(l, key));
      }
    }
    left.addListener(this);
    right.addListener(this);
  }

  /**
   * Validates the configuration and computes the output column types. Only
   * the selected right key columns are narrowed to the more specific type, as
   * their values are either joined values or {@link Tuple#ABSENT}.
   *
   * @return the left column types followed by the right column types
   * selected by {@code rightOutputMask}
   * @throws IllegalArgumentException if the configuration is invalid
   */
  protected static List<Class<?>> computeColumnTypes(Indexer left, Indexer right,
      TupleMask rightOutputMask) {
    if (left == right) {
      throw new IllegalArgumentException("Left and right indexer of a left outer join "
          + "must be different");
    }
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
    List<Class<?>> leftTypes = left.getSource().getColumnTypes();
    List<Class<?>> rightTypes = new ArrayList<>(right.getSource().getColumnTypes());
    for (int i = 0; i < leftKey.getSize(); i++) {
      Class<?> lt = leftTypes.get(leftKey.getIndex(i));
      int r = rightKey.getIndex(i);
      Class<?> rt = rightTypes.get(r);
      if (!ColumnTypes.mayOverlap(lt, rt)) {
        throw new IllegalArgumentException("Join key column " + i + " can never match: "
            + lt.getName() + " vs. " + rt.getName());
      }
      rightTypes.set(r, ColumnTypes.narrower(rt, lt));
    }
    List<Class<?>> result = new ArrayList<>(leftTypes);
    result.addAll(rightOutputMask.transform(rightTypes));
    return result;
  }

  /**
   * @return the optionality of the left columns followed by optional columns
   * for the selected right columns
   */
  protected static List<Boolean> computeColumnOptionality(Indexer left,
      TupleMask rightOutputMask) {
    List<Boolean> result = new ArrayList<>(left.getSource().getColumnOptionality());
    for (int i = 0; i < rightOutputMask.getSize(); i++) {
      result.add(true);
    }
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

  public Predicate<Tuple> getCondition() {
    return condition;
  }

  /**
   * A change of a left tuple is passed on for each of its partners, or padded
   * if it has none. A change of a right tuple affects the left tuples with
   * the same key for which the condition holds: their output changes by one
   * pair, and if it is their first partner (insertion) or their last one
   * (deletion), their padded tuple is deleted resp. inserted.
   */
  @Override
  public void indexerUpdated(Indexer indexer, Direction direction, Tuple tuple, Tuple key) {
    if (indexer == left) {
      leftUpdated(direction, tuple, key);
    } else {
      rightUpdated(direction, tuple, key);
    }
  }

  /**
   * @param tuple the inserted or deleted left tuple; the right side is
   *              unchanged, so its partners are computed from the current
   *              contents of the right indexer
   */
  protected void leftUpdated(Direction direction, Tuple tuple, Tuple key) {
    int partners = 0;
    Multiset<Tuple> rightTuples = right.lookupPartners(key);
    if (rightTuples != null) {
      for (Multiset.Entry<Tuple> entry : rightTuples.entrySet()) {
        Tuple joined = tuple.concat(entry.getElement(), rightOutputMask);
        if (condition.test(joined)) {
          propagate(direction, joined, entry.getCount());
          partners += entry.getCount();
        }
      }
    }
    if (partners == 0) {
      propagate(direction, pad(tuple));
    }
    // the left indexer is already updated
    Multiset<Tuple> remaining = left.lookup(key);
    if (remaining == null || !remaining.contains(tuple)) {
      partnerCounts.remove(tuple);
    } else {
      partnerCounts.put(tuple, partners);
    }
  }

  /**
   * @return the left tuple followed by {@link Tuple#ABSENT} for each selected
   * right column
   */
  protected Tuple pad(Tuple leftTuple) {
    return leftTuple.concat(padding, paddingMask);
  }

  /**
   * @param tuple the inserted or deleted right tuple
   */
  protected void rightUpdated(Direction direction, Tuple tuple, Tuple key) {
    Multiset<Tuple> leftTuples = left.lookupPartners(key);
    if (leftTuples == null) {
      return;
    }
    for (Multiset.Entry<Tuple> entry : leftTuples.entrySet()) {
      Tuple l = entry.getElement();
      Tuple joined = l.concat(tuple, rightOutputMask);
      if (!condition.test(joined)) {
        continue;
      }
      int count = entry.getCount();
      int before = partnerCounts.get(l);
      if (direction == Direction.INSERT) {
        if (before == 0) {
          propagate(Direction.DELETE, pad(l), count);
        }
        propagate(Direction.INSERT, joined, count);
        partnerCounts.put(l, before + 1);
      } else {
        propagate(Direction.DELETE, joined, count);
        partnerCounts.put(l, before - 1);
        if (before == 1) {
          propagate(Direction.INSERT, pad(l), count);
        }
      }
    }
  }

  /**
   * @return the number of partners (with multiplicities) of the left tuple
   * in the current contents of the right indexer
   */
  protected int countPartners(Tuple leftTuple, Tuple key) {
    int partners = 0;
    Multiset<Tuple> rightTuples = right.lookupPartners(key);
    if (rightTuples != null) {
      for (Multiset.Entry<Tuple> entry : rightTuples.entrySet()) {
        if (condition.test(leftTuple.concat(entry.getElement(), rightOutputMask))) {
          partners += entry.getCount();
        }
      }
    }
    return partners;
  }

  /**
   * Computes the left outer join of the current indexer contents, independent
   * of the partner counts.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Tuple key : left.getKeys()) {
      Multiset<Tuple> rightTuples = right.lookupPartners(key);
      for (Multiset.Entry<Tuple> l : left.lookup(key).entrySet()) {
        boolean hasPartner = false;
        if (rightTuples != null) {
          for (Multiset.Entry<Tuple> r : rightTuples.entrySet()) {
            Tuple joined = l.getElement().concat(r.getElement(), rightOutputMask);
            if (condition.test(joined)) {
              collector.add(joined, l.getCount() * r.getCount());
              hasPartner = true;
            }
          }
        }
        if (!hasPartner) {
          collector.add(pad(l.getElement()), l.getCount());
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
