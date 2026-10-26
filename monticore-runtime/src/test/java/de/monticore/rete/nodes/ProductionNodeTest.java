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

  @Test
  public void testParameters() {
    assertSame(source, production.getParent());
    assertEquals(List.of("state", "tr"), production.getParameterNames());
    assertEquals(2, production.getArity());
    assertEquals(1, production.getParameterIndex("tr"));
    assertThrows(IllegalArgumentException.class, () -> production.getParameterIndex("x"));
  }

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

  @Test
  public void testInitializedFromPopulatedParent() {
    source.insert(s1, t1);
    ProductionNode later = net.production(source, "state", "tr");
    RecordingListener listener = new RecordingListener();
    later.addMatchListener(listener);
    assertEquals(Set.of(Tuple.of(s1, t1)), later.getMatchTuples());
    assertTrue(listener.events.isEmpty());
  }

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

  @Test
  public void testFireForExistingMatches() {
    source.insert(s1, t1);
    RecordingListener listener = new RecordingListener();
    production.addMatchListener(listener, true);
    assertEquals(Set.of(match(s1, t1)), listener.matches);
  }

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
   * The index created for a partial binding is maintained afterwards.
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

  @Test
  public void testForEachMatchMayReportChanges() {
    source.insert(s1, t1);
    source.insert(s1, t2);
    production.forEachMatch(m -> source.delete(m.get("state"), m.get("tr")), s1, null);
    assertEquals(0, production.countMatches());
  }

  @Test
  public void testDeletionOfUnknownTupleIsDetected() {
    assertThrows(IllegalStateException.class, () -> source.delete(s1, t1));
  }

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
