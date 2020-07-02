/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.monticore.io.paths.ModelPath;
import mc.GeneratorIntegrationsTest;
import mc.embedding.transitive.transhost._symboltable.TransHostGlobalScope;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransHostTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding/transitive"));

    final TransHostGlobalScope scope = new TransHostGlobalScope(modelPath, "transhost");

    TransStartSymbol hostSymbol = scope.resolveTransStart("TH").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("TH", hostSymbol.getName());
  }

}
