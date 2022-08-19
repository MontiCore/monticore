/* (c) https://github.com/MontiCore/monticore */
package mc.feature.hwc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;

public class BuildersTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
  }
  
  @Test
  public void testMyTransitionBuilder() throws IOException {
    ASTTransition transition = StatechartDSLMill.transitionBuilder().setFrom("setByGenBuilder").setFrom("xxxx").setTo("setByGenBuilder").build();
    assertEquals("xxxxSuf2", transition.getFrom());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHWCClassGeneratedBuilder() throws IOException {
    ASTStatechart aut = StatechartDSLMill.statechartBuilder().setName("setByGeneratedBuilder").build();
    assertEquals("setByGeneratedBuilder", aut.getName());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHWCClassHWCBuilder() throws IOException {
    ASTState state = StatechartDSLMill.stateBuilder().setName("x2").setFinal(true).setName("state1").build();
    assertEquals(state.getName(), "state1Suf1");
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
