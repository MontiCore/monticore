/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodFormalParametersVarargsNoArrayTest extends JavaLightCocoTest{

  private static final JavaLightCoCoChecker checker = new JavaLightCoCoChecker();
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0813.A0813";

  @BeforeClass
  public static void  initCoCo(){ checker.addCoCo(new MethodFormalParametersVarargsNoArray());}

  @Test
  public void testInvalid() {
    testInvalid(fileName, "method", MethodFormalParametersVarargsNoArray.ERROR_CODE,
        String.format(MethodFormalParametersVarargsNoArray.ERROR_MESSAGE, "x", "method"), checker);
  }


}
