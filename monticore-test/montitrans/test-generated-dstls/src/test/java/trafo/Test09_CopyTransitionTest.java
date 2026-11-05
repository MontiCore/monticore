/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.CopyTransitionToSubstate;
import de.se_rwth.commons.logging.Log;
import mc.testcases.statechart.statechart.StatechartMill;
import mc.testcases.statechart.statechart._ast.ASTState;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(StatechartMill.class)
public class Test09_CopyTransitionTest {

  @Test
  public void testCopyTransitionToSubstate() throws IOException {
    StatechartParser p = StatechartMill.parser();
    Optional<ASTStatechart> scOpt = p.parse("src/test/resources/trafo/SC_withTransitions.sc");
    
    assertFalse(p.hasErrors());
    assertTrue(scOpt.isPresent());
    
    ASTStatechart sc = scOpt.get();

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
