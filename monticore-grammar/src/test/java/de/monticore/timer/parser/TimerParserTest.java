/* (c) https://github.com/MontiCore/monticore */
package de.monticore.timer.parser;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.testtimer.TestTimerMill;
import de.monticore.timer._ast.ASTAfterPeriodCondition;
import de.monticore.timer._ast.ASTAtTimeCondition;
import de.monticore.timer._ast.ASTCronCondition;
import de.monticore.timer._ast.ASTEveryTimeCondition;
import de.monticore.timer._ast.ASTOnDateCondition;
import de.monticore.timer._ast.ASTTimerCondition;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimerParserTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    TestTimerMill.init();
  }

  @ParameterizedTest
  @MethodSource
  public void testTimerParsing(String input, Class<?> expectedExpression) throws IOException {
    // tests most relevant parser clashes + priority interaction
    ASTTimerCondition timer = parseTimer(input);
    assertInstanceOf(expectedExpression, timer);
  }

  public static Stream<Arguments> testTimerParsing() {
    return Stream.of(
        Arguments.of("at 12:00", ASTAtTimeCondition.class),
        Arguments.of("at 12:00:10", ASTAtTimeCondition.class),
        Arguments.of("at 0:0", ASTAtTimeCondition.class),
        Arguments.of("at 60:100:2000", ASTAtTimeCondition.class),
        Arguments.of("on 2025-08-14", ASTOnDateCondition.class),
        Arguments.of("on 25-15-50", ASTOnDateCondition.class),
        Arguments.of("on 2025-08-14 at 12:00", ASTOnDateCondition.class),
        Arguments.of("on 2025-08-14 at 12:00:10", ASTOnDateCondition.class),
        Arguments.of("on 2025-08-14 at 0:0", ASTOnDateCondition.class),
        Arguments.of("on 2025-08-14 at 60:100:2000", ASTOnDateCondition.class),
        Arguments.of("on 2025-08-14 at 60:100:2000", ASTOnDateCondition.class),
        Arguments.of("after 5s", ASTAfterPeriodCondition.class),
        Arguments.of("after 5 s", ASTAfterPeriodCondition.class),
        Arguments.of("after 10min", ASTAfterPeriodCondition.class),
        Arguments.of("after P2Y3M2W1DT2H13M20S", ASTAfterPeriodCondition.class),
        Arguments.of("after PT0M", ASTAfterPeriodCondition.class),
        Arguments.of("after PT", ASTAfterPeriodCondition.class),
        Arguments.of("after P", ASTAfterPeriodCondition.class),
        Arguments.of("every 1h", ASTEveryTimeCondition.class),
        Arguments.of("5 times every 1min", ASTEveryTimeCondition.class),
        Arguments.of("start on 2025-08-14, every 1min", ASTEveryTimeCondition.class),
        Arguments.of("start on 2025-08-14 at 12:00, every 1min", ASTEveryTimeCondition.class),
        Arguments.of("start on 2025-08-14 at 12:00 ,  10 times every 1min", ASTEveryTimeCondition.class),
        Arguments.of("cron \"5 4 * * *\"", ASTCronCondition.class),
        Arguments.of("cron \"*/3 * * * *\"", ASTCronCondition.class),
        Arguments.of("cron \"1-3 * * * *\"", ASTCronCondition.class),
        Arguments.of("cron \"23 0-20/2 * * *\"", ASTCronCondition.class),
        Arguments.of("cron \"5 4 * * sun\"", ASTCronCondition.class),
        Arguments.of("cron \"0 0,12 1 */2 *\"", ASTCronCondition.class),
        Arguments.of("cron \"@weekly\"", ASTCronCondition.class)
    );
  }

  @ParameterizedTest
  @MethodSource
  public void testInvalidConstructor(String input) throws IOException {
    assertNotATimer(input);
  }

  protected static Stream<Arguments> testInvalidConstructor() {
    return Stream.of(
        Arguments.of("at 12"),
        Arguments.of("at 12::10"),
        Arguments.of("on 2025-08"),
        Arguments.of("on 25--15-50"),
        Arguments.of("on 2025 - 08 - 30"),
        Arguments.of("on 2025-08-30-12"),
        Arguments.of("on -1-08-14"),
        Arguments.of("on 2025-08-14 every 12min"),
        Arguments.of("after 12:00"),
        Arguments.of("after reboot"),
        Arguments.of("after -1h"),
        Arguments.of("after P 2Y 3M 2W 1D"),
        Arguments.of("every 5"),
        Arguments.of("5h every 1min"),
        Arguments.of("5x every 1min"),
        Arguments.of("start on 2025-08-14 every 1min"),
        Arguments.of("start at 12:00 on 2025-08-14, every 1min"),
        Arguments.of("start on 2025-08-14 at 12:00 10 times, every 1min")
    );
  }

  @AfterAll
  public static void cleanUp() {
    TestTimerMill.reset();
  }

  // Helpers

  protected ASTTimerCondition parseTimer(String timerStr) throws IOException {
    Optional<ASTTimerCondition> timerOpt = TestTimerMill.parser().parse_String(timerStr);
    MCAssertions.assertNoFindings();
    assertTrue(timerOpt.isPresent());
    return timerOpt.get();
  }

  protected void assertNotATimer(String timerStr) throws IOException {
    Optional<ASTTimerCondition> timerOpt = TestTimerMill.parser().parse_String(timerStr);
    assertTrue(timerOpt.isEmpty());
    Log.clearFindings();
  }
}
