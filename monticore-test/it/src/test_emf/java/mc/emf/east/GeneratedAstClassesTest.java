/* (c) https://github.com/MontiCore/monticore */

package mc.emf.east;

import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GeneratedAstClassesTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testErrorsIfNullByAstNodes() {
    ASTState b = StatechartDSLMill.stateBuilder().uncheckedBuild();
    // Preconditions.checkNotNull is not generated
    // NullPointerException is thrown
    assertThrows(NullPointerException.class, () -> b.setTransitionsList(null));
  }

}
