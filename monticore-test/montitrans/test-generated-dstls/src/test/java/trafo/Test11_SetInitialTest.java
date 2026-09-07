/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.tf.SetInitial;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.statechart.statechart._ast.ASTState;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class Test11_SetInitialTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSetInitialState() throws IOException {
    StatechartParser p = new StatechartParser();
    ASTStatechart sc = p.parse("src/test/resources/trafo/SC_withSubstates.sc").get();

    assertNotNull(sc);
    assertFalse(p.hasErrors());

    SetInitial testee = new SetInitial(sc);
    testee.doAll();

    ASTState topState = sc.getState(0);
    assertNotNull(topState);
    assertTrue(topState.isInitial());

    testee.undoReplacement();
    assertFalse(topState.isInitial());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
