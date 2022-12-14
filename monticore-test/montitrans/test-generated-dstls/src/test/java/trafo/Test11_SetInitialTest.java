/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.tf.SetInitial;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import junit.framework.TestCase;
import mc.testcases.statechart.statechart._ast.ASTState;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class Test11_SetInitialTest extends TestCase {
  
  @Before
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
