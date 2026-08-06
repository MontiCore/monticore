/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestWithMCLanguage(StatechartDSLMill.class)
public class HWAstTest {

  @Test
  public void testHWAstNodeClass() {
    ASTStatechart a = StatechartDSLMill.statechartBuilder().uncheckedBuild();
    a.setName("a");
    assertEquals("My statechart is a", a.toString());
  }
  
  @Test
  public void testHWInterfaceAstBaseNode() {
    ASTStatechart a = StatechartDSLMill.statechartBuilder().uncheckedBuild();
    assertEquals("ASTStatechart", a.foo());
    
    ASTState b = StatechartDSLMill.stateBuilder().uncheckedBuild();
    assertEquals("ASTState", b.foo());
  }
  
  @Test
  public void testHWAstNodeFactory() {
    // Call the method of the HW node factory
    ASTStatechart a = StatechartDSLMill.statechartBuilder().uncheckedBuild();
    assertEquals("default", a.getName());
    
    // Call the method of the generated node factory
    ASTTransition b = StatechartDSLMill.transitionBuilder().uncheckedBuild();
    assertNull(b.getFrom());
  }
  
}
