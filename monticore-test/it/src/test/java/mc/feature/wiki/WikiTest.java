/* (c) https://github.com/MontiCore/monticore */

package mc.feature.wiki;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.wiki.wiki._parser.WikiParser;

public class WikiTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    
    WikiParser p = new WikiParser();
    
    p.parseWikiArtikel(new StringReader("==Test==\n==Test== ==\n== test ==\n"));
    assertEquals(false, p.hasErrors());
    
  }
  
}
