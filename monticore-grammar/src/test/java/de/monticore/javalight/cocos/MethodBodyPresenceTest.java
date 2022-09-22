/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MethodBodyPresenceTest extends JavaLightCocoTest{
  private static final JavaLightCoCoChecker checker = new JavaLightCoCoChecker();
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0803.A0803";

  @BeforeClass
  public static void  initCoCo(){ checker.addCoCo(new MethodBodyPresence());}

  @Test
  public void testInvalid1(){
    testInvalid(fileName, "method", MethodBodyPresence.ERROR_CODE,
        String.format(MethodBodyPresence.ERROR_MESSAGE, "method"), checker);
  }
  @Test
  public void testInvalid2(){
    testInvalid(fileName + "a", "method", MethodBodyPresence.ERROR_CODE,
        String.format(MethodBodyPresence.ERROR_MESSAGE, "method"),checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0803", "method", checker);
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
