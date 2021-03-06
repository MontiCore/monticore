/* (c) https://github.com/MontiCore/monticore */

package mc.emf.east;

import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GeneratedAstClassesTest extends GeneratorIntegrationsTest {
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Test
  public void testErrorsIfNullByAstNodes() {
    ASTState b = StatechartDSLMill.stateBuilder().uncheckedBuild();
    thrown.expect(NullPointerException.class);
    // Log.errorIfNull is not generated
    // thrown.expectMessage("must not be null.");
    b.setTransitionsList(null);
  }

}
