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
public class OptStateWithOptSubstateTest {

  @Test
  public void testEmptyAutomaton() throws IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = AutomatonMill.parser();
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
    AutomatonParser parser = AutomatonMill.parser();
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
    AutomatonParser parser = AutomatonMill.parser();
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
