/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testmcliteralsv2.TestMCLiteralsV2Mill;
import de.monticore.testmcliteralsv2._ast.*;
import de.monticore.testmcliteralsv2._parser.TestMCLiteralsV2Parser;
import de.se_rwth.commons.logging.Log;
import mcnumbers._ast.ASTDecimal;
import mcnumbers._ast.ASTInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stringliterals._ast.ASTCharLiteral;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnnecessaryUnicodeEscape")
@TestWithMCLanguage(TestMCLiteralsV2Mill.class)
public class MCLiteralsUnitTest {
  
  // setup the language infrastructure
  TestMCLiteralsV2Parser parser;
  
  @BeforeEach
  public void init() {
    parser = TestMCLiteralsV2Mill.parser();
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat for Cardinality
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testCardinalityToken() throws IOException {
    Optional<ASTAnyTokenList> astOpt = parser.parse_StringAnyTokenList( ":[65..67]:" );
    assertTrue(astOpt.isPresent());
    ASTAnyTokenList ast = astOpt.get();
    assertEquals(5, ast.sizeAnyTokens());
    ASTAnyToken t = ast.getAnyToken(0);
    t = ast.getAnyToken(1);
    assertTrue(t.isPresentDecimalToken());
    assertEquals("65", t.getDecimalToken());
    t = ast.getAnyToken(2);
    t = ast.getAnyToken(3);
    assertTrue(t.isPresentDecimalToken());
    assertEquals("67", t.getDecimalToken());
    t = ast.getAnyToken(4);
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testNat1() throws IOException {
    Optional<ASTDecimal> astOpt = parser.parse_StringDecimal( " 9" );
    assertTrue(astOpt.isPresent());
    ASTDecimal ast = astOpt.get();
    assertEquals("9", ast.getSource());
    assertEquals(9, ast.getValue());
    assertEquals(9, ast.getValueInt());
  }
  @Test
  public void testNat2() throws IOException {
    Optional<ASTDecimal> astOpt = parser.parse_StringDecimal( " 0" );
    assertTrue(astOpt.isPresent());
    ASTDecimal ast = astOpt.get();
    assertEquals("0", ast.getSource());
    assertEquals(0, ast.getValue());
  }
  @Test
  public void testNat3() throws IOException {
    Optional<ASTDecimal> os = parser.parse_StringDecimal( " 00 0 " );
    assertFalse(os.isPresent());
    
    Log.getFindings()
        .remove(MCAssertions.assertHasFindingStartingWith("Expected EOF but found token"));
  }
  @Test
  public void testNat4() throws IOException {
    Optional<ASTDecimal> astOpt = parser.parse_StringDecimal( " 23 " );
    assertTrue(astOpt.isPresent());
    ASTDecimal ast = astOpt.get();
    assertEquals("23", ast.getSource());
    assertEquals(23, ast.getValue());
    assertEquals(23, ast.getValueInt());
  }
  @Test
  public void testNat5() throws IOException {
    Optional<ASTDecimal> astOpt = parser.parse_StringDecimal( " 463 " );
    assertTrue(astOpt.isPresent());
    ASTDecimal ast = astOpt.get();
    assertEquals(463, ast.getValue());
  }

  // --------------------------------------------------------------------
  @Test
  public void testNat6() throws IOException {
    Optional<ASTDecimal> os = parser.parse_StringDecimal( " 0x23 " );
    assertFalse(os.isPresent());
    
    Log.getFindings()
        .remove(MCAssertions.assertHasFindingStartingWith("Expected EOF but found token"));
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens() throws IOException {
    Optional<ASTAnyTokenList> astOpt = parser.parse_StringAnyTokenList( ":463 23:" );
    assertTrue(astOpt.isPresent());
    ASTAnyTokenList ast = astOpt.get();
    assertEquals(2, ast.sizeAnyTokens());
    ASTAnyToken a0 = ast.getAnyToken(0);
    assertTrue(a0.isPresentDecimalToken());
    assertEquals("463", a0.getDecimalToken());
    ASTAnyToken a1 = ast.getAnyToken(1);
    assertTrue(a1.isPresentDecimalToken());
    assertEquals("23", a1.getDecimalToken());
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens2() throws IOException {
    Optional<ASTAnyTokenList> astOpt = parser.parse_StringAnyTokenList(
      ":9 'a' 45 00 47:" );
    assertTrue(astOpt.isPresent());
    ASTAnyTokenList ast = astOpt.get();
    assertEquals(6, ast.sizeAnyTokens());
    assertEquals("9", ast.getAnyToken(0).getDecimalToken());
    assertEquals("a", ast.getAnyToken(1).getCharToken());
    assertEquals("45", ast.getAnyToken(2).getDecimalToken());
    // Observe the separated '0's!
    assertEquals("0", ast.getAnyToken(3).getDecimalToken());
    assertEquals("0", ast.getAnyToken(4).getDecimalToken());
    assertEquals("47", ast.getAnyToken(5).getDecimalToken());
  }

  // --------------------------------------------------------------------
  @Test
  public void testAbstractInterfaceFunctions() throws IOException {
    Optional<ASTDecimal> astOpt = parser.parse_StringDecimal( " 234 " );
    assertTrue(astOpt.isPresent());
    ASTDecimal ast = astOpt.get();
    assertEquals(234, ast.getValue());
    assertEquals(234, ast.getValueInt());
    assertEquals("234", ast.getSource());
  }

  // --------------------------------------------------------------------
  // Numbers: Integer
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testInt() throws IOException {
    Optional<ASTInteger> astOpt = parser.parse_StringInteger( " -463 " );
    assertTrue(astOpt.isPresent());
    ASTInteger ast = astOpt.get();
    assertEquals(-463, ast.getValue());
    assertEquals(-463, ast.getValueInt());
    assertEquals("-463", ast.getSource());
  }

  // --------------------------------------------------------------------
  @Test
  public void testIntTokens2() throws IOException {
    Optional<ASTIntegerList> astOpt = parser.parse_StringIntegerList(
        "[9, -45, -0, - 47]" );
    assertTrue(astOpt.isPresent());
    ASTIntegerList ast = astOpt.get();
    assertEquals(4, ast.sizeIntegers());
    assertEquals(9, ast.getInteger(0).getValue());
    assertEquals("9", ast.getInteger(0).getSource());
    assertEquals(-45, ast.getInteger(1).getValue());
    assertEquals("-45", ast.getInteger(1).getSource());
    assertEquals(0, ast.getInteger(2).getValue());
    // "-" is still present
    assertEquals("-0", ast.getInteger(2).getSource());
    assertEquals(-47, ast.getInteger(3).getValue());
    // space between the two token is missing
    assertEquals("-47", ast.getInteger(3).getSource());
  }

  // --------------------------------------------------------------------
  @Test
  public void testIntNEG() throws IOException {
    Optional<ASTInteger> os = parser.parse_StringInteger( " 0x34 " );
    assertFalse(os.isPresent());
    
    Log.getFindings()
        .remove(MCAssertions.assertHasFindingStartingWith("Expected EOF but found token"));
  }

  // --------------------------------------------------------------------
  // test of the Test-Literal B
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testB() throws IOException {
    Optional<ASTBTest> astOpt = parser.parse_StringBTest( " X2X, XFF001DX" );
    assertTrue(astOpt.isPresent());
    ASTBTest ast = astOpt.get();
    assertEquals("X2X", ast.getXHexDigit(0));
    assertEquals("XFF001DX", ast.getXHexDigit(1));
  }

  
  // --------------------------------------------------------------------
  // String
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testString() throws IOException {
    Optional<ASTStringList> astOpt = parser.parse_StringStringList(
     "[\"ZWeR\",\"4\", \"',\\b,\\\\;\", \"S\\u34F4W\", \"o\"]" );
    assertTrue(astOpt.isPresent());
    ASTStringList ast = astOpt.get();
    assertEquals("ZWeR", ast.getStringLiteral(0).getValue());
    assertEquals("4", ast.getStringLiteral(1).getValue());
    assertEquals("',\b,\\;", ast.getStringLiteral(2).getValue());
    assertEquals("S\u34F4W", ast.getStringLiteral(3).getValue());
    assertEquals("o", ast.getStringLiteral(4).getValue());

    // repeat wg. buffering
    assertEquals("ZWeR", ast.getStringLiteral(0).getValue());
  }

  // --------------------------------------------------------------------
  // Char
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testChar() throws IOException {
    Optional<ASTCharLiteral> astOpt = parser.parse_StringCharLiteral( " 'h'" );
    assertTrue(astOpt.isPresent());
    ASTCharLiteral ast = astOpt.get();
    assertEquals("h", ast.getSource());
    assertEquals('h', ast.getValue());
  }

  // --------------------------------------------------------------------
  @Test
  public void testChar2() throws IOException {
    Optional<ASTCharList> astOpt = parser.parse_StringCharList(
     "['Z','4','\\'', '\\b', '\\\\', '\7', '\\7', 'o']" );
    assertTrue(astOpt.isPresent());
    ASTCharList ast = astOpt.get();
    assertEquals('Z', ast.getCharLiteral(0).getValue());
    assertEquals('4', ast.getCharLiteral(1).getValue());
    assertEquals('\'', ast.getCharLiteral(2).getValue());
    assertEquals('\b', ast.getCharLiteral(3).getValue());
    assertEquals('\\', ast.getCharLiteral(4).getValue());
    // Encoded by Java
    assertEquals('\7', ast.getCharLiteral(5).getValue());
    assertEquals('o', ast.getCharLiteral(7).getValue());
  }

  // --------------------------------------------------------------------
  // --------------------------------------------------------------------
  @Test
  public void testCharUnicode() throws IOException {
    Optional<ASTCharList> astOpt = parser.parse_StringCharList(
     "['\\u2345', '\\u23EF', '\\u0001', '\\uAFFA']" );
    assertTrue(astOpt.isPresent());
    ASTCharList ast = astOpt.get();
    assertEquals('\u2345', ast.getCharLiteral(0).getValue());
    assertEquals('\u23EF', ast.getCharLiteral(1).getValue());
    assertEquals('\u0001', ast.getCharLiteral(2).getValue());
    assertEquals('\uAFFA', ast.getCharLiteral(3).getValue());
  }

}

