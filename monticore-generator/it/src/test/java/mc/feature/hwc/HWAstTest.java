/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactory;

public class HWAstTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testHWAstNodeClass() {
    ASTStatechart a = StatechartDSLNodeFactory.createASTStatechart();
    a.setName("a");
    assertEquals("My statechart is a", a.toString());
  }
  
  @Test
  public void testHWInterfaceAstBaseNode() {
    ASTStatechart a = StatechartDSLNodeFactory.createASTStatechart();
    assertEquals("ASTStatechart", a.foo());
    
    ASTState b = StatechartDSLNodeFactory.createASTState();
    assertEquals("ASTState", b.foo());
  }
  
  @Test
  public void testHWAstNodeFactory() {
    // Call the method of the HW node factory
    ASTStatechart a = StatechartDSLNodeFactory.createASTStatechart();
    assertEquals("default", a.getName());
    
    // Call the method of the generated node factory
    ASTTransition b = StatechartDSLNodeFactory.createASTTransition();
    assertNull(b.getFrom());
  }
  
}
