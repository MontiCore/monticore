/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MethodNoDuplicateModifierTest extends JavaLightCocoTest {
  
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0818.A0818";
  
  @BeforeEach
  public void initCoCo() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new MethodNoDuplicateModifier());
  }
  
  @Test
  public void testInvalid() {
    testInvalid(fileName, "method", MethodNoDuplicateModifier.ERROR_CODE,
        String.format(MethodNoDuplicateModifier.ERROR_MSG_FORMAT, "public", "method"), checker);
  }
  
  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0818", "method", checker);
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
