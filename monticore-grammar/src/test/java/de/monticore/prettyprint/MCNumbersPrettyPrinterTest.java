/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.testmcnumbers.TestMCNumbersMill;
import de.monticore.testmcnumbers._parser.TestMCNumbersParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mcnumbers._ast.ASTDecimal;
import mcnumbers._ast.ASTInteger;
import mcnumbers._prettyprint.MCNumbersFullPrettyPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MCNumbersPrettyPrinterTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCNumbersMill.reset();
    TestMCNumbersMill.init();
  }
  
  @Test
  public void testDecimalZero() throws IOException {
    TestMCNumbersParser parser = new TestMCNumbersParser();
    Optional<ASTDecimal> result = parser.parseDecimal(new StringReader("0"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDecimal decimal = result.get();
    
    MCNumbersFullPrettyPrinter prettyPrinter = new MCNumbersFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(decimal);
    
    result = parser.parseDecimal(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(decimal.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testDecimal() throws IOException {
    TestMCNumbersParser parser = new TestMCNumbersParser();
    Optional<ASTDecimal> result = parser.parseDecimal(new StringReader("9702"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDecimal decimal = result.get();
    
    MCNumbersFullPrettyPrinter prettyPrinter = new MCNumbersFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(decimal);
    
    result = parser.parseDecimal(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(decimal.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testIntegerPositive() throws IOException {
    TestMCNumbersParser parser = new TestMCNumbersParser();
    Optional<ASTInteger> result = parser.parseInteger(new StringReader("780530"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInteger integer = result.get();
    
    MCNumbersFullPrettyPrinter prettyPrinter = new MCNumbersFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(integer);
    
    result = parser.parseInteger(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(integer.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testIntegerNegative() throws IOException {
    TestMCNumbersParser parser = new TestMCNumbersParser();
    Optional<ASTInteger> result = parser.parseInteger(new StringReader("-9702"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInteger integer = result.get();
    
    MCNumbersFullPrettyPrinter prettyPrinter = new MCNumbersFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(integer);
    
    result = parser.parseInteger(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(integer.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
