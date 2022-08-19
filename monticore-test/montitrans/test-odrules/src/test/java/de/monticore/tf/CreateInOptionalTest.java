/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class CreateInOptionalTest {

  private ASTAutomaton automaton;
  
  @Before
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
  public void testIsNotPresent() throws IOException {
    setUp("EmptyAutomaton.aut");
    CreateInOptional testee = new CreateInOptional(automaton);
    assertTrue(testee.doPatternMatching());
    assertFalse(testee.get_state_1().isPresent());

    testee.doReplacement();
    // no state was found, so no state should be created
    assertFalse(testee.get_state_1().isPresent());
    assertFalse(testee.get_state_2().isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsPresent() throws IOException {
    setUp("AutomatonWithSingleState.aut");
    CreateInOptional testee = new CreateInOptional(automaton);
    assertTrue(testee.doPatternMatching());
    assertTrue(testee.get_state_1().isPresent());

    testee.doReplacement();
    // a state was found, so a substate should be created
    assertTrue(testee.get_state_1().isPresent());
    assertTrue(testee.get_state_2().isPresent());
    assertEquals(testee.get_state_2().get().getName(), "TheNewState");
    assertFalse(testee.get_state_2().get().isInitial());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
