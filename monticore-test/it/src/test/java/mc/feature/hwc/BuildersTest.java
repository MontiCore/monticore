/* (c) https://github.com/MontiCore/monticore */
package mc.feature.hwc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.se_rwth.commons.logging.Log;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;

public class BuildersTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
  }
  
  @Test
  public void testMyTransitionBuilder() throws IOException {
    ASTTransition transition = StatechartDSLMill.transitionBuilder().setFrom("setByGenBuilder").setFrom("xxxx").setTo("setByGenBuilder").build();
    Assertions.assertEquals("xxxxSuf2", transition.getFrom());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHWCClassGeneratedBuilder() throws IOException {
    ASTStatechart aut = StatechartDSLMill.statechartBuilder().setName("setByGeneratedBuilder").build();
    Assertions.assertEquals("setByGeneratedBuilder", aut.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHWCClassHWCBuilder() throws IOException {
    ASTState state = StatechartDSLMill.stateBuilder().setName("x2").setFinal(true).setName("state1").build();
    Assertions.assertEquals(state.getName(), "state1Suf1");
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
