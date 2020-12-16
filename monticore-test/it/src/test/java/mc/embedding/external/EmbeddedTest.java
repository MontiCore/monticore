/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external;

import mc.GeneratorIntegrationsTest;
import mc.embedding.external.embedded.EmbeddedMill;
import mc.embedding.external.embedded._symboltable.IEmbeddedGlobalScope;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmbeddedTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {

    final IEmbeddedGlobalScope scope = EmbeddedMill.globalScope();
    scope.getModelPath().addEntry(Paths.get("src/test/resources/mc/embedding"));

    final TextSymbol textSymbol = scope.resolveText("E").orElse(null);
    assertNotNull(textSymbol);
    assertEquals("E", textSymbol.getName());
  }

}
