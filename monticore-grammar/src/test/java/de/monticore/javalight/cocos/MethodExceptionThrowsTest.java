/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._symboltable.ITestJavaLightScope;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

public class MethodExceptionThrowsTest extends JavaLightCocoTest {
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0811.A0811";

  @BeforeEach
  public void initCoco() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new MethodExceptionThrows());
  }

  @Test
  public void testInvalid() {
    globalScope.add(TestJavaLightMill.oOTypeSymbolBuilder().setName("A").build());

    ITestJavaLightScope javaScope = TestJavaLightMill.scope();
    javaScope.setName("java");

    ITestJavaLightScope langScope = TestJavaLightMill.scope();
    langScope.setName("lang");

    javaScope.addSubScope(langScope);
    globalScope.addSubScope(javaScope);

    langScope.add(TestJavaLightMill.oOTypeSymbolBuilder().setName("Throwable").build());

    testInvalid(fileName, "meth1", MethodExceptionThrows.ERROR_CODE,
            String.format(MethodExceptionThrows.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect() {
    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", globalScope);

    ITestJavaLightScope javaScope = TestJavaLightMill.scope();
    javaScope.setName("java");

    ITestJavaLightScope langScope = TestJavaLightMill.scope();
    langScope.setName("lang");

    javaScope.addSubScope(langScope);
    globalScope.addSubScope(javaScope);

    globalScope.add(TestJavaLightMill.oOTypeSymbolBuilder().setName("A").addSuperTypes(sType).build());
    langScope.add(TestJavaLightMill.oOTypeSymbolBuilder().setName("Throwable").build());

    testValid("de.monticore.javalight.cocos.valid.MethodDecl", "meth1", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
