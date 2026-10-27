/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.rete.AutomatonModel.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link TypeInputNode}, which is filled by the AST node
 * notifications of the network.
 */
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

  /**
   * Added and removed AST nodes of the type are passed on as unary tuples, in
   * order; there is a single input node per type.
   */
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

  /**
   * The input node of an interface contains the instances of all implementing
   * classes, but no other nodes.
   */
  @Test
  public void testContainsInstancesOfSubtypes() {
    TypeInputNode<ASTNamedElement> named = net.typeInput(ASTNamedElement.class);
    net.nodeAdded(aut);
    net.nodeAdded(s1);
    net.nodeAdded(t1);
    assertEquals(tuples(Tuple.of(aut), Tuple.of(s1)), contents(named));
  }

  /**
   * Adding a node twice and removing an unknown node are rejected.
   */
  @Test
  public void testInvalidNotificationsAreRejected() {
    net.typeInput(ASTState.class);
    net.nodeAdded(s1);
    assertThrows(IllegalStateException.class, () -> net.nodeAdded(s1));
    assertThrows(IllegalStateException.class, () -> net.nodeRemoved(s2));
  }

  /**
   * The contents can only be changed via the notifications of the network,
   * not by calling {@code insert} or {@code delete} directly.
   */
  @Test
  public void testDirectModificationIsRejected() {
    TypeInputNode<ASTState> states = net.typeInput(ASTState.class);
    assertThrows(IllegalStateException.class, () -> states.insert(s1));
    assertThrows(IllegalStateException.class, () -> states.delete(s1));
  }

  /**
   * A second input node for the same type is rejected, and no input node can
   * be created after the first notification, as earlier facts are not retained.
   */
  @Test
  public void testOnlyOneInputPerTypeBeforeFirstFact() {
    net.typeInput(ASTState.class);
    assertThrows(IllegalStateException.class, () -> new TypeInputNode<>(net, ASTState.class));
    net.nodeAdded(s1);
    assertThrows(IllegalStateException.class, () -> net.typeInput(ASTTransition.class));
  }

  /**
   * An input node with children cannot be disposed; a disposed input node is
   * unregistered from the network and receives no further AST nodes.
   */
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

  /**
   * The string representation names the type.
   */
  @Test
  public void testToString() {
    assertEquals("TypeInputNode[ASTState]", net.typeInput(ASTState.class).toString());
  }
}
