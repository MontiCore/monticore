/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritence;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.inheritence.inheritence.InheritenceMill;
import mc.feature.inheritence.inheritence._ast.ASTSub;
import mc.feature.inheritence.inheritence._ast.ASTSuper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CloneInheritenceTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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
