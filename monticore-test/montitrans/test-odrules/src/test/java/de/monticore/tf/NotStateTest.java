/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonMill.class)
public class NotStateTest {

  @Test
  public void testEmptyAutomat() throws IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(0, aut.get().getStateList().size());

    // execute tested code and store result
    NotState rule = new NotState(aut.get());

    // definition of test input
    rule.doAll();

    // assertions
    assertEquals(1, aut.get().getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAutomatWithState() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());

    // execute tested code and store result
    NotState rule = new NotState(aut.get());

    // definition of test input
    assertFalse(rule.doPatternMatching());

    assertEquals(1, aut.get().getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
