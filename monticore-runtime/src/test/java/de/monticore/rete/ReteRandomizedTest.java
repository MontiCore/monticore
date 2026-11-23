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

    // optional pattern elements: each state with an optional successor t != s
    TupleSource optNext = net.leftOuterJoin(net.indexer(states, 0), net.indexer(edges, 0),
        t -> t.get(0) != t.get(1));                                      // [s, t?]
    // nested optional: the optional name of the optional successor
    TupleSource optNextName = net.leftOuterJoin(net.indexer(optNext, 1),
        net.indexer(names, 0));                                          // [s, t?, n?]
    // sequential optional with injectivity across both optionals
    TupleSource optPred = net.leftOuterJoin(net.indexer(optNext, 0), net.indexer(edges, 1),
        t -> t.get(2) != t.get(0) && t.get(2) != t.get(1));              // [s, t?, p?]
    // optional successor edge of each edge, with indexers of the same source
    TupleSource optEdgeNext = net.leftOuterJoin(net.indexer(edges, 1),
        net.indexer(edges, 0));                                          // [x, y, z?]
    // downstream nodes reading ABSENT
    TupleSource optNextJoined = net.join(net.indexer(optNext, 1), net.indexer(names, 0));
    TupleSource optNextNoSucc = net.antiJoin(net.indexer(optNext, 1), net.indexer(edges, 0));
    TupleSource optNextHasSucc = net.semiJoin(net.indexer(optNext, 1), net.indexer(edges, 0));
    TupleSource optPredDistinct = net.inequality(net.distinct(net.project(optPred, 1, 2)), 0, 1);
    TupleSource optPredCount = net.count(optPred, 1);

    // existence with conditions spanning both sides (e.g. injectivity): edges (x, y) without
    // an edge from x to another target, with indexers of the same source
    TupleSource onlyTarget = net.antiJoin(net.indexer(edges, 0), net.indexer(edges, 0),
        t -> t.get(1) != t.get(2));                                      // [x, y]
    // states with a predecessor other than themselves
    TupleSource otherPred = net.semiJoin(net.indexer(states, 0), net.indexer(edges, 1),
        t -> t.get(0) != t.get(1));                                      // [s]
    // a key and a condition reading ABSENT: each optional successor t without a successor
    // other than s
    TupleSource optNextNoOtherSucc = net.antiJoin(net.indexer(optNext, 1),
        net.indexer(edges, 0), t -> t.get(2) != t.get(0));             // [s, t?]

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
    s.sources.put("optNext", optNext);
    s.sources.put("optNextName", optNextName);
    s.sources.put("optPred", optPred);
    s.sources.put("optEdgeNext", optEdgeNext);
    s.sources.put("optNextJoined", optNextJoined);
    s.sources.put("optNextNoSucc", optNextNoSucc);
    s.sources.put("optNextHasSucc", optNextHasSucc);
    s.sources.put("optPredDistinct", optPredDistinct);
    s.sources.put("optPredCount", optPredCount);
    s.sources.put("onlyTarget", onlyTarget);
    s.sources.put("otherPred", otherPred);
    s.sources.put("optNextNoOtherSucc", optNextNoOtherSucc);

    s.productions.put("tc", net.production(tc, "x", "y"));
    s.productions.put("distinct", net.production(distinct, "x", "z"));
    s.productions.put("anti", net.production(anti, "x", "y"));
    s.productions.put("deadEnds", net.production(deadEnds, "s"));
    s.productions.put("j2", net.production(j2, "x", "y", "z"));
    s.productions.put("optPred", net.production(optPred, "s", "t", "p"));
    s.productions.put("onlyTarget", net.production(onlyTarget, "x", "y"));
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
   * Compares the left outer joins to their semantics computed directly from
   * the facts, independent of the network: each state with each successor
   * {@code t != s}, or {@code ABSENT} if there is none ({@code optNext}); the
   * name of the successor, if any ({@code optNextName}); and each predecessor
   * {@code p} different from {@code s} and {@code t}, or {@code ABSENT}
   * ({@code optPred}). Multiplicities result from parallel edges.
   */
  protected static void assertOptionalSemantics(Facts facts, Structure actual, String context) {
    Multiset<Tuple> optNext = HashMultiset.create();
    for (ASTState s : facts.nodes) {
      boolean found = false;
      for (Tuple edge : facts.edges) {
        if (edge.get(0) == s && edge.get(1) != s) {
          optNext.add(Tuple.of(s, edge.get(1)));
          found = true;
        }
      }
      if (!found) {
        optNext.add(Tuple.of(s, Tuple.ABSENT));
      }
    }
    Multiset<Tuple> optNextName = HashMultiset.create();
    Multiset<Tuple> optPred = HashMultiset.create();
    for (Tuple st : optNext) {
      Object s = st.get(0);
      Object t = st.isAbsent(1) ? Tuple.ABSENT : st.get(1);
      String name = st.isAbsent(1) ? null : facts.names.get(st.get(1, ASTState.class));
      optNextName.add(Tuple.of(s, t, name == null ? Tuple.ABSENT : name));
      boolean found = false;
      for (Tuple edge : facts.edges) {
        Object p = edge.get(0);
        if (edge.get(1) == s && p != s && p != t) {
          optPred.add(Tuple.of(s, t, p));
          found = true;
        }
      }
      if (!found) {
        optPred.add(Tuple.of(s, t, Tuple.ABSENT));
      }
    }
    assertEquals(optNext, contents(actual.sources.get("optNext")), context + ": optNext");
    assertEquals(optNextName, contents(actual.sources.get("optNextName")),
        context + ": optNextName");
    assertEquals(optPred, contents(actual.sources.get("optPred")), context + ": optPred");
    assertExistenceSemantics(facts, optNext, actual, context);
  }

  /**
   * Compares the semi and anti joins with conditions to their semantics
   * computed directly from the facts: each edge {@code (x, y)} without an edge
   * from {@code x} to a target other than {@code y} ({@code onlyTarget}), each
   * state with a predecessor other than itself ({@code otherPred}), and each
   * tuple {@code (s, t)} of {@code optNext} whose successor {@code t} is
   * absent or has no successor other than {@code s}
   * ({@code optNextNoOtherSucc}). Left tuples keep their multiplicities.
   *
   * @param optNext the expected contents of {@code optNext}
   */
  protected static void assertExistenceSemantics(Facts facts, Multiset<Tuple> optNext,
      Structure actual, String context) {
    Multiset<Tuple> onlyTarget = HashMultiset.create();
    for (Tuple edge : facts.edges) {
      if (facts.edges.stream()
          .noneMatch(other -> other.get(0) == edge.get(0) && other.get(1) != edge.get(1))) {
        onlyTarget.add(edge);
      }
    }
    Multiset<Tuple> otherPred = HashMultiset.create();
    for (ASTState s : facts.nodes) {
      if (facts.edges.stream().anyMatch(edge -> edge.get(1) == s && edge.get(0) != s)) {
        otherPred.add(Tuple.of(s));
      }
    }
    Multiset<Tuple> optNextNoOtherSucc = HashMultiset.create();
    for (Tuple st : optNext) {
      if (st.isAbsent(1) || facts.edges.stream()
          .noneMatch(edge -> edge.get(0) == st.get(1) && edge.get(1) != st.get(0))) {
        optNextNoOtherSucc.add(st);
      }
    }
    assertEquals(onlyTarget, contents(actual.sources.get("onlyTarget")),
        context + ": onlyTarget");
    assertEquals(otherPred, contents(actual.sources.get("otherPred")), context + ": otherPred");
    assertEquals(optNextNoOtherSucc, contents(actual.sources.get("optNextNoOtherSucc")),
        context + ": optNextNoOtherSucc");
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
   * kinds of networks built from scratch, and its left outer joins and its
   * semi and anti joins with conditions to their semantics.
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
      if (step % 50 == 49) {
        assertOptionalSemantics(facts, incremental, "seed " + seed + ", step " + step);
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
