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

public class DeleteStateTest {

  ASTAutomaton aut;
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = new AutomatonParser();
     aut = parser.parse(inputFile).get();

  }

  @Test
  public void testDoReplacment() {
    int noOfStates_Before = aut.getStateList().size();
    new DeleteState(aut).doAll();
    assertEquals(noOfStates_Before - 1, aut.getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testUndoReplacment() {
    int noOfStates_Before = aut.getStateList().size();
    DeleteState testee = new DeleteState(aut);
    testee.doAll();
    testee.undoReplacement();

    assertEquals(noOfStates_Before, aut.getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
