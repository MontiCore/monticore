/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.NoOtherFinalSubstate;
import de.monticore.tf.NoSubstateWithTwoFinalSubstates;
import de.monticore.tf.NoTwoFinalSubstates;
import de.monticore.tf.runtime.ODRule;
import mc.testcases.statechart.statechart.StatechartMill;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The objects of the negative structures of a part of a pattern are matched injectively among each
 * other (and w.r.t. the positive objects), like the objects of the positive part: the rules mark
 * the state Top as initial if their negative application condition is satisfied.
 */
@TestWithMCLanguage(StatechartMill.class)
public class NegativeInjectivityTest {

  /**
   * Applies the rule to a statechart with the given substates of Top and checks whether it matches
   * (and marks Top as initial) as expected
   */
  protected void assertMatches(Function<ASTStatechart, ODRule> rule, String substatesOfTop,
      boolean expected) throws IOException {
    StatechartParser parser = StatechartMill.parser();
    ASTStatechart sc = parser.parse_String("statechart S { state Top { " + substatesOfTop + " } }")
        .orElseThrow();
    assertFalse(parser.hasErrors());

    ODRule trafo = rule.apply(sc);
    boolean matches = trafo.doPatternMatching();
    if (matches) {
      trafo.doReplacement();
    }
    assertEquals(expected, matches);
    assertEquals(matches, sc.getState(0).isInitial());
  }

  /** A single final substate cannot be bound to both final substates of the negative structure */
  @ParameterizedTest(name = "[{index}] {0} -> {1}")
  @CsvSource(delimiter = '|', value = {
      "''                                                              | true",
      "state X { state A; }                                            | true",
      "state X { state A <<final>>; }                                  | true",
      "state X { state A <<final>>; } state Y { state B <<final>>; }   | true",
      "state X { state A <<final>>; state B <<final>>; }               | false"})
  public void testWithinNegativeStructure(String substatesOfTop, boolean expected)
      throws IOException {
    assertMatches(NoSubstateWithTwoFinalSubstates::new, substatesOfTop, expected);
  }

  /** The sibling negative structures form one negative application condition */
  @ParameterizedTest(name = "[{index}] {0} -> {1}")
  @CsvSource(delimiter = '|', value = {
      "''                                    | true",
      "state A;                              | true",
      "state A <<final>>;                    | true",
      "state A <<final>>; state B <<final>>; | false"})
  public void testSiblingNegativeStructures(String substatesOfTop, boolean expected)
      throws IOException {
    assertMatches(NoTwoFinalSubstates::new, substatesOfTop, expected);
  }

  /** The final substate bound by the positive part is not bound by the negative structure */
  @ParameterizedTest(name = "[{index}] {0} -> {1}")
  @CsvSource(delimiter = '|', value = {
      "''                                    | false",
      "state A <<final>>;                    | true",
      "state A <<final>>; state B;           | true",
      "state A <<final>>; state B <<final>>; | false"})
  public void testNegativeAndPositiveStructure(String substatesOfTop, boolean expected)
      throws IOException {
    assertMatches(NoOtherFinalSubstate::new, substatesOfTop, expected);
  }
}
