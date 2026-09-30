/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MethodModifiersValidTest extends JavaLightCocoTest {
  
  private final String validFileName = "de.monticore.javalight.cocos.valid.A0822";
  private final String invalidFileName = "de.monticore.javalight.cocos.invalid.A0822.A0822";
  
  @BeforeEach
  public void initCoCo() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new MethodModifiersValid());
  }
  
  @Test
  public void testInvalid() {
    testInvalid(invalidFileName, "method", MethodModifiersValid.ERROR_CODE,
        String.format(MethodModifiersValid.ERROR_MSG_FORMAT, "method", "volatile"), checker);
  }
  
  @Test
  public void testCorrect() {
    testValid(validFileName, "method", checker);
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testCorrect2() {
    testValid(validFileName+"a", "method", checker);
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
