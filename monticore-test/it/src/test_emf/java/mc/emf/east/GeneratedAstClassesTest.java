/* (c) https://github.com/MontiCore/monticore */

package mc.emf.east;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl.StatechartDSLMill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestWithMCLanguage(StatechartDSLMill.class)
public class GeneratedAstClassesTest {
  
  @Test
  public void testErrorsIfNullByAstNodes() {
    ASTState b = StatechartDSLMill.stateBuilder().uncheckedBuild();
    // Preconditions.checkNotNull is not generated
    // NullPointerException is thrown
    assertThrows(NullPointerException.class, () -> b.setTransitionsList(null));
  }
}
