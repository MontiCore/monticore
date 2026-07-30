/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class DoubleCommonLiteralsTest {

  static Stream<Arguments> checkDoubleLiteralArgs() {
    return Stream.of(
        // decimal number
        Arguments.of(0.0, "0.0"),
        Arguments.of(0.0, "0.0"),
        Arguments.of(3.0, "3.0"),
        Arguments.of(3.0, "3.0")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkDoubleLiteralArgs")
  public void checkDoubleLiteral(double d, String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTLiteral> lit = parser.parse_StringLiteral(s);
    assertTrue(lit.isPresent());
    assertInstanceOf(ASTBasicDoubleLiteral.class, lit.get());
    assertEquals(d, ((ASTBasicDoubleLiteral) lit.get()).getValue(), 0);
  }
  
  static Stream<Arguments> checkFalseArgs() {
    return Stream.of(
        Arguments.of(".0d", List.of("mismatched input 'd' expecting '.'", "extraneous input '.'")),
        Arguments.of("0.d", List.of("mismatched input 'd'")),
        Arguments.of("5d", List.of("mismatched input 'd'")),
        Arguments.of("009e2d", List.of("mismatched input 'e2d'")),
        Arguments.of("0 .0", List.of("rule basicDoubleLiteral failed predicate")),
        Arguments.of("0.0 d", List.of("Expected EOF but found token"))
    );
  }

  @ParameterizedTest
  @MethodSource("checkFalseArgs")
  public void checkFalse(String s, List<String> expectedErrors) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTBasicDoubleLiteral> lit = parser.parse_StringBasicDoubleLiteral(s);
    assertFalse(lit.isPresent());
    
    for (String expectedError : expectedErrors) {
      Log.getFindings().removeAll(
          MCAssertions.assertHasFindingsStartingWith(expectedError));
    }
  }
}
