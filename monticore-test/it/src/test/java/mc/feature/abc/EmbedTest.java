/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.abc.realabc._parser.RealABCParser;

public class EmbedTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    
    RealABCParser p = parse("a b c");
    
    assertEquals(false, p.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testb() throws IOException {
    
    RealABCParser p = parse("a b");
    
    assertEquals(false, p.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testc() throws IOException {
    
    RealABCParser p = parse("a a a b b b c c c");
    
    assertEquals(false, p.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testd() throws IOException {
    
    RealABCParser p = parse("a b c c");
    
    assertEquals(false, p.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  private RealABCParser parse(String in) throws IOException {
    RealABCParser parser = new  RealABCParser();
    parser.parseS(new StringReader(in));

    return parser;
  }
  
}
