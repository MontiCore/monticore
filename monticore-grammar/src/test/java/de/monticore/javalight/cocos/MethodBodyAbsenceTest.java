/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class MethodBodyAbsenceTest extends JavaLightCocoTest{
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0804.A0804";

  @BeforeEach
  public void  initCoCo(){
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new MethodBodyAbsence());}

  @Test
  public void testInvalid1(){
    testInvalid(fileName, "method", MethodBodyAbsence.ERROR_CODE,
        String.format(MethodBodyAbsence.ERROR_MESSAGE, "method"), checker);
  }
  @Test
  public void testInvalid2(){
    testInvalid(fileName + "a", "method", MethodBodyAbsence.ERROR_CODE,
        String.format(MethodBodyAbsence.ERROR_MESSAGE, "method"),checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0804", "method", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
