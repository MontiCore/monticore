/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abc;

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
import mc.feature.abc.realabc._parser.RealABCParser;

public class EmbedTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    
    RealABCParser p = parse("a b c");
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testb() throws IOException {
    
    RealABCParser p = parse("a b");
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testc() throws IOException {
    
    RealABCParser p = parse("a a a b b b c c c");
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testd() throws IOException {
    
    RealABCParser p = parse("a b c c");
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  private RealABCParser parse(String in) throws IOException {
    RealABCParser parser = new  RealABCParser();
    parser.parseS(new StringReader(in));

    return parser;
  }
  
}
