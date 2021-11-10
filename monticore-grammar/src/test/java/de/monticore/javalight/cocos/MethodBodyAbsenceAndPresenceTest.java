/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodBodyAbsenceAndPresenceTest extends JavaLightCocoTest{

  private static final JavaLightCoCoChecker checker = new JavaLightCoCoChecker();
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0804.A0804";

  @BeforeClass
  public static void  initCoCo(){ checker.addCoCo(new MethodBodyAbsenceAndPresence());}

  @Test
  public void testInvalid1(){
    testInvalid(fileName, "method", MethodBodyAbsenceAndPresence.ERROR_CODE,
        String.format(MethodBodyAbsenceAndPresence.ERROR_MESSAGE, "method"), checker);
  }
  @Test
  public void testInvalid2(){
    testInvalid(fileName + "a", "method", MethodBodyAbsenceAndPresence.ERROR_CODE,
        String.format(MethodBodyAbsenceAndPresence.ERROR_MESSAGE, "method"),checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0804", "method", checker);
  }

}
