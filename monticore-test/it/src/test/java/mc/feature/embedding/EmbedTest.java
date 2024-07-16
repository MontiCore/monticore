/* (c) https://github.com/MontiCore/monticore */

package mc.feature.embedding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.embedding.outer.embedded._parser.EmbeddedParser;

public class EmbedTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  
  @Test
  public void test() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart(new StringReader("a a a"));
    
    Assertions.assertEquals(false, parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test2_a() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart(new StringReader("a x a"));
    
    Assertions.assertEquals(false, parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test2_b() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart2(new StringReader("a x a"));
    
    Assertions.assertEquals(false, parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test3() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart2(new StringReader("a a x a a"));
    
    Assertions.assertEquals(true, parser.hasErrors());
  }
  
  @Test
  public void test4() throws IOException {
    
    EmbeddedParser parser = new EmbeddedParser();
    parser.parseStart3(new StringReader("b x"));
    
    Assertions.assertEquals(false, parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
