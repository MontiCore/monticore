/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testmcnumbers.TestMCNumbersMill;
import de.monticore.testmcnumbers._parser.TestMCNumbersParser;
import mcnumbers._ast.ASTDecimal;
import mcnumbers._ast.ASTInteger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCNumbersMill.class)
public class MCNumbersPrettyPrinterTest {

  @Test
  public void testDecimalZero() throws IOException {
    TestMCNumbersParser parser = TestMCNumbersMill.parser();
    Optional<ASTDecimal> result = parser.parse_StringDecimal("0");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDecimal decimal = result.get();
    
    String output = TestMCNumbersMill.prettyPrint(decimal, false);
    
    result = parser.parse_StringDecimal(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(decimal.deepEquals(result.get()));
  }
  
  @Test
  public void testDecimal() throws IOException {
    TestMCNumbersParser parser = TestMCNumbersMill.parser();
    Optional<ASTDecimal> result = parser.parse_StringDecimal("9702");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDecimal decimal = result.get();
    
    String output = TestMCNumbersMill.prettyPrint(decimal, false);
    
    result = parser.parse_StringDecimal(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(decimal.deepEquals(result.get()));
  }
  
  @Test
  public void testIntegerPositive() throws IOException {
    TestMCNumbersParser parser = TestMCNumbersMill.parser();
    Optional<ASTInteger> result = parser.parse_StringInteger("780530");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInteger integer = result.get();
    
    String output = TestMCNumbersMill.prettyPrint(integer, false);
    
    result = parser.parse_StringInteger(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(integer.deepEquals(result.get()));
  }
  
  @Test
  public void testIntegerNegative() throws IOException {
    TestMCNumbersParser parser = TestMCNumbersMill.parser();
    Optional<ASTInteger> result = parser.parse_StringInteger("-9702");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInteger integer = result.get();
    
    String output = TestMCNumbersMill.prettyPrint(integer, false);
    
    result = parser.parse_StringInteger(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(integer.deepEquals(result.get()));
  }
}
