/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@TestWithMCLanguage(CombinedTemporalsTestMill.class)
public class ISOPeriodParsingTest {
  CombinedTemporalsTestParser parser;

  @BeforeEach
  void setup() {
    parser = CombinedTemporalsTestMill.parser();
  }

  // These inputs pass the first parse step, which allows a fraction directly after a
  // designator. The second parse step must reject them with an error instead of throwing.
  @DisplayName("Period without digits before the fraction")
  @ParameterizedTest(name = "Input: {0}")
  @ValueSource(strings = {"P1Y.5M", "P1Y,5M", "PT.5H", "P1D,5H", "PT.5S"})
  void testPeriodWithoutDigitsBeforeFraction(String input) {
    Assertions.assertDoesNotThrow(() -> parser.parse_String(input));
    MCAssertions.assertHasFindings(Finding::isError);
  }
}
