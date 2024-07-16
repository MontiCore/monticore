/* (c) https://github.com/MontiCore/monticore */

package mc.feature.wiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.wiki.wiki._parser.WikiParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class WikiTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    
    WikiParser p = new WikiParser();
    
    p.parseWikiArtikel(new StringReader("==Test==\n==Test== ==\n== test ==\n"));
    Assertions.assertEquals(false, p.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
