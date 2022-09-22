/* (c) https://github.com/MontiCore/monticore */

package mc.feature.wiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.wiki.wiki._parser.WikiParser;
import de.se_rwth.commons.logging.Log;

public class WikiTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    
    WikiParser p = new WikiParser();
    
    p.parseWikiArtikel(new StringReader("==Test==\n==Test== ==\n== test ==\n"));
    assertEquals(false, p.hasErrors());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
