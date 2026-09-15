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
public class AtMostOneSubstateTest {

  @Test
  public void testEmptyAutomaton() throws IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(0, aut.get().getStateList().size());

    // execute tested code and store result
    AtMostOneSubstate rule = new AtMostOneSubstate(aut.get());

    // should not match
    assertFalse(rule.doPatternMatching());
  }

  @Test
  public void testOneState() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());

    // execute tested code and store result
    AtMostOneSubstate rule = new AtMostOneSubstate(aut.get());

    // one state should match
    assertTrue(rule.doPatternMatching());
    assertNotNull(rule.get_state_top());
    assertFalse(rule.get_state_sub().isPresent());
  }

  @Test
  public void testOneSubstate() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(1, aut.get().getState(0).getStateList().size());

    // execute tested code and store result
    AtMostOneSubstate rule = new AtMostOneSubstate(aut.get());

    // one substate should match
    assertTrue(rule.doPatternMatching());
    assertNotNull(rule.get_state_top());
    assertEquals("a", rule.get_state_top().getName());
    assertTrue(rule.get_state_sub().isPresent());
    assertEquals("c", rule.get_state_sub().get().getName());
  }

  @Test
  public void testTwoSubstates() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoSubstates.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());
    assertEquals(2, aut.get().getState(0).getStateList().size());

    // execute tested code and store result
    AtMostOneSubstate rule = new AtMostOneSubstate(aut.get());

    // two substates should not match
    assertFalse(rule.doPatternMatching());
  }

  @Test
  public void testSubstateWithSubstate() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonSubstateWithSubstate.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());
    assertEquals(1, aut.get().getState(0).getStateList().size());
    assertEquals(1, aut.get().getState(0).getState(0).getStateList().size());

    // execute tested code and store result
    AtMostOneSubstate rule = new AtMostOneSubstate(aut.get());

    // substate with substate should not match
    assertFalse(rule.doPatternMatching());
  }
}
