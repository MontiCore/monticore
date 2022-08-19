/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.tf.DeleteTransition;
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

public class Test10_DeleteTransitionTest extends TestCase {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testCopyTransitionToSubstate() throws IOException {
    StatechartParser p = new StatechartParser();
    ASTStatechart sc = p.parse("src/test/resources/trafo/SC_withTransitions.sc").get();

    assertNotNull(sc);
    assertFalse(p.hasErrors());

    DeleteTransition testee = new DeleteTransition(sc);
    testee.doAll();

    ASTState topState = sc.getState(0);
    assertNotNull(topState);
    assertEquals(2, topState.getTransitionList().size());
    assertEquals("A", topState.getTransition(0).getFrom());
    assertEquals("C", topState.getTransition(1).getFrom());

    testee.undoReplacement();

    assertEquals(3, topState.getTransitionList().size());

    assertEquals("A", topState.getTransition(0).getFrom());
    assertEquals("B", topState.getTransition(1).getFrom());
    assertEquals("C", topState.getTransition(2).getFrom());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
