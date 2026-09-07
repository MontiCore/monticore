/* (c) https://github.com/MontiCore/monticore */

package mc.embedding;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.embedding.host.HostMill;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.host._symboltable.IHostGlobalScope;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestWithMCLanguage(HostMill.class)
public class HostTest {

  @Test
  public void test() {
    final IHostGlobalScope scope = HostMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding"));
    HostSymbol hostSymbol = scope.resolveHost("H").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("H", hostSymbol.getName());
  }

}
