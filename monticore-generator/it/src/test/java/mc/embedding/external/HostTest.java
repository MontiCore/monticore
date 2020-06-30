/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external;

import de.monticore.io.paths.ModelPath;
import mc.GeneratorIntegrationsTest;
import mc.embedding.external.host.HostMill;
import mc.embedding.external.host._symboltable.HostGlobalScope;
import mc.embedding.external.host._symboltable.HostSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HostTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding"));

    final HostGlobalScope scope = HostMill
        .hostGlobalScopeBuilder()
        .setModelPath(modelPath)
        .setModelFileExtension("host")
        .build();

    HostSymbol hostSymbol = scope.resolveHost("H").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("H", hostSymbol.getName());
  }

}
