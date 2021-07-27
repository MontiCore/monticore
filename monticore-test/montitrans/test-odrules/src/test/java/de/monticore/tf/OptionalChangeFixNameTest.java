/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class OptionalChangeFixNameTest {

  private ASTAutomaton automaton;

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  private void setUp(String model) throws IOException {
    String inputFile = "src/main/models/automaton/" + model;
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    automaton = aut.get();
  }

  @Test
  public void testSuccessfullMatch() throws IOException {
    setUp("AutomatonTwoStatesAndSubstate.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertTrue(testee.get_state_2().isPresent());
    assertEquals(testee.get_state_2().get().getName(), "c");
  }

  @Test
  public void testSuccessfullMatchReplacement() throws IOException {
    setUp("AutomatonTwoStatesAndSubstate.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertTrue(testee.get_state_2().isPresent());
    assertEquals(testee.get_state_2().get().getName(), "c");
    testee.doReplacement();
    assertTrue(testee.get_state_2().isPresent());
    assertEquals(testee.get_state_2().get().getName(), "c_new");
  }

  @Test
  public void testNoOptFoundMatch() throws IOException {
    setUp("AutomatonTwoStatesAndSubstate_2.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertFalse(testee.get_state_2().isPresent());
  }

  @Test
  public void testNoOptFoundReplacement() throws IOException {
    setUp("AutomatonTwoStatesAndSubstate_2.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertFalse(testee.get_state_2().isPresent());
    testee.doReplacement();
    assertFalse(testee.get_state_2().isPresent());
  }

  @Test
  public void testNoOptFoundUndoReplacement() throws IOException {
    setUp("AutomatonTwoStatesAndSubstate_2.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertFalse(testee.get_state_2().isPresent());
    testee.doReplacement();
    assertFalse(testee.get_state_2().isPresent());
    testee.undoReplacement();
    assertFalse(testee.get_state_2().isPresent());
  }
}
