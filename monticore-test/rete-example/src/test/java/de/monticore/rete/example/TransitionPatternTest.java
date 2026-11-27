/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.example;

import de.monticore.rete.Match;
import de.monticore.rete.MatchListener;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.rete.example.simpleautomata._rete.SimpleAutomataReteFeatures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates the Rete based pattern matching on SimpleAutomata ASTs with
 * the pattern {@code (source)-transition->(target)}: {@code transition} is a
 * transition of some automaton whose source and target names refer to the
 * states {@code source} and {@code target} of the same automaton.
 */
@TestWithMCLanguage(SimpleAutomataMill.class)
public class TransitionPatternTest {

  protected static final String PING_PONG = """
      automaton PingPong {
        state Ping <<initial>>;
        state Pong <<final>>;
        Ping - ball > Pong;
        Pong - ball > Ping;
        Pong - stop > Missing;
      }""";

  /**
   * Uses the same state names as {@link #PING_PONG}; its transitions must
   * not be connected to the states of the other automaton.
   */
  protected static final String OTHER = """
      automaton Other {
        state Ping;
        state Pong;
        Ping - serve > Pong;
      }""";

  protected ReteNetwork network;

  protected ProductionNode transitions;

  protected SimpleAutomataFullReteFactEmitter emitter;

  protected ASTAutomaton pingPong;

  protected ASTAutomaton other;

  protected final List<String> events = new ArrayList<>();

  /**
   * Builds the network (input nodes must exist before facts are reported),
   * registers a listener and initializes the network with two parsed models.
   */
  @BeforeEach
  public void setUp() throws IOException {
    network = new ReteNetwork();
    transitions = buildTransitionPattern(network);
    transitions.addMatchListener(new MatchListener() {
      @Override
      public void matchAppeared(Match match) {
        events.add("+" + describe(match));
      }

      @Override
      public void matchDisappeared(Match match) {
        events.add("-" + describe(match));
      }
    });

    pingPong = SimpleAutomataMill.parser().parse_String(PING_PONG).orElseThrow();
    other = SimpleAutomataMill.parser().parse_String(OTHER).orElseThrow();
    emitter = new SimpleAutomataFullReteFactEmitter(network);
    emitter.addSubtree(pingPong);
    emitter.addSubtree(other);
  }

  /**
   * Builds the network of the pattern {@code (source, transition, target)}:
   * <pre>
   * AUTOMATON_TRANSITIONS [a, t]    ⋈ TRANSITION_FROM [t, from]  on t
   *                                 ⋈ TRANSITION_TO   [t, to]    on t
   *                                 ⋈ namedStates [a, src, from] on (a, from)
   *                                 ⋈ namedStates [a, tgt, to]   on (a, to)
   * namedStates = AUTOMATON_STATES [a, s] ⋈ STATE_NAME [s, name] on s
   * </pre>
   * The indexer of {@code namedStates} by (automaton, name) is shared by the
   * joins resolving the source and the target state.
   */
  protected static ProductionNode buildTransitionPattern(ReteNetwork net) {
    TupleSource autTransitions = net.featureInput(AUTOMATON_TRANSITIONS); // [a, t]
    TupleSource from = net.featureInput(TRANSITION_FROM);                 // [t, from]
    TupleSource to = net.featureInput(TRANSITION_TO);                     // [t, to]
    TupleSource autStates = net.featureInput(AUTOMATON_STATES);           // [a, s]
    TupleSource stateNames = net.featureInput(STATE_NAME);                // [s, name]

    // [a, s, name]: the states of each automaton with their names
    JoinNode namedStates = net.join(net.indexer(autStates, 1), net.indexer(stateNames, 0));
    Indexer statesByAutomatonAndName = net.indexer(namedStates, 0, 2);

    // [a, t, from, to]
    JoinNode withFrom = net.join(net.indexer(autTransitions, 1), net.indexer(from, 0));
    JoinNode withTo = net.join(net.indexer(withFrom, 1), net.indexer(to, 0));

    // [a, t, from, to, src]
    JoinNode withSource = net.join(net.indexer(withTo, 0, 2), statesByAutomatonAndName);
    // [a, t, from, to, src, tgt]
    JoinNode withTarget = net.join(net.indexer(withSource, 0, 3), statesByAutomatonAndName);

    return net.production(net.project(withTarget, 4, 1, 5), "source", "transition", "target");
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  /**
   * @return the (first) state of the automaton with the given name
   */
  protected static ASTState state(ASTAutomaton automaton, String name) {
    return automaton.getStateList().stream()
        .filter(s -> s.getName().equals(name)).findFirst().orElseThrow();
  }

  /**
   * @return the (first) transition of the automaton with the given source
   * name and input
   */
  protected static ASTTransition transition(ASTAutomaton automaton, String from, String input) {
    return automaton.getTransitionList().stream()
        .filter(t -> t.getFrom().equals(from) && t.getInput().equals(input))
        .findFirst().orElseThrow();
  }

  /**
   * @return a readable representation of a match, e.g. {@code Ping-ball->Pong}
   */
  protected static String describe(Match match) {
    ASTState source = match.get("source", ASTState.class);
    ASTTransition transition = match.get("transition", ASTTransition.class);
    ASTState target = match.get("target", ASTState.class);
    return source.getName() + "-" + transition.getInput() + "->" + target.getName();
  }

  /**
   * @return the readable representations of all current matches
   */
  protected Set<String> matches() {
    Set<String> result = new HashSet<>();
    transitions.forEachMatch(m -> result.add(describe(m)));
    return result;
  }

  // ---------------------------------------------------------------------------
  // Tests
  // ---------------------------------------------------------------------------

  /**
   * Transitions are only connected to states of the same automaton, and
   * transitions referencing unknown states do not match.
   */
  @Test
  public void testInitialMatches() {
    assertEquals(Set.of("Ping-ball->Pong", "Pong-ball->Ping", "Ping-serve->Pong"), matches());
    assertEquals(2, transitions.countMatches(null, null, state(pingPong, "Ping"))
        + transitions.countMatches(null, null, state(pingPong, "Pong")));
    ASTTransition serve = transition(other, "Ping", "serve");
    assertTrue(transitions.hasMatch(state(other, "Ping"), serve, state(other, "Pong")));
    assertFalse(transitions.hasMatch(state(pingPong, "Ping"), serve, state(pingPong, "Pong")));
    // the listener was registered before the initialization
    assertEquals(3, events.size());
  }

  /**
   * Renaming a state is a single feature change; the network determines all
   * transitions affected by the new and the old name.
   */
  @Test
  public void testRenameState() {
    ASTState pong = state(pingPong, "Pong");
    pong.setName("Missing");
    network.featureValueChanged(STATE_NAME, pong, "Pong", "Missing");
    // all transitions of PingPong now reference an unknown state "Pong"
    assertEquals(Set.of("Ping-serve->Pong"), matches());

    pong.setName("Pong");
    network.featureValueChanged(STATE_NAME, pong, "Missing", "Pong");
    assertEquals(Set.of("Ping-ball->Pong", "Pong-ball->Ping", "Ping-serve->Pong"), matches());
  }

  /**
   * Adding a state is reported as its subtree plus the containment fact of
   * its parent.
   */
  @Test
  public void testAddState() {
    ASTState missing = SimpleAutomataMill.stateBuilder().setName("Missing").build();
    events.clear();
    pingPong.addState(missing);
    emitter.addSubtree(missing, pingPong, AUTOMATON_STATES);
    assertEquals(List.of("+Pong-stop->Missing"), events);
  }

  /**
   * Adding (removing) a transition is reported as its subtree plus the
   * containment fact of its parent, both in one transaction.
   */
  @Test
  public void testAddAndRemoveTransition() {
    ASTTransition loop = SimpleAutomataMill.transitionBuilder()
        .setFrom("Ping").setInput("wait").setTo("Ping").build();

    pingPong.addTransition(loop);
    emitter.addSubtree(loop, pingPong, AUTOMATON_TRANSITIONS);
    assertTrue(matches().contains("Ping-wait->Ping"));

    pingPong.removeTransition(loop);
    emitter.removeSubtree(loop, pingPong, AUTOMATON_TRANSITIONS);
    assertFalse(matches().contains("Ping-wait->Ping"));
    assertEquals(List.of("+Ping-wait->Ping", "-Ping-wait->Ping"), events.subList(3, 5));
  }

  /**
   * Changing the target name of a transition moves its match to the new
   * target state.
   */
  @Test
  public void testRedirectTransition() {
    ASTTransition ball = transition(pingPong, "Ping", "ball");
    ball.setTo("Ping");
    network.featureValueChanged(TRANSITION_TO, ball, "Pong", "Ping");
    assertEquals(Set.of("Ping-ball->Ping", "Pong-ball->Ping", "Ping-serve->Pong"), matches());
  }

  /**
   * A transient change within a transaction causes no events.
   */
  @Test
  public void testTransientChangeIsNotReported() {
    ASTState ping = state(pingPong, "Ping");
    events.clear();
    network.transaction(() -> {
      network.featureValueChanged(STATE_NAME, ping, "Ping", "Tmp");
      assertFalse(matches().contains("Ping-ball->Pong"));
      network.featureValueChanged(STATE_NAME, ping, "Tmp", "Ping");
    });
    assertTrue(events.isEmpty());
  }

  /**
   * Removing a whole model removes all of its matches and leaves the other
   * model untouched.
   */
  @Test
  public void testRemoveAutomaton() {
    emitter.removeSubtree(pingPong);
    assertEquals(Set.of("Ping-serve->Pong"), matches());
    emitter.addSubtree(pingPong);
    assertEquals(3, transitions.countMatches());
  }

  /**
   * Binds the source parameter at query time to find all outgoing
   * transitions of a state. The production creates an index over the bound
   * position on the first query and maintains it incrementally afterwards.
   */
  @Test
  public void testOutgoingTransitionsOfFixedSource() {
    ASTState ping = state(pingPong, "Ping");
    ASTState pong = state(pingPong, "Pong");

    // Pong-stop->Missing is not a match, as its target state does not exist
    List<Match> outgoing = transitions.getAllMatches(pong, null, null);
    assertEquals(List.of("Pong-ball->Ping"), outgoing.stream().map(TransitionPatternTest::describe).toList());
    assertSame(transition(pingPong, "Pong", "ball"), outgoing.get(0).get("transition"));

    // the index is maintained when transitions are added
    ASTTransition back = SimpleAutomataMill.transitionBuilder()
        .setFrom("Pong").setInput("return").setTo("Pong").build();
    pingPong.addTransition(back);
    emitter.addSubtree(back, pingPong, AUTOMATON_TRANSITIONS);
    assertEquals(2, transitions.countMatches(pong, null, null));

    // ... and when the source of a transition changes
    ASTTransition ball = transition(pingPong, "Ping", "ball");
    ball.setFrom("Pong");
    network.featureValueChanged(TRANSITION_FROM, ball, "Ping", "Pong");
    assertEquals(Set.of("Pong-ball->Ping", "Pong-return->Pong", "Pong-ball->Pong"),
        transitions.getAllMatches(pong, null, null).stream()
            .map(TransitionPatternTest::describe).collect(Collectors.toSet()));
    assertFalse(transitions.hasMatch(ping, null, null));

    // several parameters can be bound at once
    assertEquals(1, transitions.countMatches(pong, null, ping));
    assertTrue(transitions.getOneArbitraryMatch(pong, null, ping).isPresent());
  }

  /**
   * Fixes the source state within the network by a filter node, yielding a
   * production (and events) only for the outgoing transitions of that state.
   * The filter and its production are attached to the already initialized
   * network and initialize themselves from the existing matches.
   */
  @Test
  public void testFixedSourceInNetwork() {
    ASTState ping = state(pingPong, "Ping");
    // the parent of the production is the projection [source, transition, target]
    TupleSource allTransitions = transitions.getParent();
    ProductionNode fromPing = network.production(
        network.filter(allTransitions, t -> t.get(0) == ping), "source", "transition", "target");
    List<String> fromPingEvents = new ArrayList<>();
    fromPing.addMatchListener(new MatchListener() {
      @Override
      public void matchAppeared(Match match) {
        fromPingEvents.add("+" + describe(match));
      }

      @Override
      public void matchDisappeared(Match match) {
        fromPingEvents.add("-" + describe(match));
      }
    });
    assertEquals(Set.of("Ping-ball->Pong"),
        fromPing.getAllMatches().stream().map(TransitionPatternTest::describe)
            .collect(Collectors.toSet()));

    // a new outgoing transition of Ping appears ...
    ASTTransition loop = SimpleAutomataMill.transitionBuilder()
        .setFrom("Ping").setInput("wait").setTo("Ping").build();
    pingPong.addTransition(loop);
    emitter.addSubtree(loop, pingPong, AUTOMATON_TRANSITIONS);
    // ... while changes of other states' transitions cause no events
    ASTTransition pongBall = transition(pingPong, "Pong", "ball");
    pongBall.setTo("Pong");
    network.featureValueChanged(TRANSITION_TO, pongBall, "Ping", "Pong");
    // Ping of the other automaton is a different state
    ASTTransition serve = transition(other, "Ping", "serve");
    serve.setTo("Ping");
    network.featureValueChanged(TRANSITION_TO, serve, "Pong", "Ping");

    assertEquals(List.of("+Ping-wait->Ping"), fromPingEvents);
    assertEquals(2, fromPing.countMatches());
  }

  /**
   * Boolean attributes are always reported; initial states are those whose
   * {@code STATE_INITIAL} fact has the value {@code true}.
   */
  @Test
  public void testBooleanFeature() {
    ReteNetwork net = new ReteNetwork();
    TupleSource initial = net.featureInput(STATE_INITIAL); // [s, isInitial]
    ProductionNode initialStates = net.production(
        net.project(net.filter(initial, t -> t.get(1, Boolean.class)), 0), "state");
    new SimpleAutomataFullReteFactEmitter(net).addSubtree(pingPong);

    ASTState ping = state(pingPong, "Ping");
    ASTState pong = state(pingPong, "Pong");
    assertEquals(Set.of(Tuple.of(ping)), initialStates.getMatchTuples());

    pong.setInitial(true);
    net.featureValueChanged(STATE_INITIAL, pong, false, true);
    assertEquals(Set.of(Tuple.of(ping), Tuple.of(pong)), initialStates.getMatchTuples());
  }
}
