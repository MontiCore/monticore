/* (c) https://github.com/MontiCore/monticore */

package mc.feature.statechart;

import static org.junit.Assert.assertEquals;
import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactory;
import mc.feature.hwc.statechartdsl._ast.ASTState;

import org.junit.Test;

public class SCTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testToString() {
    ASTState a = StatechartDSLNodeFactory.createASTState();
    a.setName("a");
    assertEquals("a", a.toString());
  }
}
