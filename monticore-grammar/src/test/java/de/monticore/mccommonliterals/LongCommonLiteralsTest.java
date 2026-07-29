/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class LongCommonLiteralsTest {
  
  static Stream<Arguments> checkLongLiteralArgs() {
    return Stream.of(
        // decimal number
        Arguments.of(0L, "0L"),
        Arguments.of(123L, "123L"),
        Arguments.of(10L, "10L"),
        Arguments.of(5L, "5L"),
        Arguments.of(5L, "05L"),
        Arguments.of(5L, "05L")
    );
  }

  @ParameterizedTest
  @MethodSource("checkLongLiteralArgs")
  public void checkLongLiteral(long l, String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTLiteral> lit = parser.parse_StringLiteral(s);
    assertTrue(lit.isPresent());
    assertInstanceOf(ASTBasicLongLiteral.class, lit.get());
    assertEquals(l, ((ASTBasicLongLiteral) lit.get()).getValue());
  }

  @ParameterizedTest
  @ValueSource(strings = { "0x12L", "0XeffL", "0x1234567890L", "0xabcdefL", "0x0L", "0xaL",
      "0xC0FFEEL", "0x005fL", "0 L" })
  public void checkFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTBasicLongLiteral> lit = parser.parse_StringBasicLongLiteral(s);
    assertFalse(lit.isPresent());
    
    Log.getFindings().removeAll(MCAssertions.assertHasFindingsStartingWith("rule basicLongLiteral failed predicate"));
  }
}
