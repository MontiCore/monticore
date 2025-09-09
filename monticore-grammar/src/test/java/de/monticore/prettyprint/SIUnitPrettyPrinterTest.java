/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.siunit.siunits.SIUnitsMill;
import de.monticore.siunit.siunits._ast.ASTSIUnit;
import de.monticore.siunit.siunits._parser.SIUnitsParser;
import de.monticore.siunit.siunits._prettyprint.SIUnitsFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SIUnitPrettyPrinterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    SIUnitsMill.reset();
    SIUnitsMill.init();
  }

  @ParameterizedTest
  @MethodSource
  public void testSIUnitPrettyPrinting(String siunitStr) throws IOException {
    SIUnitsParser parser = SIUnitsMill.parser();
    Optional<ASTSIUnit> parsedOpt =
        parser.parseSIUnit(new StringReader(siunitStr));
    assertFalse(parser.hasErrors());
    assertTrue(parsedOpt.isPresent());
    ASTSIUnit parsed = parsedOpt.get();

    SIUnitsFullPrettyPrinter prettyPrinter =
        new SIUnitsFullPrettyPrinter(new IndentPrinter());
    String prettyPrinted = prettyPrinter.prettyprint(parsed);

    Optional<ASTSIUnit> parsedPrinted = parser.parse_String(prettyPrinted);
    assertFalse(parser.hasErrors());
    assertTrue(parsedOpt.isPresent());
    assertTrue(parsed.deepEquals(parsedPrinted.get()));

    assertTrue(Log.getFindings().isEmpty());
  }

  public static Stream<Arguments> testSIUnitPrettyPrinting() {
    return Stream.of(
        Arguments.of("m"),
        Arguments.of("m^2"),
        Arguments.of("m^1"),
        Arguments.of("m^0"),
        Arguments.of("dm"),
        Arguments.of("dm^2"),
        Arguments.of("m^2s"),
        Arguments.of("m^2s^2g"),
        Arguments.of("m^2ds^7g^4"),
        Arguments.of("°"),
        Arguments.of("°C"),
        Arguments.of("µm"),
        Arguments.of("Ω")
    );
  }
}
