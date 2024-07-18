/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class MethodFormalParametersDifferentNameTest extends JavaLightCocoTest {
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0812.A0812";

  @BeforeEach
  public void initCoCo() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new MethodFormalParametersDifferentName());
  }

  @Test
  public void testInvalid() {
    testInvalid(fileName, "meth1", MethodFormalParametersDifferentName.ERROR_CODE,
            String.format(MethodFormalParametersDifferentName.ERROR_MSG_FORMAT, "i", "meth1"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalid(fileName+"a", "meth1", MethodFormalParametersDifferentName.ERROR_CODE,
        String.format(MethodFormalParametersDifferentName.ERROR_MSG_FORMAT, "i", "meth1"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.MethodDecl", "meth1", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
