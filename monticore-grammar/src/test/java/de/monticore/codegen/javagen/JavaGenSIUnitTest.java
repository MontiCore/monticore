// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JavaGenSIUnitTest extends AbstractJavaGenTest {

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("de.monticore.tests.expressionsandstatements.SIUnitTestModels#getSIUnitCases")
  public void testJavaGenSIUnitVals(String modelStr, Object expectedValue) {
    checkValue(modelStr, expectedValue);
  }

}
