/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.testjavalight.TestJavaLightMill;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodExceptionThrowsTest extends JavaLightCocoTest {

  private static final JavaLightCoCoChecker checker = new JavaLightCoCoChecker();
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0811.A0811";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new MethodExceptionThrows());
  }

  @Test
  public void testInvalid() {
    globalScope.add(TestJavaLightMill.oOTypeSymbolBuilder().setName("A").build());
    globalScope.add(TestJavaLightMill.oOTypeSymbolBuilder().setName("java.lang.Throwable").build());

    testInvalid(fileName, "meth1", MethodExceptionThrows.ERROR_CODE,
            String.format(MethodExceptionThrows.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect(){
    testValid("de.monticore.javalight.cocos.valid.MethodDecl", checker);
  }

}
