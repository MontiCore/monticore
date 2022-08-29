/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeleteStateListTest {

  ASTAutomaton aut;
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
     aut = parser.parse(inputFile).get();

  }

  @Test
  public void testDoReplacement() {
    DeleteStateList testee = new DeleteStateList(aut);
    assertTrue(testee.doPatternMatching());
    testee.doReplacement();
    assertTrue(aut.getState(0).getStateList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testUndoReplacement() {
    List<ASTState> list_before = Lists.newArrayList(aut.getStateList());

    DeleteStateList testee = new DeleteStateList(aut);
    testee.doAll();
    testee.undoReplacement();

    assertEquals(2, aut.getStateList().size());
    assertTrue(list_before.containsAll(aut.getStateList()));
    assertTrue(aut.getStateList().containsAll(list_before));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
