/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.rete.tuple.Tuple;

import java.util.List;

/**
 * A source node with arbitrary column types whose contents are set by the
 * test. Each change is propagated through the network like a change of an
 * input node.
 */
public class TestSource extends AbstractSourceNode {

  protected final TestNetwork testNetwork;

  protected final Multiset<Tuple> contents = HashMultiset.create();

  public TestSource(TestNetwork network, Class<?>... columnTypes) {
    super(network, List.of(columnTypes));
    this.testNetwork = network;
  }

  /**
   * Inserts one occurrence of the tuple and propagates the change.
   */
  public void insert(Object... values) {
    change(Direction.INSERT, Tuple.of(values));
  }

  /**
   * Deletes one occurrence of the tuple and propagates the change. The
   * deletion of an unknown tuple is propagated as well, to test how the
   * receivers detect inconsistencies.
   */
  public void delete(Object... values) {
    change(Direction.DELETE, Tuple.of(values));
  }

  /**
   * Changes the contents and propagates the delta through the network.
   */
  public void change(Direction direction, Tuple tuple) {
    testNetwork.change(() -> emit(direction, tuple));
  }

  /**
   * Changes the contents and enqueues the delta without propagating it, to
   * report several deltas within one {@link TestNetwork#change(Runnable)}.
   */
  public void emit(Direction direction, Tuple tuple) {
    if (direction == Direction.INSERT) {
      contents.add(tuple);
    } else {
      contents.remove(tuple);
    }
    propagate(direction, tuple);
  }

  @Override
  public void pullInto(Multiset<Tuple> collector) {
    collector.addAll(contents);
  }

  @Override
  protected void detachFromParents() {
    // no parents
  }
}
