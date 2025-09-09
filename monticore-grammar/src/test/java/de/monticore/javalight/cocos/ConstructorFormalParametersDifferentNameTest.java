/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class ConstructorFormalParametersDifferentNameTest extends JavaLightCocoTest{
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0821.A0821";
  @BeforeEach
  public void initCoCo() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new ConstructorFormalParametersDifferentName());
  }

  @Test
  public void testInvalid() {
    testInvalid(fileName, "const1", ConstructorFormalParametersDifferentName.ERROR_CODE,
        String.format(ConstructorFormalParametersDifferentName.ERROR_MSG_FORMAT, "i", "const1"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalid(fileName+"a", "const1", ConstructorFormalParametersDifferentName.ERROR_CODE,
        String.format(ConstructorFormalParametersDifferentName.ERROR_MSG_FORMAT, "i", "const1"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0821", "const1", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}

