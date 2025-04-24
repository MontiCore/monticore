/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.runtime.types3.DefsTypesForTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.runtime.types3.DefsTypesForTests.oOtype;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class MethodExceptionThrowsTest extends JavaLightCocoTest {
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0811.A0811";

  @BeforeEach
  public void initCoco() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new MethodExceptionThrows());
  }

  @Test
  public void testInvalid() {
    globalScope.add(oOtype("A"));

    testInvalid(fileName, "meth1", MethodExceptionThrows.ERROR_CODE,
            String.format(MethodExceptionThrows.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect() {
    globalScope.add(oOtype("A", List.of(DefsTypesForTests._ThrowableSymType)));

    testValid("de.monticore.javalight.cocos.valid.MethodDecl", "meth1", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
