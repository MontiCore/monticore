/* (c) https://github.com/MontiCore/monticore */
package mc.feature.hwc;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestWithMCLanguage(StatechartDSLMill.class)
public class BuildersTest {
  
  @Test
  public void testMyTransitionBuilder() {
    ASTTransition transition =
        StatechartDSLMill.transitionBuilder().setFrom("setByGenBuilder").setFrom("xxxx")
            .setTo("setByGenBuilder").build();
    assertEquals("xxxxSuf2", transition.getFrom());
  }
  
  @Test
  public void testHWCClassGeneratedBuilder() {
    ASTStatechart aut =
        StatechartDSLMill.statechartBuilder().setName("setByGeneratedBuilder").build();
    assertEquals("setByGeneratedBuilder", aut.getName());
  }
  
  @Test
  public void testHWCClassHWCBuilder() {
    ASTState state =
        StatechartDSLMill.stateBuilder().setName("x2").setFinal(true).setName("state1").build();
    assertEquals("state1Suf1", state.getName());
  }
}
