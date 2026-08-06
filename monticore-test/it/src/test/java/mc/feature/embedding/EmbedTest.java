/* (c) https://github.com/MontiCore/monticore */

package mc.feature.embedding;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.embedding.outer.embedded.EmbeddedMill;
import mc.feature.embedding.outer.embedded._parser.EmbeddedParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(EmbeddedMill.class)
public class EmbedTest {

  @Test
  public void test() throws IOException {
    
    EmbeddedParser parser = EmbeddedMill.parser();
    parser.parse_StringStart("a a a");
    
    assertFalse(parser.hasErrors());
  }
  
  @Test
  public void test2_a() throws IOException {
    
    EmbeddedParser parser = EmbeddedMill.parser();
    parser.parse_StringStart("a x a");
    
    assertFalse(parser.hasErrors());
  }
  
  @Test
  public void test2_b() throws IOException {
    
    EmbeddedParser parser = EmbeddedMill.parser();
    parser.parse_StringStart2("a x a");
    
    assertFalse(parser.hasErrors());
  }
  
  @Test
  public void test3() throws IOException {
    
    EmbeddedParser parser = EmbeddedMill.parser();
    parser.parse_StringStart2("a a x a a");
    
    assertTrue(parser.hasErrors());
    MCAssertions.assertHasFindingStartingWith("extraneous input 'a' expecting {'x', 'y'}");
  }
  
  @Test
  public void test4() throws IOException {
    
    EmbeddedParser parser = EmbeddedMill.parser();
    parser.parse_StringStart3("b x");
    
    assertFalse(parser.hasErrors());
  }
  
}
