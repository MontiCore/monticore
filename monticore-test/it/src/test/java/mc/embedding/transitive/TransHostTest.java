/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.embedding.transitive.transhost.TransHostMill;
import mc.embedding.transitive.transhost._symboltable.ITransHostGlobalScope;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class TransHostTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void setUp() throws IOException {
    TransHostMill.reset();
    TransHostMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void test() {
    final ITransHostGlobalScope scope = TransHostMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding/transitive"));

    TransStartSymbol hostSymbol = scope.resolveTransStart("TH").orElse(null);
    Assertions.assertNotNull(hostSymbol);
    Assertions.assertEquals("TH", hostSymbol.getName());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
