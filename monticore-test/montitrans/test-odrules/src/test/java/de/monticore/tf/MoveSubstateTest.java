/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MoveSubstateTest {
  
  ASTAutomaton aut;

  @Before
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
