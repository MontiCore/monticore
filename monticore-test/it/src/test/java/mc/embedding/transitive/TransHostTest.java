/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.embedding.transitive.transhost.TransHostMill;
import mc.embedding.transitive.transhost._symboltable.ITransHostGlobalScope;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestWithMCLanguage(TransHostMill.class)
public class TransHostTest {

  @Test
  public void test() {
    final ITransHostGlobalScope scope = TransHostMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding/transitive"));

    TransStartSymbol hostSymbol = scope.resolveTransStart("TH").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("TH", hostSymbol.getName());
  }

}
