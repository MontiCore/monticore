/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.testmchexnumbers.TestMCHexNumbersMill;
import de.monticore.testmchexnumbers._parser.TestMCHexNumbersParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mchexnumbers._ast.ASTHexInteger;
import mchexnumbers._ast.ASTHexadecimal;
import mchexnumbers._prettyprint.MCHexNumbersFullPrettyPrinter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCHexNumbersPrettyPrinterTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCHexNumbersMill.reset();
    TestMCHexNumbersMill.init();
  }
  
  @Test
  public void testHexadecimal() throws IOException {
    TestMCHexNumbersParser parser = new TestMCHexNumbersParser();
    Optional<ASTHexadecimal> result = parser.parseHexadecimal(new StringReader("0X6b90A"));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTHexadecimal hexadecimal = result.get();
    
    MCHexNumbersFullPrettyPrinter prettyPrinter = new MCHexNumbersFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(hexadecimal);
    
    result = parser.parseHexadecimal(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(hexadecimal.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHexIntegerPositiv() throws IOException {
    TestMCHexNumbersParser parser = new TestMCHexNumbersParser();
    Optional<ASTHexInteger> result = parser.parseHexInteger(new StringReader("0X6b90A"));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTHexInteger hexinteger = result.get();
    
    MCHexNumbersFullPrettyPrinter prettyPrinter = new MCHexNumbersFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(hexinteger);
    
    result = parser.parseHexInteger(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(hexinteger.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHexIntegerNegative() throws IOException {
    TestMCHexNumbersParser parser = new TestMCHexNumbersParser();
    Optional<ASTHexInteger> result = parser.parseHexInteger(new StringReader("-0xaf67"));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTHexInteger hexinteger = result.get();
    
    MCHexNumbersFullPrettyPrinter prettyPrinter = new MCHexNumbersFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(hexinteger);
    
    result = parser.parseHexInteger(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(hexinteger.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
