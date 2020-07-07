/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.testmcliteralsv2._ast.*;
import de.monticore.testmcliteralsv2._parser.TestMCLiteralsV2Parser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mcnumbers._ast.ASTDecimal;
import mcnumbers._ast.ASTInteger;
import mcnumbers._ast.ASTNumber;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import stringliterals._ast.ASTCharLiteral;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// import de.monticore.mcliteralsv2._ast.*;

public class MCLiteralsUnitTest {
  
  // setup the language infrastructure
  TestMCLiteralsV2Parser parser = new TestMCLiteralsV2Parser() ;
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  

  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat for Cardinality
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testCardinalityToken() throws IOException {
    ASTAnyTokenList ast = parser.parse_StringAnyTokenList( ":[65..67]:" ).get();
    assertEquals(5, ast.sizeAnyTokens());
    ASTAnyToken t = ast.getAnyTokens(0);
    t = ast.getAnyTokens(1);
    assertTrue(t.isPresentDecimalToken());
    assertEquals("65", t.getDecimalToken());
    t = ast.getAnyTokens(2);
    t = ast.getAnyTokens(3);
    assertTrue(t.isPresentDecimalToken());
    assertEquals("67", t.getDecimalToken());
    t = ast.getAnyTokens(4);
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testNat1() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 9" ).get();
    assertEquals("9", ast.getSource());
    assertEquals(9, ast.getValue());
    assertEquals(9, ast.getValueInt());
  }
  @Test
  public void testNat2() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 0" ).get();
    assertEquals("0", ast.getSource());
    assertEquals(0, ast.getValue());
  }
  @Test
  public void testNat3() throws IOException {
    Optional<ASTDecimal> os = parser.parse_StringDecimal( " 00 0 " );
    assertEquals(false, os.isPresent());
  }
  @Test
  public void testNat4() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 23 " ).get();
    assertEquals("23", ast.getSource());
    assertEquals(23, ast.getValue());
    assertEquals(23, ast.getValueInt());
  }
  @Test
  public void testNat5() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 463 " ).get();
    assertEquals(463, ast.getValue());
  }

  // --------------------------------------------------------------------
  @Test
  public void testNat6() throws IOException {
    Optional<ASTDecimal> os = parser.parse_StringDecimal( " 0x23 " );
    assertEquals(false, os.isPresent());
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens() throws IOException {
    ASTAnyTokenList ast = parser.parse_StringAnyTokenList( ":463 23:" ).get();
    assertEquals(2, ast.sizeAnyTokens());
    ASTAnyToken a0 = ast.getAnyTokens(0);
    assertTrue(a0.isPresentDecimalToken());
    assertEquals("463", a0.getDecimalToken());
    ASTAnyToken a1 = ast.getAnyTokens(1);
    assertTrue(a1.isPresentDecimalToken());
    assertEquals("23", a1.getDecimalToken());
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens2() throws IOException {
    ASTAnyTokenList ast = parser.parse_StringAnyTokenList(
      ":9 'a' 45 00 47:" ).get();
    assertEquals(6, ast.sizeAnyTokens());
    assertEquals("9", ast.getAnyTokens(0).getDecimalToken());
    assertEquals("a", ast.getAnyTokens(1).getCharToken());
    assertEquals("45", ast.getAnyTokens(2).getDecimalToken());
    // Observe the separated '0's!
    assertEquals("0", ast.getAnyTokens(3).getDecimalToken());
    assertEquals("0", ast.getAnyTokens(4).getDecimalToken());
    assertEquals("47", ast.getAnyTokens(5).getDecimalToken());
  }

  // --------------------------------------------------------------------
  @Test
  public void testAbstractInterfaceFunctions() throws IOException {
    ASTNumber ast = parser.parse_StringDecimal( " 234 " ).get();
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
    ASTInteger ast = parser.parse_StringInteger( " -463 " ).get();
    assertEquals(-463, ast.getValue());
    assertEquals(-463, ast.getValueInt());
    assertEquals("-463", ast.getSource());
  }

  // --------------------------------------------------------------------
  @Test
  public void testIntTokens2() throws IOException {
    ASTIntegerList ast = parser.parse_StringIntegerList(
        "[9, -45, -0, - 47]" ).get();
    assertEquals(4, ast.sizeIntegers());
    assertEquals(9, ast.getIntegers(0).getValue());
    assertEquals("9", ast.getIntegers(0).getSource());
    assertEquals(-45, ast.getIntegers(1).getValue());
    assertEquals("-45", ast.getIntegers(1).getSource());
    assertEquals(0, ast.getIntegers(2).getValue());
    // "-" is still present
    assertEquals("-0", ast.getIntegers(2).getSource());
    assertEquals(-47, ast.getIntegers(3).getValue());
    // space between the two token is missing
    assertEquals("-47", ast.getIntegers(3).getSource());
  }

  // --------------------------------------------------------------------
  @Test
  public void testIntNEG() throws IOException {
    Optional<ASTInteger> os = parser.parse_StringInteger( " 0x34 " );
    assertEquals(false, os.isPresent());
  }

  // --------------------------------------------------------------------
  // test of the Test-Literal B
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testB() throws IOException {
    ASTBTest ast = parser.parse_StringBTest( " X2X, XFF001DX" ).get();
    assertEquals("X2X", ast.getXHexDigits(0));
    assertEquals("XFF001DX", ast.getXHexDigits(1));
  }

  
  // --------------------------------------------------------------------
  // String
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testString() throws IOException {
    ASTStringList ast = parser.parse_StringStringList(
     "[\"ZWeR\",\"4\", \"',\\b,\\\\;\", \"S\\u34F4W\", \"o\"]" ).get();
    assertEquals("ZWeR", ast.getStringLiterals(0).getValue());
    assertEquals("4", ast.getStringLiterals(1).getValue());
    assertEquals("',\b,\\;", ast.getStringLiterals(2).getValue());
    assertEquals("S\u34F4W", ast.getStringLiterals(3).getValue());
    assertEquals("o", ast.getStringLiterals(4).getValue());

    // repeat wg. buffering
    assertEquals("ZWeR", ast.getStringLiterals(0).getValue());
  }

  // --------------------------------------------------------------------
  // Char
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testChar() throws IOException {
    ASTCharLiteral ast = parser.parse_StringCharLiteral( " 'h'" ).get();
    assertEquals("h", ast.getSource());
    assertEquals('h', ast.getValue());
  }

  // --------------------------------------------------------------------
  @Test
  public void testChar2() throws IOException {
    ASTCharList ast = parser.parse_StringCharList(
     "['Z','4','\\'', '\\b', '\\\\', '\7', '\\7', 'o']" ).get();
    assertEquals('Z', ast.getCharLiterals(0).getValue());
    assertEquals('4', ast.getCharLiterals(1).getValue());
    assertEquals('\'', ast.getCharLiterals(2).getValue());
    assertEquals('\b', ast.getCharLiterals(3).getValue());
    assertEquals('\\', ast.getCharLiterals(4).getValue());
    // Encoded by Java
    assertEquals('\7', ast.getCharLiterals(5).getValue());
    assertEquals('o', ast.getCharLiterals(7).getValue());
  }

  // --------------------------------------------------------------------
  // --------------------------------------------------------------------
  @Test
  public void testCharUnicode() throws IOException {
    ASTCharList ast = parser.parse_StringCharList(
     "['\\u2345', '\\u23EF', '\\u0001', '\\uAFFA']" ).get();
    assertEquals('\u2345', ast.getCharLiterals(0).getValue());
    assertEquals('\u23EF', ast.getCharLiterals(1).getValue());
    assertEquals('\u0001', ast.getCharLiterals(2).getValue());
    assertEquals('\uAFFA', ast.getCharLiterals(3).getValue());
  }

}

