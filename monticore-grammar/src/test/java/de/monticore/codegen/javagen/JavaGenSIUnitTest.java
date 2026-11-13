// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import de.monticore.codegen.CodeGenSymTypeExpressionConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

public class JavaGenSIUnitTest extends AbstractJavaGenTest {

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("de.monticore.tests.expressionsandstatements.SIUnitTestModels#getSIUnitCases")
  public void testJavaGenSIUnitVals(String modelStr, Object expectedValue) {
    checkValue(modelStr, expectedValue);
  }

  /**
   * the type check does not allow conversions like dB -> B,
   * thus, the handler is tested directly
   */
  @Test
  public void testJavaGenSIUnitUnsupportedConversion() {
    JavaGenSymTypeExpressionConverter.init();
    SymTypeExpression doubleType =
        SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE);
    SymTypeExpression dB = SymTypeExpressionFactory.createNumericWithSIUnit(
        List.of(SymTypeExpressionFactory.createSIUnitBasic("dB")),
        List.of(), doubleType
    );
    SymTypeExpression b = SymTypeExpressionFactory.createNumericWithSIUnit(
        List.of(SymTypeExpressionFactory.createSIUnitBasic("B")),
        List.of(), doubleType
    );
    CodeGenSymTypeExpressionConverter.printConverted(
        new IndentPrinter(), b, dB, p -> p.print("x")
    );
    MCAssertions.assertHasFindingsStartingWith("0xFD240");
  }

}
