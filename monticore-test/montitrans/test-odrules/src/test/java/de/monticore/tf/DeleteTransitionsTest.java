/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeleteTransitionsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDeleteTransitions() throws IOException {
    String inputFile = "src/main/models/automaton/Testautomat.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    DeleteTransitions dtr = new DeleteTransitions(aut.get());

    // definition of test input
    dtr.doAll();

    // assertions
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(2, aut.get().getTransitionList().size());

    ASTState hierarchicalState = aut.get().getState(1);
    assertEquals(2, hierarchicalState.getStateList().size());
    assertEquals(0, hierarchicalState.getTransitionList().size());

    // and undo
    dtr.undoReplacement();

    // assertions
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(2, aut.get().getTransitionList().size());

    assertEquals(2, hierarchicalState.getStateList().size());
    assertEquals(4, hierarchicalState.getTransitionList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }



}
