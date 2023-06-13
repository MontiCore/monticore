/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConstructorModifiersValidTest extends JavaLightCocoTest{
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0820.A0820";

  @Before
  public void initCoCo() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new ConstructorModifiersValid());
  }

  @Test
  public void testInvalid() {
    testInvalid(fileName, "constructor", ConstructorModifiersValid.ERROR_CODE,
        String.format(ConstructorModifiersValid.ERROR_MSG_FORMAT, "constructor"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0820", "constructor", checker);
  
    assertTrue(Log.getFindings().isEmpty());
  }

}


