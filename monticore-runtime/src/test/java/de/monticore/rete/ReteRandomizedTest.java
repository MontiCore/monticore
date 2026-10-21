/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.rete.aggregation.Aggregators;
import de.monticore.rete.nodes.Indexer;
import de.monticore.rete.nodes.JoinNode;
import de.monticore.rete.nodes.ProductionNode;
import de.monticore.rete.nodes.TupleSource;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static de.monticore.rete.AutomatonModel.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Applies random changes to a network and compares the incrementally
 * maintained results to networks built from scratch for the final facts.
 */
public class ReteRandomizedTest {

  protected static final String[] NAMES = {"a", "b", "c"};

  /**
   * The nodes of a network built by {@link #buildInner(ReteNetwork)}.
   */
  protected static class Structure {

    protected final Map<String, TupleSource> sources = new LinkedHashMap<>();

    protected final Map<String, ProductionNode> productions = new LinkedHashMap<>();

    protected final Map<String, RecordingListener> listeners = new HashMap<>();
  }

  /**
   * The current facts of the model.
   */
  protected static class Facts {

    protected final Set<ASTState> nodes = new HashSet<>();

    protected final List<Tuple> edges = new ArrayList<>();

    protected final Map<ASTState, String> names = new HashMap<>();

    /**
     * Reports all facts to the network (insertions only).
     */
    protected void feed(ReteNetwork net) {
      nodes.forEach(net::nodeAdded);
      for (Tuple edge : edges) {
        net.featureValueAdded(NEXT, edge.get(0, ASTState.class), edge.get(1, ASTState.class));
      }
      names.forEach((s, n) -> net.featureValueAdded(NAME, s, n));
    }
  }

  /**
   * Creates the input nodes, which must exist before facts are reported.
   */
  protected static void createInputs(ReteNetwork net) {
    net.typeInput(ASTState.class);
    net.featureInput(NEXT);
    net.featureInput(NAME);
  }

  /**
   * Builds a network using every node type, including topologies that are
   * only handled correctly with queue-based propagation, and registers a
   * {@link RecordingListener} at each production.
   *
   * @return the nodes to compare, by name
   */
  protected static Structure buildInner(ReteNetwork net) {
    Structure s = new Structure();
    TupleSource states = net.typeInput(ASTState.class);
    TupleSource edges = net.featureInput(NEXT);
    TupleSource names = net.featureInput(NAME);

    // L is shared by two joins and an anti join; the output of J1 feeds the
    // right side of J2 and the anti join
    Indexer l = net.indexer(edges, 1);
    JoinNode j1 = net.join(l, net.indexer(edges, 0)); // [x, y, z]: 2-step paths
    TupleSource p2 = net.project(j1, 0, 2);
    Indexer r2 = net.indexer(p2, 0);
    JoinNode j2 = net.join(l, r2); // [x, y, z]: 3-step paths via y
    TupleSource anti = net.antiJoin(l, r2); // edges (x, y) without 2-step path from y
    TupleSource deadEnds = net.antiJoin(net.indexer(states, 0), net.indexer(edges, 0));
    TupleSource sameName = net.join(net.indexer(names, 1), net.indexer(names, 1));
    TupleSource tc = net.transitiveClosure(edges);
    TupleSource tcNames = net.join(net.indexer(tc, 1), net.indexer(names, 0));
    TupleSource count = net.count(j2, 0);
    TupleSource minName = net.aggregate(tcNames, TupleMask.of(3, 0), 2, Aggregators.min());
    TupleSource distinct = net.distinct(net.project(j2, 0, 2));
    TupleSource union = net.union(p2, edges, net.project(j2, 0, 2));
    TupleSource namedReachable = net.semiJoin(net.indexer(tc, 1), net.indexer(names, 0));

    s.sources.put("j1", j1);
    s.sources.put("p2", p2);
    s.sources.put("j2", j2);
    s.sources.put("anti", anti);
    s.sources.put("deadEnds", deadEnds);
    s.sources.put("sameName", sameName);
    s.sources.put("tc", tc);
    s.sources.put("tcNames", tcNames);
    s.sources.put("count", count);
    s.sources.put("minName", minName);
    s.sources.put("distinct", distinct);
    s.sources.put("union", union);
    s.sources.put("namedReachable", namedReachable);

    s.productions.put("tc", net.production(tc, "x", "y"));
    s.productions.put("distinct", net.production(distinct, "x", "z"));
    s.productions.put("anti", net.production(anti, "x", "y"));
    s.productions.put("deadEnds", net.production(deadEnds, "s"));
    s.productions.put("j2", net.production(j2, "x", "y", "z"));
    for (Map.Entry<String, ProductionNode> p : s.productions.entrySet()) {
      RecordingListener listener = new RecordingListener();
      p.getValue().addMatchListener(listener, true);
      s.listeners.put(p.getKey(), listener);
    }
    return s;
  }

  /**
   * @return the current contents of the node, including multiplicities
   */
  protected static Multiset<Tuple> contents(TupleSource source) {
    Multiset<Tuple> result = HashMultiset.create();
    source.pullInto(result);
    return result;
  }

  /**
   * Asserts that all nodes of both structures have the same contents (with
   * multiplicities), all productions the same matches, and that the events
   * received by the listeners of {@code actual} add up to its matches.
   */
  protected static void assertSameResults(Structure expected, Structure actual, String context) {
    for (String name : expected.sources.keySet()) {
      assertEquals(contents(expected.sources.get(name)), contents(actual.sources.get(name)),
          context + ": node " + name);
    }
    for (String name : expected.productions.keySet()) {
      assertEquals(expected.productions.get(name).getMatchTuples(),
          actual.productions.get(name).getMatchTuples(), context + ": production " + name);
      Set<Tuple> fromEvents = new HashSet<>();
      actual.listeners.get(name).matches.forEach(m -> fromEvents.add(m.toTuple()));
      assertEquals(actual.productions.get(name).getMatchTuples(), fromEvents,
          context + ": events of production " + name);
    }
  }

  /**
   * Builds a fresh network for the given facts.
   *
   * @param pullInitialization whether the inner nodes are created after the
   *                           facts were reported (initialization by pulling)
   *                           or before (incremental insertion)
   */
  protected static Structure fromScratch(Facts facts, boolean pullInitialization) {
    ReteNetwork net = new ReteNetwork();
    createInputs(net);
    if (pullInitialization) {
      facts.feed(net);
      return buildInner(net);
    }
    Structure s = buildInner(net);
    facts.feed(net);
    return s;
  }

  /**
   * Applies random insertions and deletions (some grouped in transactions)
   * and periodically compares the incrementally maintained network to both
   * kinds of networks built from scratch.
   */
  @ParameterizedTest
  @ValueSource(longs = {1, 2, 3, 4, 5})
  public void testRandomChanges(long seed) {
    Random random = new Random(seed);
    List<ASTState> states = new ArrayList<>();
    for (int i = 0; i < 12; i++) {
      states.add(new ASTState("s" + i));
    }
    ReteNetwork net = new ReteNetwork();
    createInputs(net);
    Structure incremental = buildInner(net);
    Facts facts = new Facts();

    for (int step = 0; step < 1500; step++) {
      if (random.nextInt(10) == 0) {
        // a transaction with several changes
        net.transaction(() -> {
          for (int i = 0; i < 4; i++) {
            randomChange(net, facts, states, random);
          }
        });
      } else {
        randomChange(net, facts, states, random);
      }
      if (step % 300 == 299) {
        assertSameResults(fromScratch(facts, false), incremental, "seed " + seed + ", step " + step);
        assertSameResults(fromScratch(facts, true), incremental, "seed " + seed + ", step " + step);
      }
    }
  }

  /**
   * Performs one random change (edge insertion or deletion, name addition,
   * removal or change, or AST node addition or removal), reporting it to
   * the network and recording it in {@code facts}.
   */
  protected void randomChange(ReteNetwork net, Facts facts, List<ASTState> states,
      Random random) {
    ASTState s = states.get(random.nextInt(states.size()));
    ASTState t = states.get(random.nextInt(states.size()));
    int op = random.nextInt(100);
    if (op < 40) {
      // parallel edges and self loops are allowed
      net.featureValueAdded(NEXT, s, t);
      facts.edges.add(Tuple.of(s, t));
    } else if (op < 75) {
      if (!facts.edges.isEmpty()) {
        Tuple edge = facts.edges.remove(random.nextInt(facts.edges.size()));
        net.featureValueRemoved(NEXT, edge.get(0, ASTState.class), edge.get(1, ASTState.class));
      }
    } else if (op < 90) {
      String newName = NAMES[random.nextInt(NAMES.length)];
      String oldName = facts.names.get(s);
      if (oldName == null) {
        net.featureValueAdded(NAME, s, newName);
        facts.names.put(s, newName);
      } else if (random.nextBoolean()) {
        net.featureValueRemoved(NAME, s, oldName);
        facts.names.remove(s);
      } else {
        net.featureValueChanged(NAME, s, oldName, newName);
        facts.names.put(s, newName);
      }
    } else {
      if (facts.nodes.remove(s)) {
        net.nodeRemoved(s);
      } else {
        net.nodeAdded(s);
        facts.nodes.add(s);
      }
    }
  }
}
