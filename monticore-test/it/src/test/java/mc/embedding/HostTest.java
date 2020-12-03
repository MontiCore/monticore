/* (c) https://github.com/MontiCore/monticore */

package mc.embedding;

import mc.GeneratorIntegrationsTest;
import mc.embedding.host.HostMill;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.host._symboltable.IHostGlobalScope;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HostTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final IHostGlobalScope scope = HostMill.globalScope();
    scope.getModelPath().addEntry(Paths.get("src/test/resources/mc/embedding"));
    scope.setFileExt("host");

    HostSymbol hostSymbol = scope.resolveHost("H").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("H", hostSymbol.getName());
  }

}
