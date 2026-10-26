/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.rete.AutomatonModel.*;
import static org.junit.jupiter.api.Assertions.*;

public class TypeInputNodeTest extends AbstractNodeTest {

  protected ASTAutomaton aut;

  protected ASTState s1, s2;

  protected ASTTransition t1;

  @BeforeEach
  public void setUp() {
    aut = new ASTAutomaton("aut");
    s1 = new ASTState("s1");
    s2 = new ASTState("s2");
    t1 = new ASTTransition("t1");
  }

  @Test
  public void testContainsReportedNodes() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    RecordingReceiver recorder = record(states);
    assertSame(ASTState.class, states.getType());
    assertEquals(List.of(ASTState.class), states.getColumnTypes());
    assertSame(states, net.typeInput(ASTState.class));

    net.nodeAdded(s1);
    net.nodeAdded(s2);
    assertTrue(states.contains(s1));
    assertEquals(2, states.size());
    net.nodeRemoved(s1);
    assertFalse(states.contains(s1));
    assertEquals(List.of("+[s1]", "+[s2]", "-[s1]"), recorder.take());
    assertEquals(tuples(Tuple.of(s2)), contents(states));
    recorder.assertConsistent();
  }

  @Test
  public void testContainsInstancesOfSubtypes() {
    TypeInputNode<ASTNamedElement> named = net.typeInput(ASTNamedElement.class);
    net.nodeAdded(aut);
    net.nodeAdded(s1);
    net.nodeAdded(t1);
    assertEquals(tuples(Tuple.of(aut), Tuple.of(s1)), contents(named));
  }

  @Test
  public void testInvalidNotificationsAreRejected() {
    net.typeInput(ASTState.class);
    net.nodeAdded(s1);
    assertThrows(IllegalStateException.class, () -> net.nodeAdded(s1));
    assertThrows(IllegalStateException.class, () -> net.nodeRemoved(s2));
  }

  @Test
  public void testDirectModificationIsRejected() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    assertThrows(IllegalStateException.class, () -> states.insert(s1));
    assertThrows(IllegalStateException.class, () -> states.delete(s1));
  }

  @Test
  public void testOnlyOneInputPerTypeBeforeFirstFact() {
    net.typeInput(ASTState.class);
    assertThrows(IllegalStateException.class, () -> new TypeInputNode<>(net, ASTState.class));
    net.nodeAdded(s1);
    assertThrows(IllegalStateException.class, () -> net.typeInput(ASTTransition.class));
  }

  @Test
  public void testDispose() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    FilterNode filter = net.filter(states, t -> true);
    assertThrows(IllegalStateException.class, states::dispose);

    filter.dispose();
    states.dispose();
    assertTrue(net.getTypeInputs().isEmpty());
    assertFalse(net.isRelevant(s1));
    net.nodeAdded(s1);
    assertEquals(0, states.size());
  }

  @Test
  public void testToString() {
    assertEquals("TypeInputNode[ASTState]", net.typeInput(ASTState.class).toString());
  }
}
