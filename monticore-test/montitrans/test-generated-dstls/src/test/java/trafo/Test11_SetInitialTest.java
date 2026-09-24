/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.SetInitial;
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
public class Test11_SetInitialTest {

  @Test
  public void testSetInitialState() throws IOException {
    StatechartParser p = StatechartMill.parser();
    Optional<ASTStatechart> scOpt = p.parse("src/test/resources/trafo/SC_withSubstates.sc");
    
    assertFalse(p.hasErrors());
    assertTrue(scOpt.isPresent());
    
    ASTStatechart sc = scOpt.get();

    SetInitial testee = new SetInitial(sc);
    testee.doAll();

    ASTState topState = sc.getState(0);
    assertNotNull(topState);
    assertTrue(topState.isInitial());

    testee.undoReplacement();
    assertFalse(topState.isInitial());
  }

}
