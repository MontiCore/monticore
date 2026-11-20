/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.Feature;
import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.rete.AutomatonModel.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link FeatureInputNode}, which is filled by the feature
 * notifications of the network.
 */
public class FeatureInputNodeTest extends AbstractNodeTest {

  protected ASTState s1, s2;

  @BeforeEach
  public void setUp() {
    s1 = new ASTState("s1");
    s2 = new ASTState("s2");
  }

  /**
   * Added, changed and removed feature values are passed on as
   * {@code [owner, value]} tuples, in order; a value added twice is
   * contained twice. There is a single input node per feature. The columns are
   * not optional (unset optional attributes produce no facts).
   */
  @Test
  public void testContainsReportedValuesAsMultiset() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    RecordingReceiver recorder = record(names);
    assertSame(NAME, names.getFeature());
    assertEquals(List.of(ASTNamedElement.class, String.class), names.getColumnTypes());
    assertEquals(List.of(false, false), names.getColumnOptionality());
    assertSame(names, net.featureInput(NAME));

    net.featureValueAdded(NAME, s1, "x");
    net.featureValueAdded(NAME, s1, "x");
    net.featureValueAdded(NAME, s2, "y");
    assertEquals(2, names.count(s1, "x"));
    assertEquals(0, names.count(s2, "x"));
    assertEquals(3, names.size());

    net.featureValueChanged(NAME, s2, "y", "z");
    net.featureValueRemoved(NAME, s1, "x");
    assertEquals(List.of("+[s1, x]", "+[s1, x]", "+[s2, y]", "-[s2, y]", "+[s2, z]", "-[s1, x]"),
        recorder.take());
    assertEquals(tuples(Tuple.of(s1, "x"), Tuple.of(s2, "z")), contents(names));
    recorder.assertConsistent();
  }

  /**
   * Removing an unknown value, a {@code null} value and (with raw types) a
   * value of the wrong type are rejected.
   */
  @Test
  public void testInvalidNotificationsAreRejected() {
    net.featureInput(NAME);
    assertThrows(IllegalStateException.class, () -> net.featureValueRemoved(NAME, s1, "x"));
    assertThrows(NullPointerException.class, () -> net.featureValueAdded(NAME, s1, null));
    @SuppressWarnings({"rawtypes", "unchecked"})
    Feature<ASTNamedElement, Object> raw = (Feature) NAME;
    assertThrows(ClassCastException.class, () -> net.featureValueAdded(raw, s1, 42));
  }

  /**
   * Values of features without input node are irrelevant and ignored
   * without error.
   */
  @Test
  public void testValuesOfFeaturesWithoutInputAreIgnored() {
    net.featureInput(NAME);
    assertFalse(net.isRelevant(SOURCE));
    net.featureValueAdded(SOURCE, new ASTTransition("t1"), s1);
  }

  /**
   * The contents can only be changed via the notifications of the network,
   * not by calling {@code insert} or {@code delete} directly.
   */
  @Test
  public void testDirectModificationIsRejected() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    assertThrows(IllegalStateException.class, () -> names.insert(s1, "x"));
    assertThrows(IllegalStateException.class, () -> names.delete(s1, "x"));
  }

  /**
   * A second input node for the same feature is rejected, and no input node
   * can be created after the first notification.
   */
  @Test
  public void testOnlyOneInputPerFeatureBeforeFirstFact() {
    net.featureInput(NAME);
    assertThrows(IllegalStateException.class, () -> new FeatureInputNode<>(net, NAME));
    net.featureValueAdded(NAME, s1, "x");
    assertThrows(IllegalStateException.class, () -> net.featureInput(STATES));
  }

  /**
   * A disposed input node is unregistered from the network and receives no
   * further values.
   */
  @Test
  public void testDispose() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    names.dispose();
    assertTrue(net.getFeatureInputs().isEmpty());
    assertFalse(net.isRelevant(NAME));
    net.featureValueAdded(NAME, s1, "x");
    assertEquals(0, names.size());
  }

  /**
   * The string representation names the feature.
   */
  @Test
  public void testToString() {
    assertEquals("FeatureInputNode[" + NAME + "]", net.featureInput(NAME).toString());
  }
}
