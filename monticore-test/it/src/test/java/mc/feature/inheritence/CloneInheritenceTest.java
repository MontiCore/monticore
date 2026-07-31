/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritence;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.inheritence.inheritence.InheritenceMill;
import mc.feature.inheritence.inheritence._ast.ASTSub;
import mc.feature.inheritence.inheritence._ast.ASTSuper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloneInheritenceTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() {

    ASTSuper s = InheritenceMill.superBuilder().setXQ(InheritenceMill.xQBuilder().uncheckedBuild()).uncheckedBuild();

    s.deepClone();

    ASTSub t = InheritenceMill.subBuilder()
        .setXQ(
            InheritenceMill.xQBuilder()
            .uncheckedBuild()
        )
        .setXP(
            InheritenceMill.xPBuilder()
            .uncheckedBuild()
        )
        .uncheckedBuild();

    t.deepClone();
    assertTrue(Log.getFindings().isEmpty());
  }

}
