/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.tf.CopyTransitionToSubstate;
import junit.framework.TestCase;
import mc.testcases.statechart.statechart._ast.ASTState;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.Test;

import java.io.IOException;

public class Test09_CopyTransitionTest extends TestCase {

  @Test
  public void testCopyTransitionToSubstate() throws IOException {
    StatechartParser p = new StatechartParser();
    ASTStatechart sc = p.parse("src/test/resources/trafo/SC_withTransitions.sc").get();

    assertNotNull(sc);
    assertFalse(p.hasErrors());

    CopyTransitionToSubstate testee = new CopyTransitionToSubstate(sc);
    testee.doAll();

    ASTState topState = sc.getState(0);
    assertNotNull(topState);
    assertEquals(3, topState.getTransitionList().size());
    assertEquals(1, topState.getState(0).getTransitionList().size());

    testee.undoReplacement();

    assertEquals(3, topState.getTransitionList().size());
    assertEquals(0, topState.getState(0).getTransitionList().size());
  }

}
