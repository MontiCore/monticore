/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class MethodAbstractAndOtherModifiersTest extends JavaLightCocoTest{

  private final String fileName = "de.monticore.javalight.cocos.invalid.A0802.A0802";

  @BeforeEach
  public void initCoco() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new MethodAbstractAndOtherModifiers());
  }

  @Test
  public void testInvalid() {
    testInvalid(fileName, "a", MethodAbstractAndOtherModifiers.ERROR_CODE,
        String.format(MethodAbstractAndOtherModifiers.ERROR_MSG_FORMAT, "a"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0802",
        "a", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


}
