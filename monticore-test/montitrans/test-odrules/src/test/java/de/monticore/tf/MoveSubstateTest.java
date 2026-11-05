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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class MoveSubstateTest {
  
  ASTAutomaton aut;

  @BeforeEach
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> parsedAut = parser.parse(inputFile);
    assertTrue(parsedAut.isPresent());
    aut = parsedAut.get();
  }

  @Test
  public void testDoReplacement() {
    new MoveSubstate(aut).doAll();
    assertEquals(2, aut.getStateList().size());
    assertEquals(0, aut.getState(0).getStateList().size());
    assertEquals(1, aut.getState(1).getStateList().size());
  }

  @Test
  public void testUndoReplacement() {
    MoveSubstate testee = new MoveSubstate(aut);
    testee.doAll();
    testee.undoReplacement();

    assertEquals(2, aut.getStateList().size());
    assertEquals(1, aut.getState(0).getStateList().size());
    assertEquals(0, aut.getState(1).getStateList().size());
  }

}
