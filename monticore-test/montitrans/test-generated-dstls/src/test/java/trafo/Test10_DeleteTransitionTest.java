/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.DeleteTransition;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.statechart.statechart.StatechartMill;
import mc.testcases.statechart.statechart._ast.ASTState;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(StatechartMill.class)
public class Test10_DeleteTransitionTest {

  @Test
  public void testCopyTransitionToSubstate() throws IOException {
    StatechartParser p = StatechartMill.parser();
    Optional<ASTStatechart> scOpt = p.parse("src/test/resources/trafo/SC_withTransitions.sc");
    
    assertFalse(p.hasErrors());
    assertTrue(scOpt.isPresent());
    
    ASTStatechart sc = scOpt.get();

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
  }

}
