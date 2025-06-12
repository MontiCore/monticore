package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.statements.mccommonstatements.cocos.SealedModifierUsage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SealedModifierPresenceTest extends JavaLightCocoTest {
  
  private final String fileName = "de.monticore.statements.cocos.A0913.A0913";
  
  @BeforeEach
  public void initCoCo() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new SealedModifierUsage());
  }
  
  @Test
  public void testInvalid1() {
    testInvalid(fileName, "method", SealedModifierUsage.ERROR_CODE,
        String.format(SealedModifierUsage.ERROR_MESSAGE, "sealed"), checker);
  }
  
  @Test
  public void testInvalid2() {
    testInvalid(fileName + "a", "method", SealedModifierUsage.ERROR_CODE,
        String.format(SealedModifierUsage.ERROR_MESSAGE, "non-sealed"), checker);
  }
  
  @Test
  public void testInvalid3() {
    testInvalid(fileName + "b", "method", SealedModifierUsage.ERROR_CODE,
        String.format(SealedModifierUsage.ERROR_MESSAGE, "sealed"), checker);
  }
}
