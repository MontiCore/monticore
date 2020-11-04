/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import mc.GeneratorIntegrationsTest;
import mc.embedding.transitive.transhost.TransHostMill;
import mc.embedding.transitive.transhost._symboltable.ITransHostGlobalScope;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransHostTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final ITransHostGlobalScope scope = TransHostMill.transHostGlobalScope();
    scope.getModelPath().addEntry(Paths.get("src/test/resources/mc/embedding/transitive"));
    scope.setModelFileExtension("transhost");

    TransStartSymbol hostSymbol = scope.resolveTransStart("TH").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("TH", hostSymbol.getName());
  }

}
