/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonMill.class)
public class CreateStateTest {

  ASTAutomaton aut;

  @BeforeEach
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> autOpt = parser.parse(inputFile);
    assertTrue(autOpt.isPresent());
    aut = autOpt.get();
  }

  @Test
  public void testDoReplacement() {
    int noOfStates_Before = aut.getStateList().size();
    CreateState testee = new CreateState(aut);
    testee.doAll();
    assertEquals(noOfStates_Before + 1, aut.getStateList().size());
  }

  @Test
  public void testGet_state_1() {
    CreateState testee = new CreateState(aut);
    testee.doPatternMatching();
    assertNull(testee.get_state_1());
    testee.doReplacement();
    assertEquals(aut.getState(0), testee.get_state_1());
  }

}
