/* (c) https://github.com/MontiCore/monticore */

package mc.embedding;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.embedding.embedded.EmbeddedMill;
import mc.embedding.embedded._symboltable.IEmbeddedGlobalScope;
import mc.embedding.embedded._symboltable.TextSymbol;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class EmbeddedTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws IOException {
    EmbeddedMill.reset();
    EmbeddedMill.init();
  }

  @Test
  public void test() {
    final IEmbeddedGlobalScope scope = EmbeddedMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding"));

    final TextSymbol textSymbol = scope.resolveText("E").orElse(null);
    assertNotNull(textSymbol);
    assertEquals("E", textSymbol.getName());
    assertTrue(Log.getFindings().isEmpty());
  }

}
