// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements.codegen.javagen.ExpressionsAndStatementsJavaGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class JavaGenSIUnitTest extends AbstractJavaGenTest {

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("de.monticore.tests.expressionsandstatements.SIUnitTestModels#getSIUnitCases")
  public void testJavaGenSIUnitVals(String modelStr, Object expectedValue) {
    checkValue(modelStr, expectedValue);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @ValueSource(strings = {
      "1deg + 1rad",
      "[rad]<int> x = 1deg; x",
      "1dB == 1B",
  })
  public void testJavaGenSIUnitUnsupportedConversion(String modelStr) {
    ASTBehaviorInput ast = testTool.getASTWithSymbolTable(modelStr);
    new ExpressionsAndStatementsJavaGenerator().generateCode(ast);
    MCAssertions.assertHasFindingsStartingWith("0xFD240");
  }

}
