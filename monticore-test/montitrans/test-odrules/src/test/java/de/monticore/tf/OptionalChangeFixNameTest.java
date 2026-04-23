/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalChangeFixNameTest {

  private ASTAutomaton automaton;
  
  @BeforeEach
  public void before() {
    LogStub.init();
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
