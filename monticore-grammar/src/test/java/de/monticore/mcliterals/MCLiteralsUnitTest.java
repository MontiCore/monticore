/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.testmcliteralsv2.TestMCLiteralsV2Mill;
import de.monticore.testmcliteralsv2._ast.*;
import de.monticore.testmcliteralsv2._parser.TestMCLiteralsV2Parser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mcnumbers._ast.ASTDecimal;
import mcnumbers._ast.ASTInteger;
import mcnumbers._ast.ASTNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stringliterals._ast.ASTCharLiteral;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// import de.monticore.mcliteralsv2._ast.*;

public class MCLiteralsUnitTest {
  
  // setup the language infrastructure
  TestMCLiteralsV2Parser parser = new TestMCLiteralsV2Parser() ;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCLiteralsV2Mill.reset();
    TestMCLiteralsV2Mill.init();
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat for Cardinality
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testCardinalityToken() throws IOException {
    ASTAnyTokenList ast = parser.parse_StringAnyTokenList( ":[65..67]:" ).get();
    Assertions.assertEquals(5, ast.sizeAnyTokens());
    ASTAnyToken t = ast.getAnyToken(0);
    t = ast.getAnyToken(1);
    Assertions.assertTrue(t.isPresentDecimalToken());
    Assertions.assertEquals("65", t.getDecimalToken());
    t = ast.getAnyToken(2);
    t = ast.getAnyToken(3);
    Assertions.assertTrue(t.isPresentDecimalToken());
    Assertions.assertEquals("67", t.getDecimalToken());
    t = ast.getAnyToken(4);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testNat1() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 9" ).get();
    Assertions.assertEquals("9", ast.getSource());
    Assertions.assertEquals(9, ast.getValue());
    Assertions.assertEquals(9, ast.getValueInt());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testNat2() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 0" ).get();
    Assertions.assertEquals("0", ast.getSource());
    Assertions.assertEquals(0, ast.getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testNat3() throws IOException {
    Optional<ASTDecimal> os = parser.parse_StringDecimal( " 00 0 " );
    Assertions.assertEquals(false, os.isPresent());
  }
  @Test
  public void testNat4() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 23 " ).get();
    Assertions.assertEquals("23", ast.getSource());
    Assertions.assertEquals(23, ast.getValue());
    Assertions.assertEquals(23, ast.getValueInt());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testNat5() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 463 " ).get();
    Assertions.assertEquals(463, ast.getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  @Test
  public void testNat6() throws IOException {
    Optional<ASTDecimal> os = parser.parse_StringDecimal( " 0x23 " );
    Assertions.assertEquals(false, os.isPresent());
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens() throws IOException {
    ASTAnyTokenList ast = parser.parse_StringAnyTokenList( ":463 23:" ).get();
    Assertions.assertEquals(2, ast.sizeAnyTokens());
    ASTAnyToken a0 = ast.getAnyToken(0);
    Assertions.assertTrue(a0.isPresentDecimalToken());
    Assertions.assertEquals("463", a0.getDecimalToken());
    ASTAnyToken a1 = ast.getAnyToken(1);
    Assertions.assertTrue(a1.isPresentDecimalToken());
    Assertions.assertEquals("23", a1.getDecimalToken());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens2() throws IOException {
    ASTAnyTokenList ast = parser.parse_StringAnyTokenList(
      ":9 'a' 45 00 47:" ).get();
    Assertions.assertEquals(6, ast.sizeAnyTokens());
    Assertions.assertEquals("9", ast.getAnyToken(0).getDecimalToken());
    Assertions.assertEquals("a", ast.getAnyToken(1).getCharToken());
    Assertions.assertEquals("45", ast.getAnyToken(2).getDecimalToken());
    // Observe the separated '0's!
    Assertions.assertEquals("0", ast.getAnyToken(3).getDecimalToken());
    Assertions.assertEquals("0", ast.getAnyToken(4).getDecimalToken());
    Assertions.assertEquals("47", ast.getAnyToken(5).getDecimalToken());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  @Test
  public void testAbstractInterfaceFunctions() throws IOException {
    ASTNumber ast = parser.parse_StringDecimal( " 234 " ).get();
    Assertions.assertEquals(234, ast.getValue());
    Assertions.assertEquals(234, ast.getValueInt());
    Assertions.assertEquals("234", ast.getSource());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  // Numbers: Integer
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testInt() throws IOException {
    ASTInteger ast = parser.parse_StringInteger( " -463 " ).get();
    Assertions.assertEquals(-463, ast.getValue());
    Assertions.assertEquals(-463, ast.getValueInt());
    Assertions.assertEquals("-463", ast.getSource());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  @Test
  public void testIntTokens2() throws IOException {
    ASTIntegerList ast = parser.parse_StringIntegerList(
        "[9, -45, -0, - 47]" ).get();
    Assertions.assertEquals(4, ast.sizeIntegers());
    Assertions.assertEquals(9, ast.getInteger(0).getValue());
    Assertions.assertEquals("9", ast.getInteger(0).getSource());
    Assertions.assertEquals(-45, ast.getInteger(1).getValue());
    Assertions.assertEquals("-45", ast.getInteger(1).getSource());
    Assertions.assertEquals(0, ast.getInteger(2).getValue());
    // "-" is still present
    Assertions.assertEquals("-0", ast.getInteger(2).getSource());
    Assertions.assertEquals(-47, ast.getInteger(3).getValue());
    // space between the two token is missing
    Assertions.assertEquals("-47", ast.getInteger(3).getSource());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  @Test
  public void testIntNEG() throws IOException {
    Optional<ASTInteger> os = parser.parse_StringInteger( " 0x34 " );
    Assertions.assertEquals(false, os.isPresent());
  }

  // --------------------------------------------------------------------
  // test of the Test-Literal B
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testB() throws IOException {
    ASTBTest ast = parser.parse_StringBTest( " X2X, XFF001DX" ).get();
    Assertions.assertEquals("X2X", ast.getXHexDigit(0));
    Assertions.assertEquals("XFF001DX", ast.getXHexDigit(1));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  
  // --------------------------------------------------------------------
  // String
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testString() throws IOException {
    ASTStringList ast = parser.parse_StringStringList(
     "[\"ZWeR\",\"4\", \"',\\b,\\\\;\", \"S\\u34F4W\", \"o\"]" ).get();
    Assertions.assertEquals("ZWeR", ast.getStringLiteral(0).getValue());
    Assertions.assertEquals("4", ast.getStringLiteral(1).getValue());
    Assertions.assertEquals("',\b,\\;", ast.getStringLiteral(2).getValue());
    Assertions.assertEquals("S\u34F4W", ast.getStringLiteral(3).getValue());
    Assertions.assertEquals("o", ast.getStringLiteral(4).getValue());

    // repeat wg. buffering
    Assertions.assertEquals("ZWeR", ast.getStringLiteral(0).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  // Char
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testChar() throws IOException {
    ASTCharLiteral ast = parser.parse_StringCharLiteral( " 'h'" ).get();
    Assertions.assertEquals("h", ast.getSource());
    Assertions.assertEquals('h', ast.getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  @Test
  public void testChar2() throws IOException {
    ASTCharList ast = parser.parse_StringCharList(
     "['Z','4','\\'', '\\b', '\\\\', '\7', '\\7', 'o']" ).get();
    Assertions.assertEquals('Z', ast.getCharLiteral(0).getValue());
    Assertions.assertEquals('4', ast.getCharLiteral(1).getValue());
    Assertions.assertEquals('\'', ast.getCharLiteral(2).getValue());
    Assertions.assertEquals('\b', ast.getCharLiteral(3).getValue());
    Assertions.assertEquals('\\', ast.getCharLiteral(4).getValue());
    // Encoded by Java
    Assertions.assertEquals('\7', ast.getCharLiteral(5).getValue());
    Assertions.assertEquals('o', ast.getCharLiteral(7).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  // --------------------------------------------------------------------
  @Test
  public void testCharUnicode() throws IOException {
    ASTCharList ast = parser.parse_StringCharList(
     "['\\u2345', '\\u23EF', '\\u0001', '\\uAFFA']" ).get();
    Assertions.assertEquals('\u2345', ast.getCharLiteral(0).getValue());
    Assertions.assertEquals('\u23EF', ast.getCharLiteral(1).getValue());
    Assertions.assertEquals('\u0001', ast.getCharLiteral(2).getValue());
    Assertions.assertEquals('\uAFFA', ast.getCharLiteral(3).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}

