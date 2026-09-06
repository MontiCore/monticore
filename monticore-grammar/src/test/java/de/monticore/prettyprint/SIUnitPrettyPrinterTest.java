/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.siunit.siunits.SIUnitsMill;
import de.monticore.siunit.siunits._ast.ASTSIUnit;
import de.monticore.siunit.siunits._parser.SIUnitsParser;
import de.monticore.siunit.siunits._prettyprint.SIUnitsFullPrettyPrinter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(SIUnitsMill.class)
public class SIUnitPrettyPrinterTest {

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
    assertTrue(parsedPrinted.isPresent());
    assertTrue(parsed.deepEquals(parsedPrinted.get()));
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
