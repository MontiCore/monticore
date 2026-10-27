/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.Match;
import de.monticore.rete.RecordingListener;
import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static de.monticore.rete.AutomatonModel.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link ProductionNode}: the matches as the distinct tuples of its
 * parent, the match events delivered to listeners and the queries.
 */
public class ProductionNodeTest extends AbstractNodeTest {

  /**
   * [state, transition]
   */
  protected TestSource source;

  protected ProductionNode production;

  protected ASTState s1, s2;

  protected ASTTransition t1, t2, t3;

  @BeforeEach
  public void setUp() {
    source = source(ASTState.class, ASTTransition.class);
    production = net.production(source, "state", "tr");
    s1 = new ASTState("s1");
    s2 = new ASTState("s2");
    t1 = new ASTTransition("t1");
    t2 = new ASTTransition("t2");
    t3 = new ASTTransition("t3");
  }

  protected Match match(Object... values) {
    return new Match(production, Tuple.of(values));
  }

  /**
   * The parameter names are assigned to the columns in order; unknown names
   * are rejected.
   */
  @Test
  public void testParameters() {
    assertSame(source, production.getParent());
    assertEquals(List.of("state", "tr"), production.getParameterNames());
    assertEquals(2, production.getArity());
    assertEquals(1, production.getParameterIndex("tr"));
    assertThrows(IllegalArgumentException.class, () -> production.getParameterIndex("x"));
  }

  /**
   * A wrong number of parameter names, duplicate names and columns that are
   * no AST nodes are rejected.
   */
  @Test
  public void testInvalidConfigurationsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> net.production(source, "state"),
        "number of names");
    assertThrows(IllegalArgumentException.class, () -> net.production(source, "x", "x"),
        "duplicate name");
    assertThrows(IllegalArgumentException.class,
        () -> net.production(source(ASTState.class, String.class), "state", "name"),
        "non-AST column");
  }

  /**
   * A tuple is a match as long as at least one occurrence exists, and is
   * contained once in the matches regardless of its multiplicity. The match
   * tuples cannot be modified.
   */
  @Test
  public void testMatchesAreTuplesWithPositiveCount() {
    source.insert(s1, t1);
    source.insert(s1, t1);
    assertEquals(Set.of(Tuple.of(s1, t1)), production.getMatchTuples());
    assertEquals(1, production.countMatches());
    source.delete(s1, t1);
    assertTrue(production.hasMatch(s1, t1));
    source.delete(s1, t1);
    assertFalse(production.hasMatch(s1, t1));
    assertThrows(UnsupportedOperationException.class,
        () -> production.getMatchTuples().add(Tuple.of(s1, t1)));
  }

  /**
   * A production created on a populated parent contains the existing matches
   * without reporting them to listeners added afterwards.
   */
  @Test
  public void testInitializedFromPopulatedParent() {
    source.insert(s1, t1);
    ProductionNode later = net.production(source, "state", "tr");
    RecordingListener listener = new RecordingListener();
    later.addMatchListener(listener);
    assertEquals(Set.of(Tuple.of(s1, t1)), later.getMatchTuples());
    assertTrue(listener.events.isEmpty());
  }

  /**
   * Listeners are notified when a tuple becomes or stops being a match, not
   * about further occurrences of an existing match. Removed listeners are no
   * longer notified.
   */
  @Test
  public void testListenersAreNotifiedAboutChangedMatches() {
    RecordingListener listener = new RecordingListener();
    production.addMatchListener(listener);
    source.insert(s1, t1);
    source.insert(s1, t1);   // still the same match
    assertEquals(Set.of(match(s1, t1)), listener.matches);
    assertEquals(1, listener.events.size());

    source.delete(s1, t1);
    source.delete(s1, t1);
    assertEquals(Set.of(), listener.matches);
    assertEquals(2, listener.events.size());

    production.removeMatchListener(listener);
    source.insert(s1, t1);
    assertEquals(2, listener.events.size());
  }

  /**
   * Inserting and deleting a match within one transaction reports no event.
   */
  @Test
  public void testChangesCancellingOutAreNotReported() {
    RecordingListener listener = new RecordingListener();
    production.addMatchListener(listener);
    net.transaction(() -> {
      source.insert(s1, t1);
      source.delete(s1, t1);
    });
    assertTrue(listener.events.isEmpty());
  }

  /**
   * Events are delivered at the end of the outermost transaction, not during
   * the change itself.
   */
  @Test
  public void testEventsAreDeliveredAtTheEndOfTheTransaction() {
    RecordingListener listener = new RecordingListener();
    production.addMatchListener(listener);
    net.transaction(() -> {
      source.insert(s1, t1);
      assertTrue(listener.events.isEmpty());
    });
    assertEquals(Set.of(match(s1, t1)), listener.matches);
  }

  /**
   * A listener added with {@code fireForExistingMatches} is notified
   * immediately about all current matches.
   */
  @Test
  public void testFireForExistingMatches() {
    source.insert(s1, t1);
    RecordingListener listener = new RecordingListener();
    production.addMatchListener(listener, true);
    assertEquals(Set.of(match(s1, t1)), listener.matches);
  }

  /**
   * Queries without values, with only unbound values ({@code null}), with
   * some bound values and with all values bound return the compatible
   * matches; a binding of wrong length is rejected.
   */
  @Test
  public void testQueries() {
    source.insert(s1, t1);
    source.insert(s1, t2);
    source.insert(s2, t1);
    assertEquals(3, production.getAllMatches().size());
    assertEquals(3, production.countMatches(null, null));
    assertEquals(Set.of(match(s1, t1), match(s1, t2)), Set.copyOf(production.getAllMatches(s1, null)));
    assertEquals(2, production.countMatches(null, t1));
    assertEquals(1, production.countMatches(s1, t1));
    assertEquals(0, production.countMatches(s2, t2));
    assertTrue(production.hasMatch(s2, null));
    assertFalse(production.hasMatch(null, t3));
    assertSame(t1, production.getOneArbitraryMatch(s2, null).orElseThrow().get("tr"));
    assertTrue(production.getOneArbitraryMatch(s2, t2).isEmpty());
    assertThrows(IllegalArgumentException.class, () -> production.countMatches(s1));
  }

  /**
   * The index created for the first query with a partial binding is
   * maintained afterwards: later queries with the same bound positions
   * reflect insertions and deletions of matches.
   */
  @Test
  public void testQueryIndexesAreMaintained() {
    source.insert(s1, t1);
    assertEquals(1, production.countMatches(s1, null));
    source.insert(s1, t2);
    source.insert(s2, t3);
    assertEquals(2, production.countMatches(s1, null));
    source.delete(s1, t1);
    source.delete(s1, t2);
    assertEquals(0, production.countMatches(s1, null));
    assertEquals(1, production.countMatches(s2, null));
  }

  /**
   * The action of {@code forEachMatch} may change the network, e.g. delete
   * the visited matches, as the matches are copied before.
   */
  @Test
  public void testForEachMatchMayReportChanges() {
    source.insert(s1, t1);
    source.insert(s1, t2);
    production.forEachMatch(m -> source.delete(m.get("state"), m.get("tr")), s1, null);
    assertEquals(0, production.countMatches());
  }

  /**
   * The deletion of a tuple that is no match is reported as an inconsistent
   * network.
   */
  @Test
  public void testDeletionOfUnknownTupleIsDetected() {
    assertThrows(IllegalStateException.class, () -> source.delete(s1, t1));
  }

  /**
   * A disposed production is detached from its parent and drops its
   * listeners, which are not notified about later changes.
   */
  @Test
  public void testDispose() {
    RecordingListener listener = new RecordingListener();
    production.addMatchListener(listener);
    production.dispose();
    assertTrue(source.getChildren().isEmpty());
    source.insert(s1, t1);
    assertTrue(listener.events.isEmpty());
  }
}
