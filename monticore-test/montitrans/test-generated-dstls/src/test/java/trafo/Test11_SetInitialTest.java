/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.tf.SetInitial;
import junit.framework.TestCase;
import mc.testcases.statechart.statechart._ast.ASTState;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.Test;

import java.io.IOException;

public class Test11_SetInitialTest extends TestCase {

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

  }

}
