/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonMill.class)
public class CreateInOptionalTest {

  private ASTAutomaton automaton;

  private void setUp(String model) throws IOException {
    String inputFile = "src/main/models/automaton/" + model;
    AutomatonParser parser = AutomatonMill.parser();
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
  }

}
