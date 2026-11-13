/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.isotemporals._ast.ASTISOPeriod;
import de.monticore.temporal.isotemporals.util.ISOPeriods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@TestWithMCLanguage(CombinedTemporalsTestMill.class)
public class ISOPeriodsTest {

  @DisplayName("ISO Period Classification")
  @ParameterizedTest(name = "Input: {0}")
  @CsvSource({
      "P2Y, CALENDAR_RELATIVE",
      "P3M10D, CALENDAR_RELATIVE",
      "P10D, CALENDAR_RELATIVE",
      "P10DT, CALENDAR_RELATIVE",
      "P4W, CALENDAR_RELATIVE",
      "P2Y3M10DT22H30M15S, CALENDAR_RELATIVE",
      "PT22H30M15S, FIXED",
      "PT30M, FIXED",
      "PT15.5S, FIXED",
      "P22H, FIXED"
  })
  void testClassify(String input, ISOPeriods.PeriodKind expected) {
    ASTTemporal ast = Assertions.assertDoesNotThrow(() ->
        CombinedTemporalsTestMill.parser().parse_String(input).orElseThrow());
    Assertions.assertTrue(ast.isPresentPeriod());
    Assertions.assertInstanceOf(ASTISOPeriod.class, ast.getPeriod());
    ASTISOPeriod period = (ASTISOPeriod) ast.getPeriod();

    Assertions.assertEquals(expected, ISOPeriods.classify(period));
    Assertions.assertEquals(expected == ISOPeriods.PeriodKind.CALENDAR_RELATIVE,
        ISOPeriods.isCalendarRelative(period));
  }

}
