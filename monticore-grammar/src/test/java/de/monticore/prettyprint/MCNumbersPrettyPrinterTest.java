/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.mcnumbers._ast.ASTDecimal;
import de.monticore.mcnumbers._ast.ASTInteger;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCNumbersPrettyPrinter;
import de.monticore.testmcnumbers._parser.TestMCNumbersParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class MCNumbersPrettyPrinterTest {
  
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
  
  @Test
  public void testDecimalZero() throws IOException {
    TestMCNumbersParser parser = new TestMCNumbersParser();
    Optional<ASTDecimal> result = parser.parseDecimal(new StringReader("0"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDecimal decimal = result.get();
    
    MCNumbersPrettyPrinter prettyPrinter = new MCNumbersPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(decimal);
    
    result = parser.parseDecimal(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(decimal.deepEquals(result.get()));
  }
  
  @Test
  public void testDecimal() throws IOException {
    TestMCNumbersParser parser = new TestMCNumbersParser();
    Optional<ASTDecimal> result = parser.parseDecimal(new StringReader("9702"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDecimal decimal = result.get();
    
    MCNumbersPrettyPrinter prettyPrinter = new MCNumbersPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(decimal);
    
    result = parser.parseDecimal(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(decimal.deepEquals(result.get()));
  }
  
  @Test
  public void testIntegerPositive() throws IOException {
    TestMCNumbersParser parser = new TestMCNumbersParser();
    Optional<ASTInteger> result = parser.parseInteger(new StringReader("780530"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInteger integer = result.get();
    
    MCNumbersPrettyPrinter prettyPrinter = new MCNumbersPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(integer);
    
    result = parser.parseInteger(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(integer.deepEquals(result.get()));
  }
  
  @Test
  public void testIntegerNegative() throws IOException {
    TestMCNumbersParser parser = new TestMCNumbersParser();
    Optional<ASTInteger> result = parser.parseInteger(new StringReader("-9702"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInteger integer = result.get();
    
    MCNumbersPrettyPrinter prettyPrinter = new MCNumbersPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(integer);
    
    result = parser.parseInteger(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(integer.deepEquals(result.get()));
  }
}
