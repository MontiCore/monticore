/* (c) https://github.com/MontiCore/monticore */

package mc.emf.east;

import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GeneratedAstClassesTest extends GeneratorIntegrationsTest {
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Test
  public void testErrorsIfNullByAstNodes() {
    ASTState b = StatechartDSLNodeFactory.createASTState();
    thrown.expect(NullPointerException.class);
    // Log.errorIfNull is not generated
    // thrown.expectMessage("must not be null.");
    b.setTransitionsList(null);
  }
  
  @Test
  public void testErrorsIfNullByAstNodeFactories() {
    // Log.errorIfNull is not generated
    // thrown.expect(NullPointerException.class);
    // thrown.expectMessage("must not be null.");
    StatechartDSLNodeFactory.createASTCode(null);
  }
  
}
