/* (c) https://github.com/MontiCore/monticore */

package mc.embedding;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.embedding.external.embedded.EmbeddedMill;
import mc.embedding.host.HostMill;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.host._symboltable.IHostGlobalScope;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HostTest extends GeneratorIntegrationsTest {

  @Before
  public void setUp() throws IOException {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);

    HostMill.reset();
    HostMill.init();
  }

  @Test
  public void test() {
    final IHostGlobalScope scope = HostMill.globalScope();
    scope.getModelPath().addEntry(Paths.get("src/test/resources/mc/embedding"));
    HostSymbol hostSymbol = scope.resolveHost("H").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("H", hostSymbol.getName());
  }

}
