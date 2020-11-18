/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.abc.realabc._parser.RealABCParser;

public class EmbedTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    
    RealABCParser p = parse("a b c");
    
    assertEquals(false, p.hasErrors());
  }
  
  @Test
  public void testb() throws IOException {
    
    RealABCParser p = parse("a b");
    
    assertEquals(false, p.hasErrors());
  }
  
  @Test
  public void testc() throws IOException {
    
    RealABCParser p = parse("a a a b b b c c c");
    
    assertEquals(false, p.hasErrors());
  }
  
  @Test
  public void testd() throws IOException {
    
    RealABCParser p = parse("a b c c");
    
    assertEquals(false, p.hasErrors());
  }
  
  private RealABCParser parse(String in) throws IOException {
    RealABCParser parser = new  RealABCParser();
    parser.parseS(new StringReader(in));

    return parser;
  }
  
}
