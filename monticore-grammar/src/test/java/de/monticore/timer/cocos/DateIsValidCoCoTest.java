/* (c) https://github.com/MontiCore/monticore */
package de.monticore.timer.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.testtimer.TestTimerMill;
import de.monticore.timer._ast.ASTTimerCondition;
import de.monticore.timer._cocos.TimerCoCoChecker;
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
 * Holds tests for {@link DateIsValidCoCo}
 */
public class DateIsValidCoCoTest {

  @BeforeEach
  public void setup() {
    TestTimerMill.reset();
    LogStub.init();
    TestTimerMill.init();
  }

  @ParameterizedTest
  @MethodSource
  public void testValid(String input) throws IOException {
    // Given
    ASTTimerCondition ast = parseTimer(input);
    TimerCoCoChecker checker = new TimerCoCoChecker();
    checker.addCoCo(new DateIsValidCoCo());

    // When
    checker.checkAll(ast);

    // Then
    MCAssertions.assertNoFindings();
  }

  protected static Stream<Arguments> testValid() {
    return Stream.of(
        Arguments.of("on 2025-08-15"),
        Arguments.of("on 2024-02-29"),
        Arguments.of("on 2025-02-28"),
        Arguments.of("on 0000-01-01"),
        Arguments.of("on 205-1-1")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void testInvalid(String input, List<String> expectedErrors) throws IOException {
    // Given
    ASTTimerCondition ast = parseTimer(input);
    TimerCoCoChecker checker = new TimerCoCoChecker();
    checker.addCoCo(new DateIsValidCoCo());

    // When
    checker.checkAll(ast);

    // Then
    expectedErrors.forEach(MCAssertions::assertHasFindingStartingWith);
    MCAssertions.assertNoFindings();
  }

  public static Stream<Arguments> testInvalid() {
    return Stream.of(
        Arguments.of("on 2025-11-31", List.of("0xA0916")),
        Arguments.of("on 2025-02-29", List.of("0xA0916")),
        Arguments.of("on 2025-30-01", List.of("0xA0916")),
        Arguments.of("on 2025-13-32", List.of("0xA0916")),
        Arguments.of("on 2025-00-01", List.of("0xA0916")),
        Arguments.of("on 2025-01-00", List.of("0xA0916")),
        Arguments.of("on 2025-0-0", List.of("0xA0916"))
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
