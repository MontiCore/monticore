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
 * Holds tests for {@link TimeIsValidCoCo}
 */
public class TimeIsValidCoCoTest {

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
    checker.addCoCo(new TimeIsValidCoCo());

    // When
    checker.checkAll(ast);

    // Then
    MCAssertions.assertNoFindings();
  }

  protected static Stream<Arguments> testValid() {
    return Stream.of(
        Arguments.of("at 12:00"),
        Arguments.of("at 12:00:00"),
        Arguments.of("at 23:59:59"),
        Arguments.of("at 23:59"),
        Arguments.of("at 00:00:00"),
        Arguments.of("at 0:0"),
        Arguments.of("at 000:000:000"),
        Arguments.of("at 0001:000001:00000000001")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void testInvalid(String input, List<String> expectedErrors) throws IOException {
    // Given
    ASTTimerCondition ast = parseTimer(input);
    TimerCoCoChecker checker = new TimerCoCoChecker();
    checker.addCoCo(new TimeIsValidCoCo());

    // When
    checker.checkAll(ast);

    // Then
    expectedErrors.forEach(MCAssertions::assertHasFindingStartingWith);
    MCAssertions.assertNoFindings();
  }

  public static Stream<Arguments> testInvalid() {
    return Stream.of(
        Arguments.of("at 00:00:60", List.of("0xA0915")),
        Arguments.of("at 00:60:00", List.of("0xA0915")),
        Arguments.of("at 60:00:00", List.of("0xA0915")),
        Arguments.of("at 01:600:060", List.of("0xA0915", "0xA0915")),
        Arguments.of("at 0600:01:060", List.of("0xA0915", "0xA0915")),
        Arguments.of("at 0600:600:01", List.of("0xA0915", "0xA0915")),
        Arguments.of("at 0600:600:060", List.of("0xA0915", "0xA0915", "0xA0915"))
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
