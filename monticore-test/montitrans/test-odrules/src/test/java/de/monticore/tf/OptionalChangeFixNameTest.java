/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonMill.class)
public class OptionalChangeFixNameTest {

  private ASTAutomaton setUp(String model) throws IOException {
    String inputFile = "src/main/models/automaton/" + model;
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    return aut.get();
  }

  @Test
  public void testSuccessfulMatch() throws IOException {
    ASTAutomaton automaton = setUp("AutomatonTwoStatesAndSubstate.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertTrue(testee.get_state_2().isPresent());
    assertEquals("c", testee.get_state_2().get().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuccessfulMatchReplacement() throws IOException {
    ASTAutomaton automaton = setUp("AutomatonTwoStatesAndSubstate.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertTrue(testee.get_state_2().isPresent());
    assertEquals("c", testee.get_state_2().get().getName());
    testee.doReplacement();
    assertTrue(testee.get_state_2().isPresent());
    assertEquals("c_new", testee.get_state_2().get().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoOptFoundMatch() throws IOException {
    ASTAutomaton automaton = setUp("AutomatonTwoStatesAndSubstate_2.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertFalse(testee.get_state_2().isPresent());
  }

  @Test
  public void testNoOptFoundReplacement() throws IOException {
    ASTAutomaton automaton = setUp("AutomatonTwoStatesAndSubstate_2.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertFalse(testee.get_state_2().isPresent());
    testee.doReplacement();
    assertFalse(testee.get_state_2().isPresent());
  }

  @Test
  public void testNoOptFoundUndoReplacement() throws IOException {
    ASTAutomaton automaton = setUp("AutomatonTwoStatesAndSubstate_2.aut");

    OptionalChangeFixName testee = new OptionalChangeFixName(automaton);
    assertTrue(testee.doPatternMatching());
    assertFalse(testee.get_state_2().isPresent());
    testee.doReplacement();
    assertFalse(testee.get_state_2().isPresent());
    testee.undoReplacement();
    assertFalse(testee.get_state_2().isPresent());
  }
}
