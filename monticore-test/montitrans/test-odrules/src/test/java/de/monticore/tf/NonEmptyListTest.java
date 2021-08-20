/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class NonEmptyListTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testDoPatternMatching_1() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    NonEmptyList testee = new NonEmptyList(aut.get());

    // definition of test input
    assertTrue(testee.doPatternMatching());

    // assertions
    assertEquals(1, testee.get_list_1_state_1().size());
    ASTState subState = aut.get().getState(0).getState(0);
    assertEquals(subState, testee.get_list_1_state_1().get(0));
  }

  @Test
  public void testDoPatternMatching_2() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate_2.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    NonEmptyList testee = new NonEmptyList(aut.get());

    // definition of test input
    assertTrue(testee.doPatternMatching());

    // assertions
    assertNotNull(testee.get_list_1_state_1());
    assertEquals(1, testee.get_list_1_state_1().size());
    ASTState subState = aut.get().getState(1).getState(0);
    assertEquals(subState, testee.get_list_1_state_1().get(0));
  }

}
