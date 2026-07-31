/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class IntCommonLiteralsTest {
  
  static Stream<Arguments> checkIntLiteralArgs() {
    return Stream.of(
        // decimal number
        Arguments.of(0, "0"),
        Arguments.of(123, "123"),
        Arguments.of(10, "10"),
        Arguments.of(5, "5"),
        
        // number with leading 0
        Arguments.of(2, "02"),
        Arguments.of(7, "07"),
        Arguments.of(0, "00"),
        Arguments.of(76543210, "076543210"),
        Arguments.of(17, "00017")
    );
  }

  @ParameterizedTest
  @MethodSource("checkIntLiteralArgs")
  public void checkIntLiteral(int i, String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTLiteral> lit = parser.parse_StringLiteral(s);
    assertTrue(lit.isPresent());
    assertInstanceOf(ASTNatLiteral.class, lit.get());
    assertEquals(i, ((ASTNatLiteral) lit.get()).getValue());
  }
  
  static Stream<Arguments> checkSignedIntLiteralArgs() {
    return Stream.of(
        // decimal number
        Arguments.of(0, "0"),
        Arguments.of(-123, "-123"),
        Arguments.of(-10, "-10"),
        Arguments.of(-5, "-5"),
        
        // number with leading 0
        Arguments.of(-2, "-02"),
        Arguments.of(-7, "-07"),
        Arguments.of(0, "00"),
        Arguments.of(-76543210, "-076543210"),
        Arguments.of(-17, "-00017")
    );
  }

  @ParameterizedTest
  @MethodSource("checkSignedIntLiteralArgs")
  public void checkSignedIntLiteral(int i, String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTSignedNatLiteral> lit = parser.parse_StringSignedNatLiteral(s);
    assertTrue(lit.isPresent());
    assertInstanceOf(ASTSignedNatLiteral.class, lit.get());
    assertEquals(i, lit.get().getValue());
  }

  @ParameterizedTest
  @ValueSource(strings = { "0x12", "0Xeff", "0x34567890", "0xabcdef", "0x0", "0xa", "0xC0FFEE", "0x005f" })
  public void checkFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTNatLiteral> lit = parser.parse_StringNatLiteral(s);
    assertFalse(lit.isPresent());
    
    MCAssertions.assertHasFindingsStartingWith("Expected EOF but found token");
  }
  
  static Stream<Arguments> checkSignedFalseArgs() {
    return Stream.of(
        Arguments.of("0x12", "Expected EOF but found token"),
        Arguments.of("- 2", "no viable alternative at input '-'"),
        Arguments.of("- 02", "no viable alternative at input '-'")
    );
  }

  @ParameterizedTest
  @MethodSource("checkSignedFalseArgs")
  public void checkSignedFalse(String s, String expectedError) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTSignedNatLiteral> lit = parser.parse_StringSignedNatLiteral(s);
    assertFalse(lit.isPresent());
    
    MCAssertions.assertHasFindingStartingWith(expectedError);
  }
}
