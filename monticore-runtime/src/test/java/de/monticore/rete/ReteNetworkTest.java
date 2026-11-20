/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.ast.ASTNode;
import de.monticore.rete.aggregation.Aggregators;
import de.monticore.rete.nodes.AggregatorNode;
import de.monticore.rete.nodes.ExistenceNode;
import de.monticore.rete.nodes.FeatureInputNode;
import de.monticore.rete.nodes.Indexer;
import de.monticore.rete.nodes.JoinNode;
import de.monticore.rete.nodes.ProductionNode;
import de.monticore.rete.nodes.TupleSource;
import de.monticore.rete.nodes.TypeInputNode;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.monticore.rete.AutomatonModel.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReteNetworkTest {

  protected ReteNetwork net;

  protected ASTAutomaton aut;

  protected ASTState s1, s2, s3;

  protected ASTTransition t1, t2;

  @BeforeEach
  public void setUp() {
    net = new ReteNetwork();
    aut = new ASTAutomaton("aut");
    s1 = new ASTState("s1");
    s2 = new ASTState("s2");
    s3 = new ASTState("s3");
    t1 = new ASTTransition("t1");
    t2 = new ASTTransition("t2");
  }

  /**
   * @return a snapshot of the current matches of the production
   */
  protected static Set<Tuple> tuples(ProductionNode production) {
    return new HashSet<>(production.getMatchTuples());
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
   * Builds the pattern {@code (aut, tr, src)}: {@code tr} is a transition of
   * {@code aut} with source state {@code src}.
   */
  protected ProductionNode buildTransitionSourcePattern() {
    FeatureInputNode<ASTAutomaton, ASTTransition> transitions = net.featureInput(TRANSITIONS);
    FeatureInputNode<ASTTransition, ASTState> source = net.featureInput(SOURCE);
    JoinNode join = net.join(net.indexer(transitions, 1), net.indexer(source, 0));
    return net.production(join, "aut", "tr", "src");
  }

  // ---------------------------------------------------------------------------
  // Input nodes
  // ---------------------------------------------------------------------------

  @Test
  public void testTypeInputDispatchesToSupertypes() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    TypeInputNode<ASTNamedElement> named = net.typeInput(ASTNamedElement.class);
    TypeInputNode<ASTNode> all = net.typeInput(ASTNode.class);
    ProductionNode namedElements = net.production(named, "n");
    assertSame(states, net.typeInput(ASTState.class));

    net.nodeAdded(aut);
    net.nodeAdded(s1);
    net.nodeAdded(t1);
    assertEquals(1, states.size());
    assertEquals(2, named.size());
    assertEquals(3, all.size());
    assertTrue(namedElements.hasMatch(s1));
    assertTrue(namedElements.hasMatch(aut));
    assertFalse(namedElements.hasMatch(t1));

    net.nodeRemoved(s1);
    assertEquals(0, states.size());
    assertEquals(Set.of(Tuple.of(aut)), tuples(namedElements));
  }

  /**
   * Facts without matching input node must neither fail nor be checked.
   */
  @Test
  public void testIrrelevantFactsAreIgnored() {
    net.typeInput(ASTState.class);
    assertTrue(net.isRelevant(s1));
    assertFalse(net.isRelevant(t1));
    assertFalse(net.isRelevant(NAME));
    net.nodeAdded(t1);
    net.nodeRemoved(t1);
    net.featureValueAdded(NAME, s1, "x");
    net.featureValueRemoved(NAME, s1, "x");
  }

  @Test
  public void testFeatureValuesFormMultiset() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    net.featureValueAdded(NAME, s1, "x");
    net.featureValueAdded(NAME, s1, "x");
    assertEquals(2, names.count(s1, "x"));
    net.featureValueRemoved(NAME, s1, "x");
    assertEquals(1, names.count(s1, "x"));
  }

  // ---------------------------------------------------------------------------
  // Joins
  // ---------------------------------------------------------------------------

  @Test
  public void testJoin() {
    ProductionNode p = buildTransitionSourcePattern();
    RecordingListener listener = new RecordingListener();
    p.addMatchListener(listener);

    net.featureValueAdded(TRANSITIONS, aut, t1);
    net.featureValueAdded(SOURCE, t1, s1);
    net.featureValueAdded(SOURCE, t2, s2);
    assertEquals(Set.of(Tuple.of(aut, t1, s1)), tuples(p));
    assertEquals(List.of("+{aut=aut, tr=t1, src=s1}"), listener.events);

    net.featureValueAdded(TRANSITIONS, aut, t2);
    assertEquals(2, p.countMatches());

    net.featureValueRemoved(SOURCE, t1, s1);
    assertEquals(Set.of(Tuple.of(aut, t2, s2)), tuples(p));
    assertEquals(Set.copyOf(p.getAllMatches()), listener.matches);
  }

  /**
   * Joining an {@code ASTNode} column with an {@code ASTState} column yields
   * an {@code ASTState} column.
   */
  @Test
  public void testJoinColumnTypesAreNarrowed() {
    TypeInputNode<ASTNode> all = net.typeInput(ASTNode.class);
    FeatureInputNode<ASTTransition, ASTState> source = net.featureInput(SOURCE);
    JoinNode join = net.join(net.indexer(all, 0), net.indexer(source, 1));
    assertEquals(List.of(ASTState.class, ASTTransition.class), join.getColumnTypes());
  }

  @Test
  public void testJoinWithIncompatibleKeyTypesIsRejected() {
    TypeInputNode<ASTAutomaton> automata = net.typeInput(ASTAutomaton.class);
    TypeInputNode<ASTTransition> transitions = net.typeInput(ASTTransition.class);
    assertThrows(IllegalArgumentException.class,
        () -> net.join(net.indexer(automata, 0), net.indexer(transitions, 0)));
    // interfaces may always overlap
    TypeInputNode<ASTNamedElement> named = net.typeInput(ASTNamedElement.class);
    net.join(net.indexer(named, 0), net.indexer(transitions, 0));
  }

  /**
   * A join of an indexer with itself must count each pair exactly once. The
   * count aggregation detects duplicates hidden by the set semantics of
   * productions.
   */
  @Test
  public void testSelfJoinOnSharedIndexerCountsPairsOnce() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    Indexer byName = net.indexer(names, 1);
    JoinNode sameName = net.join(byName, byName); // [e1, name, e2]
    AggregatorNode count = net.count(sameName);
    ProductionNode duplicates = net.production(
        net.project(net.inequality(sameName, 0, 2), 0, 2), "a", "b");

    net.featureValueAdded(NAME, s1, "x");
    net.featureValueAdded(NAME, s2, "x");
    net.featureValueAdded(NAME, s3, "y");
    assertEquals(5L, count.getResult(Tuple.EMPTY));
    assertEquals(Set.of(Tuple.of(s1, s2), Tuple.of(s2, s1)), tuples(duplicates));

    net.featureValueChanged(NAME, s3, "y", "x");
    assertEquals(9L, count.getResult(Tuple.EMPTY));
    assertEquals(6, duplicates.countMatches());

    net.featureValueRemoved(NAME, s1, "x");
    assertEquals(4L, count.getResult(Tuple.EMPTY));
    assertEquals(Set.of(Tuple.of(s2, s3), Tuple.of(s3, s2)), tuples(duplicates));
    assertEquals(contents(sameName).size(), 4);
  }

  @Test
  public void testAntiJoin() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    FeatureInputNode<ASTTransition, ASTState> target = net.featureInput(TARGET);
    ExistenceNode unreached = net.antiJoin(net.indexer(states, 0), net.indexer(target, 1));
    ProductionNode p = net.production(unreached, "s");
    RecordingListener listener = new RecordingListener();
    p.addMatchListener(listener);

    net.nodeAdded(s1);
    net.nodeAdded(s2);
    assertEquals(Set.of(Tuple.of(s1), Tuple.of(s2)), tuples(p));

    net.featureValueAdded(TARGET, t1, s2);
    net.featureValueAdded(TARGET, t2, s2);
    assertEquals(Set.of(Tuple.of(s1)), tuples(p));

    net.featureValueRemoved(TARGET, t1, s2);
    assertEquals(Set.of(Tuple.of(s1)), tuples(p));
    net.featureValueRemoved(TARGET, t2, s2);
    assertEquals(Set.of(Tuple.of(s1), Tuple.of(s2)), tuples(p));
    assertEquals(Set.copyOf(p.getAllMatches()), listener.matches);
  }

  @Test
  public void testSemiJoin() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    FeatureInputNode<ASTTransition, ASTState> target = net.featureInput(TARGET);
    ProductionNode reached = net.production(
        net.semiJoin(net.indexer(states, 0), net.indexer(target, 1)), "s");

    net.nodeAdded(s1);
    net.nodeAdded(s2);
    net.featureValueAdded(TARGET, t1, s2);
    net.featureValueAdded(TARGET, t2, s2);
    assertEquals(Set.of(Tuple.of(s2)), tuples(reached));
    net.featureValueRemoved(TARGET, t1, s2);
    assertEquals(Set.of(Tuple.of(s2)), tuples(reached));
    net.featureValueRemoved(TARGET, t2, s2);
    assertTrue(tuples(reached).isEmpty());
  }

  @Test
  public void testExistenceNodeRequiresDifferentIndexers() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    Indexer indexer = net.indexer(states, 0);
    assertThrows(IllegalArgumentException.class, () -> net.antiJoin(indexer, indexer));
  }

  // ---------------------------------------------------------------------------
  // Filter, eval, projection, union, distinct
  // ---------------------------------------------------------------------------

  @Test
  public void testFilterAndEval() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    TupleSource withLength = net.eval(names, t -> t.get(1, String.class).length(), int.class);
    assertEquals(Integer.class, withLength.getColumnTypes().get(2));
    ProductionNode longNames = net.production(
        net.project(net.filter(withLength, t -> t.get(2, Integer.class) > 3), 0), "e");

    net.featureValueAdded(NAME, s1, "abc");
    net.featureValueAdded(NAME, s2, "abcd");
    assertEquals(Set.of(Tuple.of(s2)), tuples(longNames));
    net.featureValueChanged(NAME, s1, "abc", "abcde");
    assertEquals(Set.of(Tuple.of(s1), Tuple.of(s2)), tuples(longNames));
  }

  @Test
  public void testEvalResultTypeIsChecked() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    net.eval(names, t -> t.get(1), Integer.class);
    assertThrows(ClassCastException.class, () -> net.featureValueAdded(NAME, s1, "x"));
  }

  /**
   * Tuples for which the eval function returns {@code null} are dropped on
   * insertion and deletion alike.
   */
  @Test
  public void testEvalNullResultDropsTuple() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    TupleSource eval = net.eval(names, t -> t.get(1).equals("x") ? null : "y", String.class);
    net.featureValueAdded(NAME, s1, "x");
    net.featureValueAdded(NAME, s2, "z");
    assertEquals(HashMultiset.create(List.of(Tuple.of(s2, "z", "y"))), contents(eval));
    net.featureValueRemoved(NAME, s1, "x");
    net.featureValueRemoved(NAME, s2, "z");
    assertTrue(contents(eval).isEmpty());
  }

  @Test
  public void testUnionAndDistinct() {
    FeatureInputNode<ASTTransition, ASTState> source = net.featureInput(SOURCE);
    FeatureInputNode<ASTTransition, ASTState> target = net.featureInput(TARGET);
    TupleSource union = net.union(net.project(source, 1), net.project(target, 1));
    TupleSource distinct = net.distinct(union);
    ProductionNode connected = net.production(distinct, "s");

    net.featureValueAdded(SOURCE, t1, s1);
    net.featureValueAdded(TARGET, t1, s2);
    net.featureValueAdded(SOURCE, t2, s2);
    assertEquals(3, contents(union).size());
    assertEquals(2, contents(distinct).size());
    assertEquals(Set.of(Tuple.of(s1), Tuple.of(s2)), tuples(connected));

    net.featureValueRemoved(TARGET, t1, s2);
    assertEquals(Set.of(Tuple.of(s1), Tuple.of(s2)), tuples(connected));
    net.featureValueRemoved(SOURCE, t2, s2);
    assertEquals(Set.of(Tuple.of(s1)), tuples(connected));
  }

  /**
   * The column types of a union are common AST supertypes, not {@code Object}.
   */
  @Test
  public void testUnionColumnTypes() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    TypeInputNode<ASTAutomaton> automata = net.typeInput(ASTAutomaton.class);
    TypeInputNode<ASTTransition> transitions = net.typeInput(ASTTransition.class);
    // ASTBase is the closest common superclass of ASTState and ASTAutomaton
    assertTrue(ASTNode.class.isAssignableFrom(
        net.union(states, automata).getColumnTypes().get(0)));
    assertTrue(ASTNode.class.isAssignableFrom(
        net.union(states, transitions).getColumnTypes().get(0)));
  }

  // ---------------------------------------------------------------------------
  // Aggregation
  // ---------------------------------------------------------------------------

  @Test
  public void testCount() {
    FeatureInputNode<ASTTransition, ASTState> source = net.featureInput(SOURCE);
    AggregatorNode outgoing = net.count(source, 1);
    assertEquals(List.of(ASTState.class, Long.class), outgoing.getColumnTypes());

    net.featureValueAdded(SOURCE, t1, s1);
    net.featureValueAdded(SOURCE, t2, s1);
    assertEquals(2L, outgoing.getResult(Tuple.of(s1)));
    assertEquals(HashMultiset.create(List.of(Tuple.of(s1, 2L))), contents(outgoing));

    net.featureValueRemoved(SOURCE, t1, s1);
    assertEquals(1L, outgoing.getResult(Tuple.of(s1)));
    net.featureValueRemoved(SOURCE, t2, s1);
    assertNull(outgoing.getResult(Tuple.of(s1)));
    assertTrue(contents(outgoing).isEmpty());
  }

  @Test
  public void testMinMax() {
    FeatureInputNode<ASTAutomaton, ASTState> states = net.featureInput(STATES);
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    JoinNode stateNames = net.join(net.indexer(states, 1), net.indexer(names, 0)); // [aut, s, n]
    AggregatorNode min = net.aggregate(stateNames, TupleMask.of(3, 0), 2, Aggregators.min());
    AggregatorNode max = net.aggregate(stateNames, TupleMask.of(3, 0), 2, Aggregators.max());
    assertEquals(String.class, min.getColumnTypes().get(1));

    net.featureValueAdded(STATES, aut, s1);
    net.featureValueAdded(STATES, aut, s2);
    net.featureValueAdded(NAME, s1, "m");
    net.featureValueAdded(NAME, s2, "k");
    assertEquals("k", min.getResult(Tuple.of(aut)));
    assertEquals("m", max.getResult(Tuple.of(aut)));

    net.featureValueChanged(NAME, s2, "k", "z");
    assertEquals("m", min.getResult(Tuple.of(aut)));
    assertEquals("z", max.getResult(Tuple.of(aut)));
    assertEquals(HashMultiset.create(List.of(Tuple.of(aut, "m"))), contents(min));
  }

  @Test
  public void testSum() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    TupleSource lengths = net.eval(names, t -> t.get(1, String.class).length(), Integer.class);
    AggregatorNode sum = net.aggregate(lengths, TupleMask.empty(3), 2, Aggregators.sumLong());
    net.featureValueAdded(NAME, s1, "ab");
    net.featureValueAdded(NAME, s2, "abc");
    assertEquals(5L, sum.getResult(Tuple.EMPTY));
    net.featureValueRemoved(NAME, s1, "ab");
    assertEquals(3L, sum.getResult(Tuple.EMPTY));
  }

  // ---------------------------------------------------------------------------
  // Transitive closure
  // ---------------------------------------------------------------------------

  /**
   * Builds the pattern {@code (from, to)}: state {@code to} is reachable
   * from state {@code from} via one or more transitions.
   */
  protected ProductionNode buildReachabilityPattern() {
    FeatureInputNode<ASTTransition, ASTState> source = net.featureInput(SOURCE);
    FeatureInputNode<ASTTransition, ASTState> target = net.featureInput(TARGET);
    JoinNode edges = net.join(net.indexer(source, 0), net.indexer(target, 0)); // [t, src, tgt]
    return net.production(net.transitiveClosure(net.project(edges, 1, 2)), "from", "to");
  }

  /**
   * Reports source and target of a new transition within one transaction.
   */
  protected void addTransition(ASTTransition t, ASTState from, ASTState to) {
    net.transaction(() -> {
      net.featureValueAdded(SOURCE, t, from);
      net.featureValueAdded(TARGET, t, to);
    });
  }

  /**
   * Reports the removal of source and target of a transition within one
   * transaction.
   */
  protected void removeTransition(ASTTransition t, ASTState from, ASTState to) {
    net.transaction(() -> {
      net.featureValueRemoved(SOURCE, t, from);
      net.featureValueRemoved(TARGET, t, to);
    });
  }

  /**
   * Covers chains, cycles, parallel edges and the removal of edges on and
   * off cycles.
   */
  @Test
  public void testTransitiveClosure() {
    ProductionNode reachable = buildReachabilityPattern();
    RecordingListener listener = new RecordingListener();
    reachable.addMatchListener(listener);
    ASTTransition t3 = new ASTTransition("t3");
    ASTTransition t4 = new ASTTransition("t4");

    addTransition(t1, s1, s2);
    addTransition(t2, s2, s3);
    assertEquals(Set.of(Tuple.of(s1, s2), Tuple.of(s2, s3), Tuple.of(s1, s3)),
        tuples(reachable));

    // cycle
    addTransition(t3, s3, s1);
    assertEquals(9, reachable.countMatches());

    // parallel edge keeps reachability
    addTransition(t4, s1, s2);
    removeTransition(t1, s1, s2);
    assertEquals(9, reachable.countMatches());

    removeTransition(t3, s3, s1);
    assertEquals(Set.of(Tuple.of(s1, s2), Tuple.of(s2, s3), Tuple.of(s1, s3)),
        tuples(reachable));

    removeTransition(t2, s2, s3);
    assertEquals(Set.of(Tuple.of(s1, s2)), tuples(reachable));
    assertEquals(Set.copyOf(reachable.getAllMatches()), listener.matches);
  }

  @Test
  public void testTransitiveClosureSelfLoop() {
    ProductionNode reachable = buildReachabilityPattern();
    addTransition(t1, s1, s1);
    assertEquals(Set.of(Tuple.of(s1, s1)), tuples(reachable));
    removeTransition(t1, s1, s1);
    assertTrue(tuples(reachable).isEmpty());
  }

  // ---------------------------------------------------------------------------
  // Matcher API
  // ---------------------------------------------------------------------------

  /**
   * Queries with unbound, partially bound and fully bound parameters, and
   * maintenance of the lazily created query indexes.
   */
  @Test
  public void testPartialBindingQueries() {
    ProductionNode p = buildTransitionSourcePattern();
    ASTAutomaton aut2 = new ASTAutomaton("aut2");
    net.featureValueAdded(TRANSITIONS, aut, t1);
    net.featureValueAdded(TRANSITIONS, aut2, t2);
    net.featureValueAdded(SOURCE, t1, s1);
    net.featureValueAdded(SOURCE, t2, s1);

    assertEquals(2, p.countMatches());
    assertEquals(2, p.countMatches(null, null, s1));
    assertEquals(1, p.countMatches(aut, null, null));
    assertTrue(p.hasMatch(aut, t1, s1));
    assertFalse(p.hasMatch(aut, t2, s1));
    Match match = p.getOneArbitraryMatch(aut2, null, null).orElseThrow();
    assertSame(t2, match.get("tr"));
    assertSame(s1, match.get("src", ASTState.class));
    assertEquals(List.of("aut", "tr", "src"), match.getParameterNames());

    // the index created by the query above is maintained
    net.featureValueRemoved(SOURCE, t2, s1);
    assertFalse(p.hasMatch(aut2, null, null));
    assertEquals(1, p.countMatches(null, null, s1));
    assertThrows(IllegalArgumentException.class, () -> p.countMatches(aut));
    assertThrows(IllegalArgumentException.class, () -> match.get("unknown"));
  }

  @Test
  public void testProductionRequiresAstColumns() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    assertThrows(IllegalArgumentException.class, () -> net.production(names, "e", "name"));
    assertThrows(IllegalArgumentException.class, () -> net.production(names, "e"));
    TupleSource elements = net.project(names, 0);
    assertThrows(IllegalArgumentException.class, () -> net.production(elements, "e", "e2"));
    net.production(elements, "e");
  }

  /**
   * A listener registered with {@code fireForExistingMatches} immediately
   * learns about matches that existed before its registration.
   */
  @Test
  public void testFireForExistingMatches() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    ProductionNode p = net.production(states, "s");
    net.nodeAdded(s1);
    RecordingListener listener = new RecordingListener();
    p.addMatchListener(listener, true);
    assertEquals(Set.of(new Match(p, Tuple.of(s1))), listener.matches);
  }

  // ---------------------------------------------------------------------------
  // Transactions and events
  // ---------------------------------------------------------------------------

  /**
   * Within (nested) transactions, queries are up to date, but events are
   * only delivered at the end of the outermost transaction.
   */
  @Test
  public void testEventsAreDeliveredAfterTransaction() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    ProductionNode p = net.production(states, "s");
    RecordingListener listener = new RecordingListener();
    p.addMatchListener(listener);

    net.transaction(() -> {
      net.nodeAdded(s1);
      // the matcher is already up to date
      assertTrue(p.hasMatch(s1));
      assertTrue(listener.events.isEmpty());
      net.transaction(() -> net.nodeAdded(s2));
      assertTrue(listener.events.isEmpty());
    });
    assertEquals(2, listener.events.size());
  }

  /**
   * A match appearing and disappearing (or vice versa) within one
   * transaction causes no events.
   */
  @Test
  public void testCancellingChangesAreNotReported() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    ProductionNode p = net.production(states, "s");
    net.nodeAdded(s2);
    RecordingListener listener = new RecordingListener();
    p.addMatchListener(listener, true);
    listener.events.clear();

    net.transaction(() -> {
      net.nodeAdded(s1);
      net.nodeRemoved(s1);
      net.nodeRemoved(s2);
      net.nodeAdded(s2);
    });
    assertTrue(listener.events.isEmpty());
  }

  /**
   * When a listener of one production is notified, other productions
   * already reflect the same change.
   */
  @Test
  public void testListenersSeeConsistentState() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    TypeInputNode<ASTNamedElement> named = net.typeInput(ASTNamedElement.class);
    ProductionNode p1 = net.production(states, "s");
    ProductionNode p2 = net.production(named, "n");
    List<Boolean> observed = new java.util.ArrayList<>();
    p1.addMatchListener(new MatchListener() {
      @Override
      public void matchAppeared(Match match) {
        observed.add(p2.hasMatch(match.get(0)));
      }

      @Override
      public void matchDisappeared(Match match) {
        observed.add(!p2.hasMatch(match.get(0)));
      }
    });
    net.nodeAdded(s1);
    net.nodeRemoved(s1);
    assertEquals(List.of(true, true), observed);
  }

  /**
   * Changes reported by a listener are propagated, and the resulting events
   * are delivered within the same delivery loop.
   */
  @Test
  public void testListenersMayReportChanges() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    ProductionNode p1 = net.production(states, "s");
    ProductionNode named = net.production(net.project(names, 0), "e");
    RecordingListener namedListener = new RecordingListener();
    named.addMatchListener(namedListener);
    p1.addMatchListener(new MatchListener() {
      @Override
      public void matchAppeared(Match match) {
        net.featureValueAdded(NAME, match.get(0, ASTState.class), "generated");
      }

      @Override
      public void matchDisappeared(Match match) {
      }
    });
    net.nodeAdded(s1);
    assertTrue(named.hasMatch(s1));
    assertEquals(Set.of(new Match(named, Tuple.of(s1))), namedListener.matches);
  }

  // ---------------------------------------------------------------------------
  // Network construction
  // ---------------------------------------------------------------------------

  /**
   * Inner nodes created after facts were reported initialize themselves from
   * their parents and are maintained incrementally afterwards.
   */
  @Test
  public void testNodesAddedLaterAreInitialized() {
    FeatureInputNode<ASTAutomaton, ASTTransition> transitions = net.featureInput(TRANSITIONS);
    FeatureInputNode<ASTTransition, ASTState> source = net.featureInput(SOURCE);
    net.featureValueAdded(TRANSITIONS, aut, t1);
    net.featureValueAdded(TRANSITIONS, aut, t2);
    net.featureValueAdded(SOURCE, t1, s1);

    JoinNode join = net.join(net.indexer(transitions, 1), net.indexer(source, 0));
    ProductionNode p = net.production(join, "aut", "tr", "src");
    AggregatorNode count = net.count(join, 0);
    assertEquals(Set.of(Tuple.of(aut, t1, s1)), tuples(p));
    assertEquals(1L, count.getResult(Tuple.of(aut)));

    net.featureValueAdded(SOURCE, t2, s2);
    assertEquals(2, p.countMatches());
    assertEquals(2L, count.getResult(Tuple.of(aut)));
  }

  @Test
  public void testInputCreationAfterFactsIsRejected() {
    net.typeInput(ASTState.class);
    net.nodeAdded(s1);
    assertThrows(IllegalStateException.class, () -> net.typeInput(ASTAutomaton.class));
    assertThrows(IllegalStateException.class, () -> net.featureInput(NAME));
    // existing input nodes are still returned
    assertNotNull(net.typeInput(ASTState.class));
  }

  /**
   * Nodes can only be disposed bottom-up and are detached from their parents.
   */
  @Test
  public void testDispose() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    FeatureInputNode<ASTTransition, ASTState> target = net.featureInput(TARGET);
    Indexer left = net.indexer(states, 0);
    Indexer right = net.indexer(target, 1);
    ExistenceNode anti = net.antiJoin(left, right);
    ProductionNode p = net.production(anti, "s");

    assertThrows(IllegalStateException.class, anti::dispose);
    assertThrows(IllegalStateException.class, left::dispose);
    p.dispose();
    anti.dispose();
    left.dispose();
    right.dispose();
    assertTrue(states.getChildren().isEmpty());
    assertTrue(target.getChildren().isEmpty());
    net.nodeAdded(s1);
    assertFalse(p.hasMatch(s1));
  }

  // ---------------------------------------------------------------------------
  // Consistency checks
  // ---------------------------------------------------------------------------

  @Test
  public void testInvalidNotificationsAreRejected() {
    net.typeInput(ASTState.class);
    net.featureInput(NAME);
    net.nodeAdded(s1);
    assertThrows(IllegalStateException.class, () -> net.nodeAdded(s1));
    assertThrows(IllegalStateException.class, () -> net.nodeRemoved(s2));
    assertThrows(IllegalStateException.class, () -> net.featureValueRemoved(NAME, s1, "x"));
    assertThrows(NullPointerException.class, () -> net.featureValueAdded(NAME, s1, null));
  }

  /**
   * Feature facts bypassing the compile-time check via raw types are rejected.
   */
  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testFeatureTypesAreCheckedAtRuntime() {
    net.featureInput(SOURCE);
    Feature raw = SOURCE;
    assertThrows(ClassCastException.class, () -> net.featureValueAdded(raw, t1, t2));
  }

  /**
   * A filter function reporting changes is rejected, and the network leaves
   * the propagation state afterwards.
   */
  @Test
  public void testNetworkCannotBeModifiedDuringPropagation() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    net.filter(states, t -> {
      net.nodeAdded(s2);
      return true;
    });
    assertThrows(IllegalStateException.class, () -> net.nodeAdded(s1));
    assertFalse(net.isPropagating());
  }

  @Test
  public void testInputNodesCannotBeModifiedDirectly() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    assertThrows(IllegalStateException.class, () -> states.insert(s1));
  }
}
