// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.symbols.util.Class2MCTestUtil;
import de.monticore.tests.expressionsandstatements.Class2MCTestModels;
import de.monticore.tests.expressionsandstatements.rte.AClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class InterpreterClass2MCTest extends AbstractInterpreterTest {

  @BeforeEach
  public void setupAClass() {
    Class2MCTestUtil.addClassPathEntry(AClass.class);
  }

  @ParameterizedTest
  @MethodSource("de.monticore.tests.expressionsandstatements.Class2MCTestModels#getNativeJavaCases")
  void testNativeJava(String modelStr, Object expectedValue) {
    checkValue(modelStr, expectedValue);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("de.monticore.tests.expressionsandstatements.Class2MCTestModels#getAClassCases")
  void testNativeJavaAClass(String tail, Object expectedValue) {
    String modelStr = Class2MCTestModels.getModelPrefix() + tail;
    checkValue(modelStr, expectedValue);
  }

}
