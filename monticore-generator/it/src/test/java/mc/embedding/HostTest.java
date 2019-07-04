/* (c) https://github.com/MontiCore/monticore */

package mc.embedding;

import de.monticore.io.paths.ModelPath;
import mc.GeneratorIntegrationsTest;
import mc.embedding.host._symboltable.HostGlobalScope;
import mc.embedding.host._symboltable.HostLanguage;
import mc.embedding.host._symboltable.HostSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HostTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final HostLanguage language = new HostLanguage();

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding"));

    final HostGlobalScope scope = new HostGlobalScope(modelPath, language);

    HostSymbol hostSymbol = scope.resolveHost("H").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("H", hostSymbol.getName());
  }

}
