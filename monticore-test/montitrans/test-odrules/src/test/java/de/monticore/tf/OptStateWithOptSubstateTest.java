/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class OptStateWithOptSubstateTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  ASTAutomaton aut;

  @Test
  public void testEmptyAutomaton() throws IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(0, aut.get().getStateList().size());

    // execute tested code and store result
    OptStateWithOptSubstate rule = new OptStateWithOptSubstate(aut.get());

    // should match
    assertTrue(rule.doPatternMatching());
    assertFalse(rule.get_state_1().isPresent());
    assertFalse(rule.get_state_2().isPresent());
  }

  @Test
  public void testAutomatonWithOneState() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());

    // execute tested code and store result
    OptStateWithOptSubstate rule = new OptStateWithOptSubstate(aut.get());

    // one state should match
    assertTrue(rule.doPatternMatching());
    assertTrue(rule.get_state_1().isPresent());
    assertFalse(rule.get_state_2().isPresent());
  }

  @Test
  public void testAutomatonWithStateAndSubstate() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(1, aut.get().getState(0).getStateList().size());

    // execute tested code and store result
    OptStateWithOptSubstate rule = new OptStateWithOptSubstate(aut.get());

    // definition of test input
    assertTrue(rule.doPatternMatching());
    assertTrue(rule.get_state_1().isPresent());
    assertTrue(rule.get_state_2().isPresent());
  }
}
