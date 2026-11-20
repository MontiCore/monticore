/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import com.google.common.primitives.Primitives;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Extends each tuple of the parent by a computed value. Tuples for which the
 * function returns {@code null} are dropped.
 * <p>
 * The function must be deterministic and must only depend on the values of
 * the tuple (see {@link FilterNode}, also for {@link Tuple#ABSENT} in
 * optional columns). The appended column is not optional.
 */
public class EvalNode extends AbstractSingleParentNode {

  protected final Function<Tuple, ?> function;

  protected final Class<?> resultType;

  /**
   * Creates the node and attaches it to {@code parent}. An eval node keeps
   * no memory, so no initialization is required.
   *
   * @param function   a deterministic function computing the appended value
   * @param resultType the type of the computed values; checked at runtime,
   *                   primitive types are replaced by their wrapper types
   */
  public EvalNode(ReteNetwork network, TupleSource parent, Function<Tuple, ?> function,
      Class<?> resultType) {
    super(network, parent, append(parent.getColumnTypes(), Primitives.wrap(resultType)),
        append(parent.getColumnOptionality(), false));
    this.function = function;
    this.resultType = Primitives.wrap(resultType);
    attach();
  }

  /**
   * @return a new list containing {@code list} followed by {@code element},
   * e.g. the column types of the parent followed by the result type
   */
  protected static <T> List<T> append(List<T> list, T element) {
    List<T> result = new ArrayList<>(list);
    result.add(element);
    return result;
  }

  /**
   * Applies the function to a tuple of the parent.
   *
   * @return {@code tuple} extended by the computed value, or {@code null} if
   * the function returned {@code null}
   * @throws ClassCastException if the computed value is not an instance of
   *                            the declared result type
   */
  protected Tuple evaluate(Tuple tuple) {
    Object value = function.apply(tuple);
    if (value == null) {
      return null;
    }
    if (!resultType.isInstance(value)) {
      throw new ClassCastException("Eval function returned " + value + " of type "
          + value.getClass().getName() + ", expected " + resultType.getName());
    }
    return tuple.append(value);
  }

  /**
   * Passes the delta on with the computed value appended, unless the
   * function returns {@code null}.
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    Tuple result = evaluate(tuple);
    if (result != null) {
      propagate(direction, result);
    }
  }

  /**
   * Adds the parent's tuples extended by their computed values, skipping
   * tuples for which the function returns {@code null}.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Multiset.Entry<Tuple> entry : pull(parent).entrySet()) {
      Tuple result = evaluate(entry.getElement());
      if (result != null) {
        collector.add(result, entry.getCount());
      }
    }
  }
}
