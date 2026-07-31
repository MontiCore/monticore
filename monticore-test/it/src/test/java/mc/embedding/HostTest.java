/* (c) https://github.com/MontiCore/monticore */

package mc.embedding;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.embedding.host.HostMill;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.host._symboltable.IHostGlobalScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class HostTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void setUp() {
    HostMill.reset();
    HostMill.init();
  }

  @Test
  public void test() {
    final IHostGlobalScope scope = HostMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding"));
    HostSymbol hostSymbol = scope.resolveHost("H").orElse(null);
    Assertions.assertNotNull(hostSymbol);
    Assertions.assertEquals("H", hostSymbol.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
