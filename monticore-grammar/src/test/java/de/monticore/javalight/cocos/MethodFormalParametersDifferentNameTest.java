/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodFormalParametersDifferentNameTest extends JavaLightCocoTest {

  private static final JavaLightCoCoChecker checker = new JavaLightCoCoChecker();
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0812.A0812";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new MethodFormalParametersDifferentName());
  }

  @Test
  public void testInvalid() {
    testInvalid(fileName, "meth1", MethodFormalParametersDifferentName.ERROR_CODE,
            String.format(MethodFormalParametersDifferentName.ERROR_MSG_FORMAT, "i", "meth1"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.MethodDecl", "meth1", checker);
  }

}
