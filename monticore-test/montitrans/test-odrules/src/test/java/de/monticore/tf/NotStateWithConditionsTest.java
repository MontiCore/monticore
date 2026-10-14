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
public class NotStateWithConditionsTest {

  @Test
  public void testAutomatWith1InitialAnd2OtherStates() throws IOException {
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse_StringAutomaton("automaton Automaton {state a; state b <<initial>>; state c;}");

    assertTrue(aut.isPresent());
    assertEquals(3, aut.get().getStateList().size());

    // execute tested code and store result
    NotStateWithConditions rule = new NotStateWithConditions(aut.get());

    // assertions
    assertFalse(rule.doPatternMatching());
  }

  @Test
  public void testAutomatWith3OtherStates() throws IOException {
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse_StringAutomaton("automaton Automaton {state a; state b; state c;}");

    assertTrue(aut.isPresent());
    assertEquals(3, aut.get().getStateList().size());

    // execute tested code and store result
    NotStateWithConditions rule = new NotStateWithConditions(aut.get());

    // assertions
    rule.doPatternMatching();
  }
}
