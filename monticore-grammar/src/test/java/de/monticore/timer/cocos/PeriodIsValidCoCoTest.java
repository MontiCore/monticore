/* (c) https://github.com/MontiCore/monticore */
package de.monticore.timer.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.testtimer.TestTimerMill;
import de.monticore.timer._ast.ASTTimerCondition;
import de.monticore.timer._cocos.TimerCoCoChecker;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Holds tests for {@link PeriodIsValidCoCo}
 */
public class PeriodIsValidCoCoTest {

  @BeforeEach
  public void setup() {
    TestTimerMill.reset();
    LogStub.init();
    TestTimerMill.init();
    BasicSymbolsMill.initializePrimitives();
    new CombineExpressionsWithLiteralsTypeTraverserFactory().initTypeCheck3ForOO();
  }

  @ParameterizedTest
  @MethodSource
  public void testValid(String input) throws IOException {
    // Given
    ASTTimerCondition ast = parseTimer(input);
    TimerCoCoChecker checker = new TimerCoCoChecker();
    checker.addCoCo(new PeriodIsValidCoCo());

    // When
    checker.checkAll(ast);

    // Then
    MCAssertions.assertNoFindings();
  }

  protected static Stream<Arguments> testValid() {
    return Stream.of(
        Arguments.of("after 5s"),
        Arguments.of("after 5 s"),
        Arguments.of("after 10min"),
        Arguments.of("after 80min"),
        Arguments.of("after 80min"),
        Arguments.of("after 10h"),
        Arguments.of("after 10d"),
        Arguments.of("after 10ks"),
        Arguments.of("after 200d"),
        Arguments.of("after P2Y3M2W1DT2H13M20S"),
        Arguments.of("after P0M"),
        Arguments.of("after PT0M")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void testInvalid(String input, List<String> expectedErrors) throws IOException {
    // Given
    ASTTimerCondition ast = parseTimer(input);
    TimerCoCoChecker checker = new TimerCoCoChecker();
    checker.addCoCo(new PeriodIsValidCoCo());

    // When
    checker.checkAll(ast);

    // Then
    expectedErrors.forEach(MCAssertions::assertHasFindingStartingWith);
    MCAssertions.assertNoFindings();
  }

  public static Stream<Arguments> testInvalid() {
    return Stream.of(
        Arguments.of("after 5m", List.of("0xA0913")),
        Arguments.of("after 5km", List.of("0xA0913")),
        Arguments.of("after 5 m/s", List.of("0xA0913")),
        Arguments.of("after 10 s/m", List.of("0xA0913")),
        Arguments.of("after 10 1/s", List.of("0xA0913")),
        Arguments.of("after P", List.of("0xA0914")),
        Arguments.of("after PT", List.of("0xA0914"))
    );
  }

  // Helpers

  protected ASTTimerCondition parseTimer(String timerStr) throws IOException {
    Optional<ASTTimerCondition> timerOpt = TestTimerMill.parser().parse_String(timerStr);
    MCAssertions.assertNoFindings();
    assertTrue(timerOpt.isPresent());
    return timerOpt.get();
  }
}
