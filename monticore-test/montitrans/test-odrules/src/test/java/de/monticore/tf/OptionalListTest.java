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
public class OptionalListTest {

  private ASTAutomaton setUp(String model) throws IOException {
    String inputFile = "src/main/models/automaton/" + model;
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    return aut.get();
  }

  @Test
  public void testEmptyAutomaton() throws IOException {
    ASTAutomaton automaton = setUp("EmptyAutomaton.aut");

    OptionalList testee = new OptionalList(automaton);
    assertFalse(testee.doPatternMatching());
  }

  @Test
  public void testSingleState() throws IOException {
    ASTAutomaton automaton = setUp("AutomatonWithSingleState.aut");

    OptionalList testee = new OptionalList(automaton);
    assertTrue(testee.doPatternMatching());

    if (testee.get_list_substate().isPresent()) {
      assertEquals(0, testee.get_list_substate().get().size());
    }
  }

  @Test
  public void testThreeSubstates() throws IOException {
    ASTAutomaton automaton = setUp("AutomatonStateWithThreeSubstates.aut");

    OptionalList testee = new OptionalList(automaton);
    assertTrue(testee.doPatternMatching());
    assertTrue(testee.get_list_substate().isPresent());
    assertEquals(3, testee.get_list_substate().get().size());
  }

  @Test
  public void testNegativeCondition() throws IOException {
    ASTAutomaton automaton = setUp("AutomatonStateWithInitialSubstate.aut");

    OptionalList testee = new OptionalList(automaton);
    assertTrue(testee.doPatternMatching());
    assertEquals("e", testee.get_state_1().getName());
    assertTrue(testee.get_list_substate().isPresent());
    assertEquals(2, testee.get_list_substate().get().size());
    assertEquals("f", testee.get_list_substate().get().get(0).getName());
    assertEquals("g", testee.get_list_substate().get().get(1).getName());
  }
}
