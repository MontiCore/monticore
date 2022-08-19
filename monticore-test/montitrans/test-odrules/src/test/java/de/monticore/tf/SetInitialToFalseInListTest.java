/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

public class SetInitialToFalseInListTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDoAll() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    ASTState s = parser.parseState("src/main/models/automaton/initialState.aut").get();

    assertTrue(s.isInitial());

    SetInitialToFalseInList sitfl = new SetInitialToFalseInList(s);

    assertTrue(s.isInitial());

    sitfl.doAll();
    assertFalse(s.isInitial());
    
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testUndoReplacement() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    ASTState s = parser.parseState("src/main/models/automaton/initialState.aut").get();

    assertTrue(s.isInitial());

    SetInitialToFalseInList sitfl = new SetInitialToFalseInList(s);

    assertTrue(s.isInitial());

    sitfl.doAll();
    sitfl.undoReplacement();
    assertTrue(s.isInitial());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
