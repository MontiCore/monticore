/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.example;

import de.monticore.rete.ReteNetwork;
import de.monticore.rete.example.simpleautomata._rete.SimpleAutomataFullReteFactEmitter;
import de.monticore.rete.example.simpleautomata.SimpleAutomataMill;
import de.monticore.rete.example.simpleautomata._ast.ASTAutomaton;
import de.monticore.rete.example.simpleautomata._ast.ASTState;
import de.monticore.rete.example.simpleautomata._ast.ASTTransition;
import de.monticore.rete.nodes.Indexer;
import de.monticore.rete.nodes.JoinNode;
import de.monticore.rete.nodes.ProductionNode;
import de.monticore.rete.nodes.TupleSource;
import de.monticore.rete.tuple.Tuple;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import static de.monticore.rete.example.simpleautomata._rete.SimpleAutomataReteFeatures.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Measures the time needed to initialize and incrementally maintain complex
 * patterns on large automata. Each state of the models has 5 outgoing
 * transitions to random targets (see {@code src/test/resources/models}):
 * {@code Large.aut} has 1000 states and {@code Huge.aut} has 10000 states..
 * <p>
 * Measured patterns:
 * <ul>
 *   <li>two-step paths {@code (s1)-t1->(s2)-t2->(s3)} (25 per state),</li>
 *   <li>triangles {@code (s1)-t1->(s2)-t2->(s3)-t3->(s1)} (few matches, but
 *   large intermediate results),</li>
 *   <li>diamonds without shortcut: two two-step paths
 *   {@code (s1)-t1->(s2)-t2->(s4)} and {@code (s1)-t3->(s3)-t4->(s4)} via
 *   different states {@code s2 != s3}, where no transition leads from
 *   {@code s1} to {@code s4} directly (self join, filter and anti join).</li>
 * </ul>
 * All results are checked against a naive computation on the AST. The
 * measured times are printed to stdout (run Gradle with
 * {@code -PshowTestOutput=true} to see them); they are rough single-run
 * numbers including JIT warm-up and are not asserted.
 */
@TestWithMCLanguage(SimpleAutomataMill.class)
public class PerformanceTest {

  protected static final String MODEL_DIR = "src/test/resources/models/";

  protected static final int INITIALIZATION_RUNS = 3;

  protected static final int CHANGES = 500;

  protected ASTAutomaton automaton;

  /**
   * The productions of the measured patterns
   */
  protected static class Patterns {

    /**
     * (source, transition, target)
     */
    protected ProductionNode edges;

    /**
     * (s1, t1, s2, t2, s3)
     */
    protected ProductionNode twoStepPaths;

    /**
     * (s1, t1, s2, t2, s3, t3), each cycle appears once per rotation
     */
    protected ProductionNode triangles;

    /**
     * (s1, t1, s2, t2, s4, t3, s3, t4), each diamond appears twice (with
     * both paths swapped)
     */
    protected ProductionNode diamonds;
  }

  /**
   * The expected matches of all patterns, computed naively on the AST
   */
  protected static class Expected {

    protected final Set<Tuple> edges = new HashSet<>();

    protected final Set<Tuple> twoStepPaths = new HashSet<>();

    protected final Set<Tuple> triangles = new HashSet<>();

    protected final Set<Tuple> diamonds = new HashSet<>();
  }

  /**
   * Parses the given model and reports the parsing time. Tests for models
   * not available in the model directory (e.g. {@code Giant.aut}, which is
   * not part of the repository) are skipped.
   */
  protected void parse(String model) throws IOException {
    assumeTrue(Files.exists(Paths.get(MODEL_DIR + model)), "Model " + model + " not available");
    long start = System.nanoTime();
    automaton = SimpleAutomataMill.parser().parse(MODEL_DIR + model).orElseThrow();
    report("parse model", System.nanoTime() - start);
  }

  /**
   * Builds the measured patterns on top of the transition pattern of
   * {@link TransitionPatternTest}. The node {@code [source, transition,
   * target]} and its indexers are shared by all patterns, the two-step paths
   * are shared by the triangles and diamonds.
   */
  protected static Patterns buildPatterns(ReteNetwork net) {
    Patterns p = new Patterns();
    p.edges = TransitionPatternTest.buildTransitionPattern(net);
    TupleSource edges = p.edges.getParent(); // [src, t, tgt]
    Indexer edgesBySourceAndTarget = net.indexer(edges, 0, 2);

    // [s1, t1, s2, t2, s3]: edge (s1, t1, s2) joined with edge (s2, t2, s3)
    JoinNode twoStep = net.join(net.indexer(edges, 2), net.indexer(edges, 0));
    p.twoStepPaths = net.production(twoStep, "s1", "t1", "s2", "t2", "s3");

    // [s1, t1, s2, t2, s3, t3]: two-step path closed by an edge (s3, t3, s1)
    JoinNode triangle = net.join(net.indexer(twoStep, 4, 0), edgesBySourceAndTarget);
    p.triangles = net.production(triangle, "s1", "t1", "s2", "t2", "s3", "t3");

    // [s1, t1, s2, t2, s4, t3, s3, t4]: pairs of two-step paths with the same
    // start and end state (self join on a shared indexer) ...
    Indexer pathsByStartAndEnd = net.indexer(twoStep, 0, 4);
    JoinNode pathPairs = net.join(pathsByStartAndEnd, pathsByStartAndEnd);
    // ... via different intermediate states ...
    TupleSource differentPaths = net.inequality(pathPairs, 2, 6);
    // ... without a direct edge from start to end
    TupleSource noShortcut = net.antiJoin(net.indexer(differentPaths, 0, 4),
        edgesBySourceAndTarget);
    p.diamonds = net.production(noShortcut, "s1", "t1", "s2", "t2", "s4", "t3", "s3", "t4");
    return p;
  }

  /**
   * Builds the patterns in a new network and initializes it with the model.
   */
  protected Patterns initializedPatterns(ReteNetwork net, SimpleAutomataFullReteFactEmitter emitter) {
    Patterns p = buildPatterns(net);
    emitter.addSubtree(automaton);
    return p;
  }

  // ---------------------------------------------------------------------------
  // Tests
  // ---------------------------------------------------------------------------

  /**
   * Measures building the (empty) network and initializing it with the
   * whole model, repeated in fresh networks to show the effect of JIT
   * warm-up. Also reports the approximate heap usage of the network.
   */
  @ParameterizedTest(name = "{0}")
  @ValueSource(strings = {"Large.aut", "Huge.aut"})
  public void testInitialization(String model) throws IOException {
    parse(model);
    Patterns p = null;
    long heapBefore = 0;
    long heapAfter = 0;
    for (int run = 1; run <= INITIALIZATION_RUNS; run++) {
      p = null;
      heapBefore = usedHeap();
      ReteNetwork net = new ReteNetwork();
      p = measure("run " + run + ": build network", () -> buildPatterns(net));
      SimpleAutomataFullReteFactEmitter emitter = new SimpleAutomataFullReteFactEmitter(net);
      measure("run " + run + ": initialize with model", () -> {
        emitter.addSubtree(automaton);
        return null;
      });
      heapAfter = usedHeap();
    }
    System.out.printf("[PerformanceTest] %-40s %10.1f MB (approx.)%n", "heap used by network",
        (heapAfter - heapBefore) / 1e6);
    System.out.printf("[PerformanceTest] matches: %d edges, %d two-step paths, %d triangles, "
            + "%d diamonds%n", p.edges.countMatches(), p.twoStepPaths.countMatches(),
        p.triangles.countMatches(), p.diamonds.countMatches());
    int states = automaton.getStateList().size();
    assertEquals(5 * states, p.edges.countMatches());
    assertEquals(25 * states, p.twoStepPaths.countMatches());
    assertMatchesExpected(p);
  }

  /**
   * Measures incremental updates: redirecting transitions, renaming states
   * (and renaming them back) and adding/removing transitions.
   */
  @ParameterizedTest(name = "{0}")
  @ValueSource(strings = {"Large.aut", "Huge.aut"})
  public void testIncrementalChanges(String model) throws IOException {
    parse(model);
    ReteNetwork net = new ReteNetwork();
    SimpleAutomataFullReteFactEmitter emitter = new SimpleAutomataFullReteFactEmitter(net);
    Patterns p = measure("initialize with model", () -> initializedPatterns(net, emitter));
    Random random = new Random(1);
    List<ASTState> states = automaton.getStateList();
    List<ASTTransition> transitions = automaton.getTransitionList();

    // redirect random transitions to random states
    long start = System.nanoTime();
    for (int i = 0; i < CHANGES; i++) {
      ASTTransition t = transitions.get(random.nextInt(transitions.size()));
      String oldTarget = t.getTo();
      String newTarget = states.get(random.nextInt(states.size())).getName();
      t.setTo(newTarget);
      net.featureValueChanged(TRANSITION_TO, t, oldTarget, newTarget);
    }
    reportAverage("redirect transition", "change", CHANGES, System.nanoTime() - start);
    assertMatchesExpected(p);

    // rename random states, which disconnects all their transitions
    Map<ASTState, String> originalNames = new LinkedHashMap<>();
    start = System.nanoTime();
    for (int i = 0; i < CHANGES; i++) {
      ASTState s = states.get(random.nextInt(states.size()));
      if (originalNames.containsKey(s)) {
        continue;
      }
      originalNames.put(s, s.getName());
      s.setName("Renamed" + i);
      net.featureValueChanged(STATE_NAME, s, originalNames.get(s), s.getName());
    }
    reportAverage("rename state", "change", originalNames.size(), System.nanoTime() - start);
    assertMatchesExpected(p);

    // ... and rename them back, which reconnects the transitions
    start = System.nanoTime();
    for (Map.Entry<ASTState, String> entry : originalNames.entrySet()) {
      ASTState s = entry.getKey();
      String oldName = s.getName();
      s.setName(entry.getValue());
      net.featureValueChanged(STATE_NAME, s, oldName, s.getName());
    }
    reportAverage("rename state back", "change", originalNames.size(),
        System.nanoTime() - start);
    assertMatchesExpected(p);

    // add new transitions between random states
    List<ASTTransition> added = new ArrayList<>();
    start = System.nanoTime();
    for (int i = 0; i < CHANGES; i++) {
      ASTTransition t = SimpleAutomataMill.transitionBuilder()
          .setFrom(states.get(random.nextInt(states.size())).getName())
          .setInput("new")
          .setTo(states.get(random.nextInt(states.size())).getName())
          .build();
      automaton.addTransition(t);
      emitter.addSubtree(t, automaton, AUTOMATON_TRANSITIONS);
      added.add(t);
    }
    reportAverage("add transition", "change", CHANGES, System.nanoTime() - start);
    assertMatchesExpected(p);

    // ... and remove them again
    start = System.nanoTime();
    for (ASTTransition t : added) {
      automaton.removeTransition(t);
      emitter.removeSubtree(t, automaton, AUTOMATON_TRANSITIONS);
    }
    reportAverage("remove transition", "change", CHANGES, System.nanoTime() - start);
    assertMatchesExpected(p);
  }

  /**
   * Measures queries with a bound start state (e.g. all two-step paths
   * starting at a given state). The first query creates the index for the
   * bound position.
   */
  @ParameterizedTest(name = "{0}")
  @ValueSource(strings = {"Large.aut", "Huge.aut"})
  public void testQueriesWithBoundParameters(String model) throws IOException {
    parse(model);
    ReteNetwork net = new ReteNetwork();
    Patterns p = initializedPatterns(net, new SimpleAutomataFullReteFactEmitter(net));
    List<ASTState> states = automaton.getStateList();
    Map<ASTState, Integer> expectedPathCounts = new HashMap<>();
    for (Tuple path : computeExpected().twoStepPaths) {
      expectedPathCounts.merge((ASTState) path.get(0), 1, Integer::sum);
    }

    measure("first query (creates index)",
        () -> p.twoStepPaths.getAllMatches(states.get(0), null, null, null, null));
    long start = System.nanoTime();
    int total = 0;
    for (ASTState s : states) {
      int count = p.twoStepPaths.getAllMatches(s, null, null, null, null).size();
      assertEquals(expectedPathCounts.getOrDefault(s, 0), count);
      total += count;
    }
    reportAverage("query paths from state", "query", states.size(), System.nanoTime() - start);
    assertEquals(25 * states.size(), total);
  }

  // ---------------------------------------------------------------------------
  // Naive computation of the expected results on the AST
  // ---------------------------------------------------------------------------

  /**
   * Computes the expected matches of all patterns by resolving the state
   * names of all transitions and enumerating paths via nested loops.
   */
  protected Expected computeExpected() {
    Expected e = new Expected();
    Map<String, List<ASTState>> statesByName = new HashMap<>();
    for (ASTState s : automaton.getStateList()) {
      statesByName.computeIfAbsent(s.getName(), k -> new ArrayList<>()).add(s);
    }

    // edges and outgoing edges per state
    Map<Object, List<Tuple>> outgoing = new HashMap<>();
    Set<Tuple> connected = new HashSet<>();
    for (ASTTransition t : automaton.getTransitionList()) {
      for (ASTState source : statesByName.getOrDefault(t.getFrom(), List.of())) {
        for (ASTState target : statesByName.getOrDefault(t.getTo(), List.of())) {
          Tuple edge = Tuple.of(source, t, target);
          e.edges.add(edge);
          outgoing.computeIfAbsent(source, k -> new ArrayList<>()).add(edge);
          connected.add(Tuple.of(source, target));
        }
      }
    }

    // two-step paths, grouped by start and end state
    Map<Tuple, List<Tuple>> pathsByStartAndEnd = new HashMap<>();
    for (Tuple first : e.edges) {
      for (Tuple second : outgoing.getOrDefault(first.get(2), List.of())) {
        Tuple path = Tuple.of(first.get(0), first.get(1), first.get(2), second.get(1),
            second.get(2));
        e.twoStepPaths.add(path);
        pathsByStartAndEnd.computeIfAbsent(Tuple.of(path.get(0), path.get(4)),
            k -> new ArrayList<>()).add(path);
      }
    }

    // triangles
    for (Tuple path : e.twoStepPaths) {
      for (Tuple closing : outgoing.getOrDefault(path.get(4), List.of())) {
        if (closing.get(2) == path.get(0)) {
          e.triangles.add(path.append(closing.get(1)));
        }
      }
    }

    // diamonds without shortcut
    for (Map.Entry<Tuple, List<Tuple>> group : pathsByStartAndEnd.entrySet()) {
      if (connected.contains(group.getKey())) {
        continue;
      }
      for (Tuple p1 : group.getValue()) {
        for (Tuple p2 : group.getValue()) {
          if (p1.get(2) != p2.get(2)) {
            e.diamonds.add(Tuple.of(p1.get(0), p1.get(1), p1.get(2), p1.get(3), p1.get(4),
                p2.get(1), p2.get(2), p2.get(3)));
          }
        }
      }
    }
    return e;
  }

  /**
   * Asserts that all productions contain exactly the naively computed matches.
   */
  protected void assertMatchesExpected(Patterns p) {
    Expected e = computeExpected();
    assertEquals(e.edges, p.edges.getMatchTuples());
    assertEquals(e.twoStepPaths, p.twoStepPaths.getMatchTuples());
    assertEquals(e.triangles, p.triangles.getMatchTuples());
    assertEquals(e.diamonds, p.diamonds.getMatchTuples());
  }

  // ---------------------------------------------------------------------------
  // Reporting
  // ---------------------------------------------------------------------------

  /**
   * Executes and times the action, reporting the time under the given label.
   *
   * @return the result of the action
   */
  protected static <T> T measure(String label, Supplier<T> action) {
    long start = System.nanoTime();
    T result = action.get();
    report(label, System.nanoTime() - start);
    return result;
  }

  protected static void report(String label, long nanos) {
    System.out.printf("[PerformanceTest] %-40s %10.2f ms%n", label, nanos / 1e6);
  }

  /**
   * Reports the total time of several operations and the average time per operation.
   */
  protected static void reportAverage(String label, String unit, int count, long nanos) {
    System.out.printf("[PerformanceTest] %-40s %10.2f ms total, %8.1f µs per %s (%d)%n",
        label, nanos / 1e6, nanos / 1e3 / Math.max(count, 1), unit, count);
  }

  /**
   * @return the currently used heap after requesting a garbage collection
   * (a rough approximation only)
   */
  protected static long usedHeap() {
    Runtime runtime = Runtime.getRuntime();
    System.gc();
    return runtime.totalMemory() - runtime.freeMemory();
  }
}
