/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class of nodes producing tuples. Manages the attached children and
 * the column types.
 */
public abstract class AbstractSourceNode extends ReteNode implements TupleSource {

  protected final List<Receiver> children = new ArrayList<>();

  protected final List<Class<?>> columnTypes;

  protected AbstractSourceNode(ReteNetwork network, List<Class<?>> columnTypes) {
    super(network);
    this.columnTypes = List.copyOf(columnTypes);
  }

  @Override
  public int getArity() {
    return columnTypes.size();
  }

  @Override
  public List<Class<?>> getColumnTypes() {
    return columnTypes;
  }

  @Override
  public void attachChild(Receiver child) {
    network.checkStructuralChangeAllowed();
    if (disposed) {
      throw new IllegalStateException(this + " is disposed");
    }
    children.add(child);
  }

  @Override
  public void detachChild(Receiver child) {
    network.checkStructuralChangeAllowed();
    children.remove(child);
  }

  @Override
  public List<Receiver> getChildren() {
    return Collections.unmodifiableList(children);
  }

  /**
   * Sends the delta to all children. The delivery is deferred via the
   * network's propagation queue (see {@link ReteNetwork}).
   */
  protected void propagate(Direction direction, Tuple tuple) {
    propagate(direction, tuple, 1);
  }

  /**
   * Sends {@code count} occurrences of the delta to all children, deferred
   * via the network's propagation queue. Does nothing if {@code count <= 0}.
   *
   * @param direction direction of the delta
   * @param tuple     the inserted or deleted tuple
   * @param count     the number of occurrences (multiplicity)
   */
  protected void propagate(Direction direction, Tuple tuple, int count) {
    if (count > 0) {
      for (Receiver child : children) {
        network.enqueue(child, direction, tuple, count);
      }
    }
  }

  /**
   * A source node can be disposed if no child is attached anymore.
   */
  @Override
  protected void checkDisposable() {
    if (!children.isEmpty()) {
      throw new IllegalStateException(this + " still has children " + children);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + columnTypes.stream()
        .map(Class::getSimpleName).toList();
  }
}
