/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
package de.monticore.mcliterals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.antlr4.MCConcreteParser.ParserExecution;
import de.monticore.mcnumbers._ast.ASTDecimal;
import de.monticore.mcnumbers._ast.ASTInteger;
import de.monticore.mcnumbers._ast.ASTNumber;
import de.monticore.stringliterals._ast.ASTCharLiteral;
// import de.monticore.mcliteralsv2._ast.*;
import de.monticore.testmcliteralsv2._ast.ASTAnyToken;
import de.monticore.testmcliteralsv2._ast.ASTAnyTokenList;
import de.monticore.testmcliteralsv2._ast.ASTBTest;
import de.monticore.testmcliteralsv2._ast.ASTCharList;
import de.monticore.testmcliteralsv2._ast.ASTIntegerList;
import de.monticore.testmcliteralsv2._ast.ASTStringList;
import de.monticore.testmcliteralsv2._parser.TestMCLiteralsV2Parser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;


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
    parser.setParserTarget(ParserExecution.EOF);
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat for Cardinality
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testCardinalityToken() throws IOException {
    ASTAnyTokenList ast = parser.parseString_AnyTokenList( ":[65..67]:" ).get();
    assertEquals(5, ast.getAnyTokens().size());
    ASTAnyToken t = ast.getAnyTokens().get(0);
    t = ast.getAnyTokens().get(1);
    assertTrue(t.getDecimalToken().isPresent());
    assertEquals("65", t.getDecimalToken().get());
    t = ast.getAnyTokens().get(2);
    t = ast.getAnyTokens().get(3);
    assertTrue(t.getDecimalToken().isPresent());
    assertEquals("67", t.getDecimalToken().get());
    t = ast.getAnyTokens().get(4);
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testNat1() throws IOException {
    ASTDecimal ast = parser.parseString_Decimal( " 9" ).get();
    assertEquals("9", ast.getSource());
    assertEquals(9, ast.getValue());
    assertEquals(9, ast.getValueInt());
  }
  @Test
  public void testNat2() throws IOException {
    ASTDecimal ast = parser.parseString_Decimal( " 0" ).get();
    assertEquals("0", ast.getSource());
    assertEquals(0, ast.getValue());
  }
  @Test
  public void testNat3() throws IOException {
    Optional<ASTDecimal> os = parser.parseString_Decimal( " 00 0 " );
    assertEquals(false, os.isPresent());
  }
  @Test
  public void testNat4() throws IOException {
    ASTDecimal ast = parser.parseString_Decimal( " 23 " ).get();
    assertEquals("23", ast.getSource());
    assertEquals(23, ast.getValue());
    assertEquals(23, ast.getValueInt());
  }
  @Test
  public void testNat5() throws IOException {
    ASTDecimal ast = parser.parseString_Decimal( " 463 " ).get();
    assertEquals(463, ast.getValue());
  }

  // --------------------------------------------------------------------
  @Test
  public void testNat6() throws IOException {
    Optional<ASTDecimal> os = parser.parseString_Decimal( " 0x23 " );
    assertEquals(false, os.isPresent());
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens() throws IOException {
    ASTAnyTokenList ast = parser.parseString_AnyTokenList( ":463 23:" ).get();
    assertEquals(2, ast.getAnyTokens().size());
    ASTAnyToken a0 = ast.getAnyTokens().get(0);
    assertTrue(a0.getDecimalToken().isPresent());
    assertEquals("463", a0.getDecimalToken().get());
    ASTAnyToken a1 = ast.getAnyTokens().get(1);
    assertTrue(a1.getDecimalToken().isPresent());
    assertEquals("23", a1.getDecimalToken().get());
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens2() throws IOException {
    ASTAnyTokenList ast = parser.parseString_AnyTokenList(
    	":9 'a' 45 00 47:" ).get();
    assertEquals(6, ast.getAnyTokens().size());
    assertEquals("9", ast.getAnyTokens().get(0).getDecimalToken().get());
    assertEquals("a", ast.getAnyTokens().get(1).getCharToken().get());
    assertEquals("45", ast.getAnyTokens().get(2).getDecimalToken().get());
    // Observe the separated '0's!
    assertEquals("0", ast.getAnyTokens().get(3).getDecimalToken().get());
    assertEquals("0", ast.getAnyTokens().get(4).getDecimalToken().get());
    assertEquals("47", ast.getAnyTokens().get(5).getDecimalToken().get());
  }

  // --------------------------------------------------------------------
  @Test
  public void testAbstractInterfaceFunctions() throws IOException {
    ASTNumber ast = parser.parseString_Decimal( " 234 " ).get();
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
    ASTInteger ast = parser.parseString_Integer( " -463 " ).get();
    assertEquals(-463, ast.getValue());
    assertEquals(-463, ast.getValueInt());
    assertEquals("-463", ast.getSource());
  }

  // --------------------------------------------------------------------
  @Test
  public void testIntTokens2() throws IOException {
    ASTIntegerList ast = parser.parseString_IntegerList(
        "[9, -45, -0, - 47]" ).get();
    assertEquals(4, ast.getIntegers().size());
    assertEquals(9, ast.getIntegers().get(0).getValue());
    assertEquals("9", ast.getIntegers().get(0).getSource());
    assertEquals(-45, ast.getIntegers().get(1).getValue());
    assertEquals("-45", ast.getIntegers().get(1).getSource());
    assertEquals(0, ast.getIntegers().get(2).getValue());
    // "-" is still present
    assertEquals("-0", ast.getIntegers().get(2).getSource());
    assertEquals(-47, ast.getIntegers().get(3).getValue());
    // space between the two token is missing 
    assertEquals("-47", ast.getIntegers().get(3).getSource());
  }

  // --------------------------------------------------------------------
  @Test
  public void testIntNEG() throws IOException {
    Optional<ASTInteger> os = parser.parseString_Integer( " 0x34 " );
    assertEquals(false, os.isPresent());
  }

  // --------------------------------------------------------------------
  // test of the Test-Literal B
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testB() throws IOException {
    ASTBTest ast = parser.parseString_BTest( " X2X, XFF001DX" ).get();
    assertEquals("X2X", ast.getXHexDigits().get(0));
    assertEquals("XFF001DX", ast.getXHexDigits().get(1));
  }

  
  // --------------------------------------------------------------------
  // String
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testString() throws IOException {
    ASTStringList ast = parser.parseString_StringList( 
     "[\"ZWeR\",\"4\", \"',\\b,\\\\;\", \"S\\u34F4W\", \"o\"]" ).get();
    assertEquals("ZWeR", ast.getStringLiterals().get(0).getValue());
    assertEquals("4", ast.getStringLiterals().get(1).getValue());
    assertEquals("',\b,\\;", ast.getStringLiterals().get(2).getValue());
    assertEquals("S\u34F4W", ast.getStringLiterals().get(3).getValue());
    assertEquals("o", ast.getStringLiterals().get(4).getValue());

    // repeat wg. buffering
    assertEquals("ZWeR", ast.getStringLiterals().get(0).getValue());
  }

  // --------------------------------------------------------------------
  // Char
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testChar() throws IOException {
    ASTCharLiteral ast = parser.parseString_CharLiteral( " 'h'" ).get();
    assertEquals("h", ast.getSource());
    assertEquals('h', ast.getValue());
  }

  // --------------------------------------------------------------------
  @Test
  public void testChar2() throws IOException {
    ASTCharList ast = parser.parseString_CharList( 
     "['Z','4','\\'', '\\b', '\\\\', '\7', '\\7', 'o']" ).get();
    assertEquals('Z', ast.getCharLiterals().get(0).getValue());
    assertEquals('4', ast.getCharLiterals().get(1).getValue());
    assertEquals('\'', ast.getCharLiterals().get(2).getValue());
    assertEquals('\b', ast.getCharLiterals().get(3).getValue());
    assertEquals('\\', ast.getCharLiterals().get(4).getValue());
    // Encoded by Java
    assertEquals('\7', ast.getCharLiterals().get(5).getValue());
    assertEquals('o', ast.getCharLiterals().get(7).getValue());
  }

  // --------------------------------------------------------------------
  @Test
  public void testCharOct1() throws IOException {
    ASTCharLiteral ast = parser.parseString_CharLiteral(
    	"'" + "\\" + "105" + "'" ).get();
    // XXX BUG, MB: Octals are not decoded
    assertEquals('\40', ast.getValue());  // this is wrong
    // should be: assertEquals('E', ast.getValue());
    //            assertEquals("\7", ast.getSource());
  }

  // --------------------------------------------------------------------
  @Test
  public void testCharOct2() throws IOException {
    ASTCharList ast = parser.parseString_CharList( 
     "['\\0', '\\7', '\\244', 'o']" ).get();
    // XXX BUG, MB : Octals
    // assertEquals('\0', ast.getCharLiterals(0).getValue());
    // assertEquals('\7', ast.getCharLiterals(1).getValue());
    // assertEquals('\244', ast.getCharLiterals(2).getValue());
    assertEquals('o', ast.getCharLiterals().get(3).getValue());
  }

  // --------------------------------------------------------------------
  // --------------------------------------------------------------------
  @Test
  public void testCharUnicode() throws IOException {
    ASTCharList ast = parser.parseString_CharList( 
     "['\\u2345', '\\u23EF', '\\u0001', '\\uAFFA']" ).get();
    assertEquals('\u2345', ast.getCharLiterals().get(0).getValue());
    assertEquals('\u23EF', ast.getCharLiterals().get(1).getValue());
    assertEquals('\u0001', ast.getCharLiterals().get(2).getValue());
    assertEquals('\uAFFA', ast.getCharLiterals().get(3).getValue());
  }

}

