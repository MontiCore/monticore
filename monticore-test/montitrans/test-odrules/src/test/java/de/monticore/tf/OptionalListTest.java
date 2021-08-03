/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class OptionalListTest {

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
  public void testEmptyAutomaton() throws IOException {
    setUp("EmptyAutomaton.aut");

    OptionalList testee = new OptionalList(automaton);
    assertFalse(testee.doPatternMatching());
  }

  @Test
  public void testSingleState() throws IOException {
    setUp("AutomatonWithSingleState.aut");

    OptionalList testee = new OptionalList(automaton);
    assertTrue(testee.doPatternMatching());

    if (testee.get_list_substate().isPresent()) {
      assertEquals(0, testee.get_list_substate().get().size());
    }
  }

  @Test
  public void testThreeSubstates() throws IOException {
    setUp("AutomatonStateWithThreeSubstates.aut");

    OptionalList testee = new OptionalList(automaton);
    assertTrue(testee.doPatternMatching());
    assertTrue(testee.get_list_substate().isPresent());
    assertEquals(3, testee.get_list_substate().get().size());
  }

  @Test
  public void testNegativeCondition() throws IOException {
    setUp("AutomatonStateWithInitialSubstate.aut");

    OptionalList testee = new OptionalList(automaton);
    assertTrue(testee.doPatternMatching());
    assertEquals(testee.get_state_1().getName(), "e");
    assertTrue(testee.get_list_substate().isPresent());
    assertEquals(testee.get_list_substate().get().size(), 2);
    assertEquals(testee.get_list_substate().get().get(0).getName(), "f");
    assertEquals(testee.get_list_substate().get().get(1).getName(), "g");
  }
}
