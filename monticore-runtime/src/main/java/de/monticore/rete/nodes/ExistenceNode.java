/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Semi join or anti join of two {@link Indexer}s. Contains the left tuples
 * for which a partner exists (semi join) or does not exist (anti join,
 * {@code negative == true}). The latter implements negative application
 * conditions.
 * <p>
 * A right tuple {@code r} is a <em>partner</em> of a left tuple {@code l} if
 * both have equal {@linkplain Indexer#isJoinable joinable} keys and the
 * optional condition holds for {@code l ++ r[without key]}, i.e. for the tuple
 * a {@link JoinNode} of both indexers would produce. Without condition, the
 * existence only depends on the key, so a right change only matters if it
 * changes the existence of its key.
 * <p>
 * The condition is part of the existence check, as conditions spanning left
 * and right columns (e.g. injectivity: the objects of a negative pattern must
 * differ from those of the positive one) have to be checked before deciding
 * that a left tuple has no partner: an anti join of a filtered join could not
 * express them.
 * <p>
 * Keys containing {@link Tuple#ABSENT} have no partners: left tuples with such
 * keys are never passed on by a semi join and always by an anti join, and
 * right tuples with such keys have no effect.
 */
public class ExistenceNode extends AbstractSourceNode implements IndexerListener {

  protected final Indexer left;

  protected final Indexer right;

  protected final boolean negative;

  /**
   * The condition partners must satisfy, {@code null} if partners only need
   * an equal key.
   */
  protected final Predicate<Tuple> condition;

  /**
   * Selects the non-key columns of right tuples appended to the left tuples
   * for evaluating the condition.
   */
  protected final TupleMask rightConditionMask;

  /**
   * With condition: for each distinct left tuple, the number of its partners
   * (with multiplicities); empty without condition.
   */
  protected final Map<Tuple, Integer> partnerCounts = new HashMap<>();

  /**
   * Creates the node without condition: partners only need an equal key.
   *
   * @see #ExistenceNode(ReteNetwork, Indexer, Indexer, boolean, Predicate)
   */
  public ExistenceNode(ReteNetwork network, Indexer left, Indexer right, boolean negative) {
    this(network, left, right, negative, null);
  }

  /**
   * Creates the node, initializes its partner counts (with condition) from
   * the current contents of the indexers and registers it as listener of
   * both indexers. The output columns are the columns of the left input, with
   * their optionality; only the key columns of a semi join are not optional,
   * as left tuples with {@link Tuple#ABSENT} in their key have no partner.
   *
   * @param left      indexer of the tuples to filter
   * @param right     indexer of the tuples whose existence is checked
   * @param negative  {@code true} for an anti join, {@code false} for a semi
   *                  join
   * @param condition a deterministic predicate only depending on tuple
   *                  values, evaluated for {@code l ++ r[without key]};
   *                  {@link Tuple#ABSENT} values already contained in
   *                  {@code l} or {@code r} are read as {@code null};
   *                  {@code null} for no condition
   * @throws IllegalArgumentException if both indexers are identical, the keys
   *                                  differ in size or can never match
   */
  public ExistenceNode(ReteNetwork network, Indexer left, Indexer right, boolean negative,
      Predicate<Tuple> condition) {
    super(network, left.getSource().getColumnTypes(), computeColumnOptionality(left, negative));
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
    this.condition = condition;
    this.rightConditionMask = right.getMask().complement();
    if (condition != null) {
      for (Tuple key : left.getKeys()) {
        for (Tuple l : left.lookup(key).elementSet()) {
          partnerCounts.put(l, countPartners(l, key));
        }
      }
    }
    left.addListener(this);
    right.addListener(this);
  }

  /**
   * @return the optionality of the left columns, with the key columns not
   * optional for a semi join
   */
  protected static List<Boolean> computeColumnOptionality(Indexer left, boolean negative) {
    List<Boolean> result = new ArrayList<>(left.getSource().getColumnOptionality());
    if (!negative) {
      for (int i = 0; i < left.getMask().getSize(); i++) {
        result.set(left.getMask().getIndex(i), false);
      }
    }
    return result;
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
   * @return the condition partners must satisfy, empty if partners only need
   * an equal key
   */
  public Optional<Predicate<Tuple>> getCondition() {
    return Optional.ofNullable(condition);
  }

  /**
   * A change on the left side is passed on if the existence condition holds
   * for it. A change on the right side only matters for the left tuples whose
   * existence of a partner it changes (number of partners 0 to 1 or 1 to 0);
   * these left tuples are inserted or deleted. Without condition, these are
   * all left tuples with the same key, if the right change changes the
   * existence of its key.
   */
  @Override
  public void indexerUpdated(Indexer indexer, Direction direction, Tuple tuple, Tuple key) {
    if (condition == null) {
      keyUpdated(indexer, direction, tuple, key);
    } else if (indexer == left) {
      leftUpdated(direction, tuple, key);
    } else {
      rightUpdated(direction, tuple, key);
    }
  }

  /**
   * Handles a change without condition, where the existence of a partner only
   * depends on the key.
   */
  protected void keyUpdated(Indexer indexer, Direction direction, Tuple tuple, Tuple key) {
    if (indexer == left) {
      if ((right.countPartners(key) > 0) != negative) {
        propagate(direction, tuple);
      }
    } else {
      int count = right.count(key);
      boolean existenceChanged = direction == Direction.INSERT ? count == 1 : count == 0;
      // null for keys that are not joinable, which change nothing
      Multiset<Tuple> leftTuples = left.lookupPartners(key);
      if (existenceChanged && leftTuples != null) {
        Direction out = negative ? direction.opposite() : direction;
        for (Multiset.Entry<Tuple> entry : leftTuples.entrySet()) {
          propagate(out, entry.getElement(), entry.getCount());
        }
      }
    }
  }

  /**
   * @param tuple the inserted or deleted left tuple; the right side is
   *              unchanged, so its partners are computed from the current
   *              contents of the right indexer
   */
  protected void leftUpdated(Direction direction, Tuple tuple, Tuple key) {
    int partners = countPartners(tuple, key);
    if ((partners > 0) != negative) {
      propagate(direction, tuple);
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
   * @param tuple the inserted or deleted right tuple, which changes the number
   *              of partners of the left tuples with the same key for which
   *              the condition holds by one
   */
  protected void rightUpdated(Direction direction, Tuple tuple, Tuple key) {
    // null for keys that are not joinable, which change nothing
    Multiset<Tuple> leftTuples = left.lookupPartners(key);
    if (leftTuples == null) {
      return;
    }
    for (Multiset.Entry<Tuple> entry : leftTuples.entrySet()) {
      Tuple l = entry.getElement();
      if (!condition.test(l.concat(tuple, rightConditionMask))) {
        continue;
      }
      int before = partnerCounts.get(l);
      int after = direction == Direction.INSERT ? before + 1 : before - 1;
      partnerCounts.put(l, after);
      if ((before == 0) != (after == 0)) {
        propagate(negative ? direction.opposite() : direction, l, entry.getCount());
      }
    }
  }

  /**
   * @return the number of partners (with multiplicities) of the left tuple
   * in the current contents of the right indexer
   */
  protected int countPartners(Tuple leftTuple, Tuple key) {
    if (condition == null) {
      return right.countPartners(key);
    }
    int partners = 0;
    Multiset<Tuple> rightTuples = right.lookupPartners(key);
    if (rightTuples != null) {
      for (Multiset.Entry<Tuple> entry : rightTuples.entrySet()) {
        if (condition.test(leftTuple.concat(entry.getElement(), rightConditionMask))) {
          partners += entry.getCount();
        }
      }
    }
    return partners;
  }

  /**
   * Adds all left tuples satisfying the existence condition, computed from
   * the current indexer contents, independent of the partner counts.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Tuple key : left.getKeys()) {
      for (Multiset.Entry<Tuple> entry : left.lookup(key).entrySet()) {
        if ((countPartners(entry.getElement(), key) > 0) != negative) {
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
