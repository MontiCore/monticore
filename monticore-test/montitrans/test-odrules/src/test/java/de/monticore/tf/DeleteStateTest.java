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

public class DeleteStateTest {

  ASTAutomaton aut;
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
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
