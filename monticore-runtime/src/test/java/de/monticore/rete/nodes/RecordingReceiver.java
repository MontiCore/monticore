/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.rete.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A child of a tuple source recording all received deltas in order, e.g.
 * {@code +[a, 1]} or {@code -[a, 1]}. It also maintains the contents of the
 * source derived from the deltas, to check them against
 * {@link TupleSource#pullInto(Multiset)}.
 */
public class RecordingReceiver implements Receiver {

  protected final TupleSource source;

  protected final Multiset<Tuple> contents = HashMultiset.create();

  protected final List<String> deltas = new ArrayList<>();

  /**
   * Initializes the contents from the source and attaches to it.
   */
  public RecordingReceiver(TupleSource source) {
    this.source = source;
    source.pullInto(contents);
    source.attachChild(this);
  }

  /**
   * Records the delta, failing on the deletion of a tuple not received before.
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    if (direction == Direction.INSERT) {
      contents.add(tuple);
    } else {
      assertTrue(contents.remove(tuple), "deletion of unknown tuple " + tuple);
    }
    deltas.add(format(direction, tuple));
  }

  /**
   * @return the deltas received since the last call, in order
   */
  public List<String> take() {
    List<String> result = List.copyOf(deltas);
    deltas.clear();
    return result;
  }

  /**
   * @return the contents of the source derived from the initial contents
   * and the received deltas
   */
  public Multiset<Tuple> getContents() {
    return contents;
  }

  /**
   * Asserts that the received deltas lead to the current contents of the source.
   */
  public void assertConsistent() {
    Multiset<Tuple> current = HashMultiset.create();
    source.pullInto(current);
    assertEquals(current, contents);
  }

  public static String format(Direction direction, Tuple tuple) {
    return (direction == Direction.INSERT ? "+" : "-") + tuple;
  }
}
