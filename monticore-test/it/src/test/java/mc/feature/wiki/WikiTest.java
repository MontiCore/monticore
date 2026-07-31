/* (c) https://github.com/MontiCore/monticore */

package mc.feature.wiki;

import java.io.IOException;
import java.io.StringReader;

import mc.GeneratorIntegrationsTest;
import mc.feature.wiki.wiki._parser.WikiParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WikiTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() throws IOException {
    
    WikiParser p = new WikiParser();
    
    p.parseWikiArtikel(new StringReader("==Test==\n==Test== ==\n== test ==\n"));
    assertFalse(p.hasErrors());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
