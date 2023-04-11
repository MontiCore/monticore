/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.testmcnumbers._parser.TestMCNumbersParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mcnumbers._ast.ASTDecimal;
import mcnumbers._ast.ASTInteger;
import mcnumbers._prettyprint.MCNumbersFullPrettyPrinter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCNumbersPrettyPrinterTest {
  
  @Before
  public void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
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
