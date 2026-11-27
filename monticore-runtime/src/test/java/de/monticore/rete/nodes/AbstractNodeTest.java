/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

/**
 * Base class of the unit tests of single nodes, which are driven by
 * {@link TestSource}s and observed by {@link RecordingReceiver}s.
 * <p>
 * Each test class covers the behavior its node implements itself. Behavior
 * inherited from a base class is tested once, in the test class of a node
 * using it:
 * <ul>
 *   <li><b>Disposing</b> as implemented by {@link AbstractSingleParentNode}
 *   (filter, eval, projection, distinct, aggregator, transitive closure) is
 *   tested in {@link FilterNodeTest}. Nodes with their own implementation
 *   (indexer, join, existence, union, production, input nodes) are tested in
 *   their own test classes.</li>
 *   <li><b>Rejecting parents of another network</b> is tested for
 *   {@link AbstractSingleParentNode} in {@link FilterNodeTest}, and in
 *   {@link IndexerTest} and {@link UnionNodeTest}.</li>
 *   <li><b>Detecting the deletion of unknown tuples</b> is only possible for
 *   nodes with memory (indexer, distinct, aggregator, transitive closure,
 *   production) and tested there; nodes without memory pass deltas on
 *   unchecked.</li>
 *   <li><b>Initialization from a populated source</b> is tested in each test
 *   class.</li>
 *   <li><b>Optional columns</b>: the optionality of the output columns and
 *   the handling of {@link de.monticore.rete.tuple.Tuple#ABSENT} are tested in
 *   each test class. The column metadata of {@link AbstractSourceNode}
 *   (default optionality, validation, string representation) is tested in
 *   {@link FilterNodeTest}.</li>
 * </ul>
 */
public abstract class AbstractNodeTest {

  protected TestNetwork net;

  @BeforeEach
  public void setUpNetwork() {
    net = new TestNetwork();
  }

  /**
   * @return a new source with the given column types
   */
  protected TestSource source(Class<?>... columnTypes) {
    return new TestSource(net, columnTypes);
  }

  /**
   * @param columnOptionality for each column, whether it is optional, e.g.
   *                          {@code List.of(false, true)}
   * @return a new source with the given column types and optional columns
   */
  protected TestSource source(List<Boolean> columnOptionality, Class<?>... columnTypes) {
    return new TestSource(net, columnOptionality, columnTypes);
  }

  /**
   * @return a new recorder attached to the node
   */
  protected static RecordingReceiver record(TupleSource node) {
    return new RecordingReceiver(node);
  }

  /**
   * @return the current contents of the node, with multiplicities
   */
  protected static Multiset<Tuple> contents(TupleSource node) {
    Multiset<Tuple> result = HashMultiset.create();
    node.pullInto(result);
    return result;
  }

  /**
   * @return a multiset of the given tuples
   */
  protected static Multiset<Tuple> tuples(Tuple... tuples) {
    return HashMultiset.create(Arrays.asList(tuples));
  }

  /**
   * @return a multiset of the given strings, to compare deltas whose order
   * is not specified
   */
  protected static Multiset<String> unordered(Iterable<String> deltas) {
    return HashMultiset.create(deltas);
  }
}
