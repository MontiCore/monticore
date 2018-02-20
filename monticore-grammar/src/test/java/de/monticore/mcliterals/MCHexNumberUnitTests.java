/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mcliterals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.mchexnumbers._ast.ASTHexInteger;
import de.monticore.mchexnumbers._ast.ASTHexadecimal;
import de.monticore.mcnumbers._ast.ASTDecimal;
import de.monticore.mcnumbers._ast.ASTInteger;
import de.monticore.mcnumbers._ast.ASTNumber;
import de.monticore.testmcliteralsv3._ast.ASTAnyToken;
import de.monticore.testmcliteralsv3._ast.ASTAnyTokenList;
import de.monticore.testmcliteralsv3._ast.ASTIntegerList;
import de.monticore.testmcliteralsv3._ast.ASTNumberList;
import de.monticore.testmcliteralsv3._parser.TestMCLiteralsV3Parser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;


public class MCHexNumberUnitTests {
    
  // setup the language infrastructure
  TestMCLiteralsV3Parser parser = new TestMCLiteralsV3Parser() ;
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
  }
  

  @Before
  public void setUp() { 
    Log.getFindings().clear();
  }
  
  // --------------------------------------------------------------------
  // Numbers: Hexadecimal Encodings
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testHex1() throws IOException {
    Optional<ASTNumber> os = parser.parse_StringNumber( " 0x34 " );
    assertEquals(true, os.isPresent());
    assertEquals(0x34, os.get().getValue());
    assertEquals("0x34", os.get().getSource());
  }

  // --------------------------------------------------------------------
  @Test
  public void testNumbers() throws IOException {
    ASTNumberList ast = parser.parse_StringNumberList(
    	"[463 0x23 - 0x0045  -0X3AFFA-27 0x3affa-0xFEDBCAabcdef]" ).get();
    assertEquals(7, ast.getNumberList().size());

    ASTNumber n = ast.getNumberList().get(0);
    assertEquals(ASTDecimal.class, n.getClass());
    assertEquals(463, n.getValue());

    n = ast.getNumberList().get(1);
    assertEquals(ASTHexadecimal.class, n.getClass());
    assertEquals(0x23, n.getValue());

    n = ast.getNumberList().get(2);
    assertEquals(ASTHexInteger.class, n.getClass());
    assertEquals(-0x45, n.getValue());
    assertEquals("-0x0045", n.getSource());

    n = ast.getNumberList().get(3);
    assertEquals(ASTHexInteger.class, n.getClass());
    assertEquals(-0x3AFFA, n.getValue());

    n = ast.getNumberList().get(4);
    assertEquals(ASTInteger.class, n.getClass());
    assertEquals(-27, n.getValue());

    n = ast.getNumberList().get(5);
    assertEquals(ASTHexadecimal.class, n.getClass());
    assertEquals(0x3affa, n.getValue());

    n = ast.getNumberList().get(6);
    assertEquals(ASTHexInteger.class, n.getClass());
    assertEquals(-0xFEDBCAabcdefl, n.getValue());

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
    ASTAnyTokenList ast = parser.parse_StringAnyTokenList( "[463 23]" ).get();
    assertEquals(2, ast.getAnyTokenList().size());
    ASTAnyToken a0 = ast.getAnyTokenList().get(0);
    assertTrue(a0.isPresentDecimalToken());
    assertEquals("463", a0.getDecimalToken());
    ASTAnyToken a1 = ast.getAnyTokenList().get(1);
    assertTrue(a1.isPresentDecimalToken());
    assertEquals("23", a1.getDecimalToken());
  }

  // --------------------------------------------------------------------
  @Test
  public void testTokens2() throws IOException {
    ASTAnyTokenList ast = parser.parse_StringAnyTokenList(
    	"[9 'a' 45 00 47]" ).get();
    assertEquals(6, ast.getAnyTokenList().size());
    assertEquals("9", ast.getAnyTokenList().get(0).getDecimalToken());
    assertEquals("a", ast.getAnyTokenList().get(1).getCharToken());
    assertEquals("45", ast.getAnyTokenList().get(2).getDecimalToken());
    // Observe the separated '0's!
    assertEquals("0", ast.getAnyTokenList().get(3).getDecimalToken());
    assertEquals("0", ast.getAnyTokenList().get(4).getDecimalToken());
    assertEquals("47", ast.getAnyTokenList().get(5).getDecimalToken());
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
    assertEquals(4, ast.getIntegerList().size());
    assertEquals(9, ast.getIntegerList().get(0).getValue());
    assertEquals("9", ast.getIntegerList().get(0).getSource());
    assertEquals(-45, ast.getIntegerList().get(1).getValue());
    assertEquals("-45", ast.getIntegerList().get(1).getSource());
    assertEquals(0, ast.getIntegerList().get(2).getValue());
    // "-" is still present
    assertEquals("-0", ast.getIntegerList().get(2).getSource());
    assertEquals(-47, ast.getIntegerList().get(3).getValue());
    // space between the two token is missing 
    assertEquals("-47", ast.getIntegerList().get(3).getSource());
  }

}

