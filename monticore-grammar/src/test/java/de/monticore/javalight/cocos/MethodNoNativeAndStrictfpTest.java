/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MethodNoNativeAndStrictfpTest extends JavaLightCocoTest{
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0819.A0819";

  @Before
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
