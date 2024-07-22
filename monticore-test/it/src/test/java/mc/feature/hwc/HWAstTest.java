/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;

public class HWAstTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testHWAstNodeClass() {
    ASTStatechart a = StatechartDSLMill.statechartBuilder().uncheckedBuild();
    a.setName("a");
    Assertions.assertEquals("My statechart is a", a.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHWInterfaceAstBaseNode() {
    ASTStatechart a = StatechartDSLMill.statechartBuilder().uncheckedBuild();
    Assertions.assertEquals("ASTStatechart", a.foo());
    
    ASTState b = StatechartDSLMill.stateBuilder().uncheckedBuild();
    Assertions.assertEquals("ASTState", b.foo());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHWAstNodeFactory() {
    // Call the method of the HW node factory
    ASTStatechart a = StatechartDSLMill.statechartBuilder().uncheckedBuild();
    Assertions.assertEquals("default", a.getName());
    
    // Call the method of the generated node factory
    ASTTransition b = StatechartDSLMill.transitionBuilder().uncheckedBuild();
    Assertions.assertNull(b.getFrom());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
