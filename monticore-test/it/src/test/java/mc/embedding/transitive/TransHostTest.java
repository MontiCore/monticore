/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.embedding.transitive.transhost.TransHostMill;
import mc.embedding.transitive.transhost._symboltable.ITransHostGlobalScope;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TransHostTest extends GeneratorIntegrationsTest {
  
  @Before
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
    assertNotNull(hostSymbol);
    assertEquals("TH", hostSymbol.getName());
    assertTrue(Log.getFindings().isEmpty());
  }

}
