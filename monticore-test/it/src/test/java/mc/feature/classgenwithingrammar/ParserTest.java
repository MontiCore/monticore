/* (c) https://github.com/MontiCore/monticore */

package mc.feature.classgenwithingrammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.classgenwithingrammar.type._parser.TypeParser;

public class ParserTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  // Test that one Welt is too much
  @Test
  public void test() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo Welt ");
    Assertions.assertTrue(hasError);
  }
  
  // Test that too many Hallo and Welt are detected in one go
  @Test
  public void test2() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo Hallo Welt ");
    Assertions.assertTrue(hasError);
  }
  
  // Tests that String is ok
  @Test
  public void test3() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo ");
    
    Assertions.assertFalse(hasError);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  // Test that one Welt is too much
  @Test
  public void testl() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall \"Wel\" ");
    
    Assertions.assertTrue(hasError);
  }
  
  // Test that too many Hallo and Welt are detected in one go
  @Test
  public void testl2() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall Hall \"Wel\" ");
    
    Assertions.assertTrue(hasError);
  }
  
  // Tests that String is ok
  @Test
  public void testl3() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall ");
    
    Assertions.assertFalse(hasError);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  private boolean parse( String input) throws IOException {
    StringReader s = new StringReader(input);    
    TypeParser parser = new TypeParser();
            
    parser.parseType(s);
    return parser.hasErrors();
  }
  
  private boolean parse2(String input) throws IOException {
    StringReader s = new StringReader(input);
    
    TypeParser parser = new TypeParser();
    
    parser.parseType2(s);
    return parser.hasErrors();

  }
}
