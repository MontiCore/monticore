/* (c) https://github.com/MontiCore/monticore */
package mc.feature.hwc;

import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.se_rwth.commons.logging.Log;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuildersTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUp() throws RecognitionException {
    Log.getFindings().clear();
  }
  
  @Test
  public void testMyTransitionBuilder() {
    ASTTransition transition = StatechartDSLMill.transitionBuilder().setFrom("setByGenBuilder").setFrom("xxxx").setTo("setByGenBuilder").build();
    assertEquals("xxxxSuf2", transition.getFrom());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHWCClassGeneratedBuilder() {
    ASTStatechart aut = StatechartDSLMill.statechartBuilder().setName("setByGeneratedBuilder").build();
    assertEquals("setByGeneratedBuilder", aut.getName());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHWCClassHWCBuilder() {
    ASTState state = StatechartDSLMill.stateBuilder().setName("x2").setFinal(true).setName("state1").build();
    assertEquals("state1Suf1", state.getName());
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
