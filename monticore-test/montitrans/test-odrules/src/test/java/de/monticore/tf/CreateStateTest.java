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

import static org.junit.Assert.*;

public class CreateStateTest {

  ASTAutomaton aut;
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = new AutomatonParser();
    aut = parser.parse(inputFile).get();

  }

  @Test
  public void testDoReplacment() {
    int noOfStates_Before = aut.getStateList().size();
    CreateState testee = new CreateState(aut);
    testee.doAll();
    assertEquals(noOfStates_Before + 1, aut.getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGet_state_1() {
    CreateState testee = new CreateState(aut);
    testee.doPatternMatching();
    assertNull(testee.get_state_1());
    testee.doReplacement();
    assertEquals(aut.getState(0), testee.get_state_1());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
