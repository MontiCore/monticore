/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.embedding.external.embedded.EmbeddedMill;
import mc.embedding.external.embedded._symboltable.IEmbeddedGlobalScope;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestWithMCLanguage(EmbeddedMill.class)
public class EmbeddedTest {

  @Test
  public void test() {

    final IEmbeddedGlobalScope scope = EmbeddedMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding"));

    final TextSymbol textSymbol = scope.resolveText("E").orElse(null);
    assertNotNull(textSymbol);
    assertEquals("E", textSymbol.getName());
  }

}
