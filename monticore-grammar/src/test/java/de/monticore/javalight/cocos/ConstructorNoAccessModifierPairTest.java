/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConstructorNoAccessModifierPairTest extends JavaLightCocoTest{
  private static final JavaLightCoCoChecker checker = new JavaLightCoCoChecker();
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0809.A0809";

  @BeforeClass
  public static void initCoCo() {
    checker.addCoCo(new ConstructorNoAccessModifierPair());
  }

  @Test
  public void testInvalid() {
    testInvalid(fileName, "constructor", ConstructorNoAccessModifierPair.ERROR_CODE,
        String.format(ConstructorNoAccessModifierPair.ERROR_MSG_FORMAT, "constructor"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0809", "constructor", checker);
  }

}
