/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritence;

import mc.GeneratorIntegrationsTest;
import mc.feature.inheritence.inheritence.InheritenceMill;
import mc.feature.inheritence.inheritence._ast.ASTSub;
import mc.feature.inheritence.inheritence._ast.ASTSuper;

import org.junit.Test;

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
  }

}
