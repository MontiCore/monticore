/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.embedding.external.embedded.EmbeddedMill;
import mc.embedding.external.embedded._symboltable.IEmbeddedGlobalScope;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class EmbeddedTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void setUp() throws IOException {
    EmbeddedMill.reset();
    EmbeddedMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void test() {

    final IEmbeddedGlobalScope scope = EmbeddedMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding"));

    final TextSymbol textSymbol = scope.resolveText("E").orElse(null);
    Assertions.assertNotNull(textSymbol);
    Assertions.assertEquals("E", textSymbol.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
