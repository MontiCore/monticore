/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SetInitialToFalseTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDoAll() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    ASTState s = parser.parseState("src/main/models/automaton/initialState.aut").get();

    assertTrue(s.isInitial());

    SetInitialToFalse sitf = new SetInitialToFalse(s);

    assertTrue(s.isInitial());

    sitf.doAll();

    assertFalse(s.isInitial());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDoAll2() throws IOException {
    AutomatonParser parser = new AutomatonParser();
    ASTAutomaton a = parser.parse("src/main/models/automaton/Testautomat.aut").get();


    SetInitialToFalse sitf = new SetInitialToFalse(a);

    // first try
    sitf.doAll();
    ASTState match_1 = sitf.get_state_1();

    // backtracking
    sitf.undoReplacement();

    // second run
    assertTrue(sitf.doPatternMatching());
    sitf.doReplacement();
    ASTState match_2 = sitf.get_state_1();
    assertNotSame(match_1, match_2);
  
    assertTrue(Log.getFindings().isEmpty());
    }
}
