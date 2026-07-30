/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testmchexnumbers.TestMCHexNumbersMill;
import de.monticore.testmchexnumbers._parser.TestMCHexNumbersParser;
import mchexnumbers._ast.ASTHexInteger;
import mchexnumbers._ast.ASTHexadecimal;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCHexNumbersMill.class)
public class MCHexNumbersPrettyPrinterTest {

  @Test
  public void testHexadecimal() throws IOException {
    TestMCHexNumbersParser parser = TestMCHexNumbersMill.parser();
    Optional<ASTHexadecimal> result = parser.parse_StringHexadecimal("0X6b90A");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTHexadecimal hexadecimal = result.get();
    
    String output = TestMCHexNumbersMill.prettyPrint(hexadecimal, false);
    
    result = parser.parse_StringHexadecimal(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(hexadecimal.deepEquals(result.get()));
  }
  
  @Test
  public void testHexIntegerPositiv() throws IOException {
    TestMCHexNumbersParser parser = TestMCHexNumbersMill.parser();
    Optional<ASTHexInteger> result = parser.parse_StringHexInteger("0X6b90A");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTHexInteger hexinteger = result.get();
    
    String output = TestMCHexNumbersMill.prettyPrint(hexinteger, false);
    
    result = parser.parse_StringHexInteger(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(hexinteger.deepEquals(result.get()));
  }
  
  @Test
  public void testHexIntegerNegative() throws IOException {
    TestMCHexNumbersParser parser = TestMCHexNumbersMill.parser();
    Optional<ASTHexInteger> result = parser.parse_StringHexInteger("-0xaf67");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTHexInteger hexinteger = result.get();
    
    String output = TestMCHexNumbersMill.prettyPrint(hexinteger, false);
    
    result = parser.parse_StringHexInteger(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(hexinteger.deepEquals(result.get()));
  }
  
}
