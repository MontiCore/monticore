/* (c) https://github.com/MontiCore/monticore */

package mc.feature.classgenwithingrammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.classgenwithingrammar.type._parser.TypeParser;

public class ParserTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  // Test that one Welt is too much
  @Test
  public void test() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo Welt ");
    assertTrue(hasError);
  }
  
  // Test that too many Hallo and Welt are detected in one go
  @Test
  public void test2() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo Hallo Welt ");
    assertTrue(hasError);
  }
  
  // Tests that String is ok
  @Test
  public void test3() throws IOException {
        
    boolean hasError = parse("Hallo Hallo Hallo ");
    
    assertFalse(hasError);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  // Test that one Welt is too much
  @Test
  public void testl() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall \"Wel\" ");
    
    assertTrue(hasError);
  }
  
  // Test that too many Hallo and Welt are detected in one go
  @Test
  public void testl2() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall Hall \"Wel\" ");
    
    assertTrue(hasError);
  }
  
  // Tests that String is ok
  @Test
  public void testl3() throws IOException {
        
    boolean hasError = parse2("Hall Hall Hall ");
    
    assertFalse(hasError);
  
    assertTrue(Log.getFindings().isEmpty());
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
