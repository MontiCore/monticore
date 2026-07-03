// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.symbols.util.Class2MCTestUtil;
import de.monticore.tests.expressionsandstatements.Class2MCTestModels;
import de.monticore.tests.expressionsandstatements.rte.AClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class InterpreterClass2MCTest extends AbstractInterpreterTest {

  @BeforeEach
  public void setupAClass() {
    Class2MCTestUtil.addClassPathEntry(AClass.class);
    AClass.resetStaticVariables();
  }

  @ParameterizedTest
  @MethodSource("de.monticore.tests.expressionsandstatements.Class2MCTestModels#getClass2MCCases")
  void testNativeJava(String tail, Object expectedValue) {
    // not supported yet
    assumeFalse(Class2MCTestModels.getInstanceOfCases().anyMatch(a -> a.get()[0].equals(tail)));
    assumeFalse(Class2MCTestModels.getCreatorExpressionCases().anyMatch(a -> a.get()[0].equals(tail)));

    String modelStr = Class2MCTestModels.getModelPrefix() + tail;
    checkValue(modelStr, expectedValue);
  }

}
