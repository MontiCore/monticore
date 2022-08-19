/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

public class MethodExceptionThrowsTest extends JavaLightCocoTest {

  private static final JavaLightCoCoChecker checker = new JavaLightCoCoChecker();
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0811.A0811";

  @BeforeClass
  public static void initCoco() {
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
  public void testCorrect() {
    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", globalScope);
    globalScope.add(TestJavaLightMill.oOTypeSymbolBuilder().setName("A").addSuperTypes(sType).build());
    globalScope.add(TestJavaLightMill.oOTypeSymbolBuilder().setName("java.lang.Throwable").build());

    testValid("de.monticore.javalight.cocos.valid.MethodDecl", "meth1", checker);
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
