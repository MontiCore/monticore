/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.Feature;
import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.rete.AutomatonModel.*;
import static org.junit.jupiter.api.Assertions.*;

public class FeatureInputNodeTest extends AbstractNodeTest {

  protected ASTState s1, s2;

  @BeforeEach
  public void setUp() {
    s1 = new ASTState("s1");
    s2 = new ASTState("s2");
  }

  @Test
  public void testContainsReportedValuesAsMultiset() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    RecordingReceiver recorder = record(names);
    assertSame(NAME, names.getFeature());
    assertEquals(List.of(ASTNamedElement.class, String.class), names.getColumnTypes());
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

  @Test
  public void testInvalidNotificationsAreRejected() {
    net.featureInput(NAME);
    assertThrows(IllegalStateException.class, () -> net.featureValueRemoved(NAME, s1, "x"));
    assertThrows(NullPointerException.class, () -> net.featureValueAdded(NAME, s1, null));
    @SuppressWarnings({"rawtypes", "unchecked"})
    Feature<ASTNamedElement, Object> raw = (Feature) NAME;
    assertThrows(ClassCastException.class, () -> net.featureValueAdded(raw, s1, 42));
  }

  @Test
  public void testValuesOfFeaturesWithoutInputAreIgnored() {
    net.featureInput(NAME);
    assertFalse(net.isRelevant(SOURCE));
    net.featureValueAdded(SOURCE, new ASTTransition("t1"), s1);
  }

  @Test
  public void testDirectModificationIsRejected() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    assertThrows(IllegalStateException.class, () -> names.insert(s1, "x"));
    assertThrows(IllegalStateException.class, () -> names.delete(s1, "x"));
  }

  @Test
  public void testOnlyOneInputPerFeatureBeforeFirstFact() {
    net.featureInput(NAME);
    assertThrows(IllegalStateException.class, () -> new FeatureInputNode<>(net, NAME));
    net.featureValueAdded(NAME, s1, "x");
    assertThrows(IllegalStateException.class, () -> net.featureInput(STATES));
  }

  @Test
  public void testDispose() {
    FeatureInputNode<ASTNamedElement, String> names = net.featureInput(NAME);
    names.dispose();
    assertTrue(net.getFeatureInputs().isEmpty());
    assertFalse(net.isRelevant(NAME));
    net.featureValueAdded(NAME, s1, "x");
    assertEquals(0, names.size());
  }

  @Test
  public void testToString() {
    assertEquals("FeatureInputNode[" + NAME + "]", net.featureInput(NAME).toString());
  }
}
