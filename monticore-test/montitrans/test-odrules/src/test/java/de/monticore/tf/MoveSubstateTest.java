/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveSubstateTest {
  
  ASTAutomaton aut;

  @BeforeEach
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    aut = parser.parse(inputFile).get();
  }

  @Test
  public void testDoReplacment() {
    new MoveSubstate(aut).doAll();
    assertEquals(2, aut.getStateList().size());
    assertEquals(0, aut.getState(0).getStateList().size());
    assertEquals(1, aut.getState(1).getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testUndoReplacment() {
    MoveSubstate testee = new MoveSubstate(aut);
    testee.doAll();
    testee.undoReplacement();

    assertEquals(2, aut.getStateList().size());
    assertEquals(1, aut.getState(0).getStateList().size());
    assertEquals(0, aut.getState(1).getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
