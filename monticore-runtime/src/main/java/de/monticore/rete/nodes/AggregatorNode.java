/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.aggregation.AggregationOperator;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Groups the tuples of the parent by the columns selected by a group mask and
 * aggregates one column per group. Contains one tuple
 * {@code groupKey ++ [result]} per non-empty group.
 * <p>
 * The aggregation sees the parent's multiplicities, e.g. counting a tuple
 * contained twice yields 2. Empty groups do not exist, i.e. a count of 0 is
 * never produced.
 */
public class AggregatorNode extends AbstractSingleParentNode {

  /**
   * Value of {@code aggregatedColumn} to aggregate whole tuples (e.g. for counting).
   */
  public static final int WHOLE_TUPLE = -1;

  protected final TupleMask groupMask;

  protected final int aggregatedColumn;

  @SuppressWarnings("rawtypes")
  protected final AggregationOperator operator;

  protected final Map<Tuple, Group> groups = new HashMap<>();

  protected static class Group {

    protected Object accumulator;

    protected int size;

    protected Object result;

    protected Group(Object accumulator) {
      this.accumulator = accumulator;
    }
  }

  /**
   * Creates the node, computes the aggregates of the current contents of
   * {@code parent} and attaches it to {@code parent}.
   *
   * @param groupMask        selects the grouping columns
   * @param aggregatedColumn index of the aggregated column, or {@link #WHOLE_TUPLE}
   * @param operator         the aggregation operator
   */
  public AggregatorNode(ReteNetwork network, TupleSource parent, TupleMask groupMask,
      int aggregatedColumn, AggregationOperator<?, ?, ?> operator) {
    super(network, parent, computeColumnTypes(parent, groupMask, aggregatedColumn, operator),
        EvalNode.append(groupMask.transform(parent.getColumnOptionality()), false));
    this.groupMask = groupMask;
    this.aggregatedColumn = aggregatedColumn;
    this.operator = operator;
    for (Multiset.Entry<Tuple> entry : pull(parent).entrySet()) {
      for (int i = 0; i < entry.getCount(); i++) {
        update(Direction.INSERT, entry.getElement());
      }
    }
    attach();
  }

  /**
   * Validates the configuration and computes the output column types: the
   * types of the group columns followed by the result type of the operator.
   * Group columns may be optional (tuples with {@link Tuple#ABSENT} in a
   * group column form a group of their own), the aggregated column must not
   * be optional.
   *
   * @throws IllegalArgumentException if the group mask does not fit the
   *                                  parent's arity or the aggregated column
   *                                  is invalid or optional
   */
  protected static List<Class<?>> computeColumnTypes(TupleSource parent, TupleMask groupMask,
      int aggregatedColumn, AggregationOperator<?, ?, ?> operator) {
    if (groupMask.getSourceArity() != parent.getArity()) {
      throw new IllegalArgumentException("Group mask " + groupMask + " does not fit arity "
          + parent.getArity() + " of " + parent);
    }
    if (aggregatedColumn < WHOLE_TUPLE || aggregatedColumn >= parent.getArity()) {
      throw new IllegalArgumentException("Invalid aggregated column " + aggregatedColumn);
    }
    if (aggregatedColumn != WHOLE_TUPLE) {
      ColumnTypes.checkNotOptional(parent, aggregatedColumn, "the aggregated column");
    }
    Class<?> valueType = aggregatedColumn == WHOLE_TUPLE ? Tuple.class
        : parent.getColumnTypes().get(aggregatedColumn);
    List<Class<?>> types = new ArrayList<>(groupMask.transform(parent.getColumnTypes()));
    types.add(operator.getResultType(valueType));
    return types;
  }

  public TupleMask getGroupMask() {
    return groupMask;
  }

  public int getAggregatedColumn() {
    return aggregatedColumn;
  }

  public AggregationOperator<?, ?, ?> getOperator() {
    return operator;
  }

  /**
   * Updates the accumulator of the tuple's group. If the aggregation result
   * of the group changes, the old result tuple is deleted and the new one is
   * inserted; if the group becomes empty, its result tuple is deleted.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void update(Direction direction, Tuple tuple) {
    Tuple key = groupMask.transform(tuple);
    Object value = aggregatedColumn == WHOLE_TUPLE ? tuple : tuple.get(aggregatedColumn);
    Group group = groups.get(key);
    boolean isInsertion = direction == Direction.INSERT;
    if (group == null) {
      if (!isInsertion) {
        throw inconsistency(this, tuple);
      }
      group = new Group(operator.createNeutral());
      groups.put(key, group);
    }
    Object oldResult = group.result;
    group.accumulator = operator.update(group.accumulator, value, isInsertion);
    group.size += isInsertion ? 1 : -1;
    if (group.size == 0) {
      groups.remove(key);
      propagate(Direction.DELETE, key.append(oldResult));
      return;
    }
    Object newResult = Objects.requireNonNull(operator.getAggregate(group.accumulator),
        "Aggregation result must not be null");
    group.result = newResult;
    if (!newResult.equals(oldResult)) {
      if (oldResult != null) {
        propagate(Direction.DELETE, key.append(oldResult));
      }
      propagate(Direction.INSERT, key.append(newResult));
    }
  }

  /**
   * @return the current aggregation result of the given group, or {@code null}
   * if the group is empty
   */
  public Object getResult(Tuple groupKey) {
    Group group = groups.get(groupKey);
    return group == null ? null : group.result;
  }

  /**
   * Adds one {@code groupKey ++ [result]} tuple per non-empty group.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Map.Entry<Tuple, Group> entry : groups.entrySet()) {
      collector.add(entry.getKey().append(entry.getValue().result));
    }
  }
}
