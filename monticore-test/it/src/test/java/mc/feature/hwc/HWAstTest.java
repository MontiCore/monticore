/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;

public class HWAstTest extends GeneratorIntegrationsTest {
  
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
