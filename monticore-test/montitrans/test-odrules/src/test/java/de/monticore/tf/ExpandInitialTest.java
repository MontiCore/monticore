/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

public class ExpandInitialTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDoAll() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    ExpandInitial testee = new ExpandInitial(aut.get());

    // definition of test input
    testee.doAll();

    // assertions
    assertFalse(aut.get().getState(0).isInitial());
    assertFalse(aut.get().getState(1).isInitial());
    assertTrue(aut.get().getState(0).getState(0).isInitial());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testUndoReplacement() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    ExpandInitial testee = new ExpandInitial(aut.get());

    // definition of test input
    testee.doAll();
    testee.undoReplacement();

    // assertions
    assertFalse(aut.get().getState(0).isInitial());
    assertFalse(aut.get().getState(1).isInitial());
    assertFalse(aut.get().getState(0).getState(0).isInitial());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
