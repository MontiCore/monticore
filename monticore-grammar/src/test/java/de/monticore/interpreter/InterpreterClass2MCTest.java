// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.tests.expressionsandstatements.Class2MCTestModels;
import de.monticore.tests.expressionsandstatements.rte.AClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterClass2MCTest extends AbstractInterpreterTest {

  @BeforeEach
  public void setupAClass() {
    addClassPathEntry(AClass.class);
  }

  @ParameterizedTest
  @MethodSource("de.monticore.tests.expressionsandstatements.Class2MCTestModels#getNativeJavaCases")
  void testNativeJava(String modelStr, Object expectedValue) {
    assertEquals(expectedValue, interpretAndCast(modelStr));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("de.monticore.tests.expressionsandstatements.Class2MCTestModels#getAClassCases")
  void testNativeJavaAClass(String tail, Object expectedValue) {
    String modelStr = Class2MCTestModels.getAClassPrefix() + tail;
    assertEquals(expectedValue, interpretAndCast(modelStr));
  }

}
