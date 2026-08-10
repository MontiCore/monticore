/* (c) https://github.com/MontiCore/monticore */

package mc.feature.wiki;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.wiki.wiki.WikiMill;
import mc.feature.wiki.wiki._parser.WikiParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(WikiMill.class)
public class WikiTest {
  @Test
  public void test() throws IOException {
    
    WikiParser p = WikiMill.parser();
    
    p.parseWikiArtikel(new StringReader("==Test==\n==Test== ==\n== test ==\n"));
    assertFalse(p.hasErrors());
  }
  
}
