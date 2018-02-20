/* (c) https://github.com/MontiCore/monticore */
package mc.feature.hwc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLMill;

public class BuildersTest {
  
  @BeforeClass
  public static void init() {
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
  }
  
  @Test
  public void testHWCClassGeneratedBuilder() throws IOException {
    ASTStatechart aut = StatechartDSLMill.statechartBuilder().setName("setByGeneratedBuilder").build();
    assertEquals("setByGeneratedBuilder", aut.getName());
  }
  
  @Test
  public void testHWCClassHWCBuilder() throws IOException {
    ASTState state = StatechartDSLMill.stateBuilder().setName("x2").setFinal(true).setName("state1").build();
    assertEquals(state.getName(), "state1Suf1");
  }
  
}
