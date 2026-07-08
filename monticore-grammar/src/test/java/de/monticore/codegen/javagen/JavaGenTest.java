// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import de.monticore.symbols.util.Class2MCTestUtil;
import de.monticore.tests.expressionsandstatements.Class2MCTestModels;
import de.monticore.tests.expressionsandstatements.rte.AClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class JavaGenTest extends AbstractJavaGenTest {

  // small set of tests for the alpha version of the generator,
  // to be severely extended in the future.

  @BeforeEach
  public void setupAClass() {
    Class2MCTestUtil.addClassPathEntry(AClass.class);
  }

  @ParameterizedTest
  @MethodSource("de.monticore.tests.expressionsandstatements.ExpressionsTestModels#getExpressionsCases")
  public void testJavaGenExprVals(String modelStr, Object expectedValue) {
    // missing implementations
    assumeFalse(modelStr.contains("<<"));
    assumeFalse(modelStr.contains(">>"));
    assumeFalse(modelStr.contains(">>>"));
    assumeFalse(modelStr.contains(" ^ "));
    assumeFalse(modelStr.equals("6 & 3"));
    assumeFalse(modelStr.equals("6 | 3"));
    // needs better checkValue, as this is not easily tested with Strings
    assumeFalse(expectedValue instanceof Set<?>);

    checkValue(modelStr, expectedValue);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("de.monticore.tests.expressionsandstatements.ExpressionsTestModels#getOptionalSimilarOperatorCases")
  public void testJavaGenOptionalSimilarExprVals(String modelStr, Object expectedValue) {
    checkValue(modelStr, expectedValue);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("de.monticore.tests.expressionsandstatements.Class2MCTestModels#getClass2MCCases")
  void testNativeJavaAClass(String tail, Object expectedValue) {
    // not supported yet, implementation missing
    assumeFalse(tail.contains(".set_"));
    // empty collections are not supported yet
    assumeFalse(expectedValue != null && expectedValue.toString().equals("[]"));

    String modelStr = Class2MCTestModels.getModelPrefix() + tail;
    checkValue(modelStr, expectedValue);
  }

}
