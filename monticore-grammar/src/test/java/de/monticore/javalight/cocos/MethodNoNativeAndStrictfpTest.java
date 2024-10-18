/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class MethodNoNativeAndStrictfpTest extends JavaLightCocoTest{
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0819.A0819";

  @BeforeEach
  public void  initCoCo(){
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new MethodNoNativeAndStrictfp());}

  @Test
  public void testInvalid1(){
    testInvalid(fileName, "method", MethodNoNativeAndStrictfp.ERROR_CODE,
        String.format(MethodNoNativeAndStrictfp.ERROR_MESSAGE, "method"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0819", "method", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
