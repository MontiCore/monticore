/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiset union of several parents of equal arity (disjunction of pattern
 * bodies). Tuples contained in several parents are counted several times;
 * use a {@link DistinctNode} to remove duplicates.
 */
public class UnionNode extends AbstractSourceNode implements Receiver {

  protected final List<TupleSource> parents;

  /**
   * Creates the union and attaches it to all parents. A union keeps no
   * memory, so no initialization is required. A parent contained several
   * times in {@code parents} contributes its tuples several times.
   *
   * @throws IllegalArgumentException if {@code parents} is empty or the
   *                                  parents differ in arity
   */
  public UnionNode(ReteNetwork network, List<? extends TupleSource> parents) {
    super(network, computeColumnTypes(parents), computeColumnOptionality(parents));
    parents.forEach(this::checkSameNetwork);
    this.parents = List.copyOf(parents);
    for (TupleSource parent : this.parents) {
      parent.attachChild(this);
    }
  }

  /**
   * Validates the parents and computes the output column types as common
   * supertypes of the parents' column types.
   *
   * @throws IllegalArgumentException if {@code parents} is empty or the
   *                                  parents differ in arity
   */
  protected static List<Class<?>> computeColumnTypes(List<? extends TupleSource> parents) {
    if (parents.isEmpty()) {
      throw new IllegalArgumentException("A union needs at least one parent");
    }
    List<Class<?>> types = new ArrayList<>(parents.get(0).getColumnTypes());
    for (TupleSource parent : parents) {
      if (parent.getArity() != types.size()) {
        throw new IllegalArgumentException("Union of different arities: " + parents);
      }
      for (int i = 0; i < types.size(); i++) {
        types.set(i, ColumnTypes.commonSupertype(types.get(i), parent.getColumnTypes().get(i)));
      }
    }
    return types;
  }

  /**
   * An output column is optional if it is optional in any parent. Must be
   * called after {@link #computeColumnTypes}, which validates the parents.
   */
  protected static List<Boolean> computeColumnOptionality(List<? extends TupleSource> parents) {
    List<Boolean> optionality = parents.get(0).getColumnOptionality();
    for (TupleSource parent : parents) {
      optionality = ColumnTypes.eitherOptional(optionality, parent.getColumnOptionality());
    }
    return optionality;
  }

  public List<TupleSource> getParents() {
    return parents;
  }

  /**
   * Passes the delta of any parent on unchanged.
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    propagate(direction, tuple);
  }

  /**
   * Adds the contents of all parents.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (TupleSource parent : parents) {
      parent.pullInto(collector);
    }
  }

  /**
   * Detaches this union from all parents.
   */
  @Override
  protected void detachFromParents() {
    for (TupleSource parent : parents) {
      parent.detachChild(this);
    }
  }
}
