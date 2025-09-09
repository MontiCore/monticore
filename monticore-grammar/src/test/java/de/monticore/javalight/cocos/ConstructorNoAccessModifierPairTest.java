/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class ConstructorNoAccessModifierPairTest extends JavaLightCocoTest{
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0809.A0809";

  @BeforeEach
  public void initCoCo() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new ConstructorNoAccessModifierPair());
  }

  @Test
  public void testInvalid() {
    testInvalid(fileName, "constructor", ConstructorNoAccessModifierPair.ERROR_CODE,
        String.format(ConstructorNoAccessModifierPair.ERROR_MSG_FORMAT, "constructor", "StringReader:<2,0>"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0809", "constructor", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
