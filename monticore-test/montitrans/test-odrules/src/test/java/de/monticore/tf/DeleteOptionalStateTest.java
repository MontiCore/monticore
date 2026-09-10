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
public class DeleteOptionalStateTest {

  @Test
  public void testEmptyAutomaton() throws IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(0, aut.get().getStateList().size());

    // execute tested code and store result
    DeleteOptionalState rule = new DeleteOptionalState(aut.get());

    // should match
    assertTrue(rule.doPatternMatching());
    assertNotNull(rule.get_automaton_1());
    assertFalse(rule.get_state_1().isPresent());

    rule.doReplacement();

    // nothing should change
    assertEquals(0, aut.get().getStateList().size());
  }

  @Test
  public void testAutomatonWithOneState() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());

    // execute tested code and store result
    DeleteOptionalState rule = new DeleteOptionalState(aut.get());

    // the one state should be matched
    assertTrue(rule.doPatternMatching());
    assertNotNull(rule.get_automaton_1());
    assertTrue(rule.get_state_1().isPresent());

    rule.doReplacement();

    // state should be deleted
    assertEquals(0, aut.get().getStateList().size());
  }
}
