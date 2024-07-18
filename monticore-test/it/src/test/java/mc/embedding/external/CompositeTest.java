/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.embedding.external.composite.CompositeMill;
import mc.embedding.external.composite._symboltable.ContentSymbol;
import mc.embedding.external.composite._symboltable.ICompositeGlobalScope;
import mc.embedding.external.composite._symboltable.ICompositeScope;
import mc.embedding.external.composite._symboltable.Text2ContentAdapter;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import mc.embedding.external.host._symboltable.HostSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class CompositeTest extends GeneratorIntegrationsTest {

  @BeforeEach
  public void setUp() throws IOException {
    CompositeMill.reset();
    CompositeMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void test() {
    final ICompositeGlobalScope scope = CompositeMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding"));

    // Symbol of the host language
    final HostSymbol hostSymbol = scope.resolveHost("ZComposite").orElse(null);
    Assertions.assertNotNull(hostSymbol);
    Assertions.assertEquals("ZComposite", hostSymbol.getName());

    // Symbol of the embedded language
    Assertions.assertTrue(hostSymbol.getSpannedScope() instanceof ICompositeScope);
    final TextSymbol textSymbol = ((ICompositeScope)hostSymbol.getSpannedScope()).resolveText("Hello").orElse(null);
    Assertions.assertNotNull(textSymbol);

    // Adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = ((ICompositeScope) hostSymbol.getSpannedScope()).resolveContent("Hello").orElse(null);
    Assertions.assertNotNull(text2ContentSymbol);
    Assertions.assertTrue(text2ContentSymbol instanceof Text2ContentAdapter);
  
  
    Assertions.assertTrue(Log.getFindings().isEmpty());

  }

}
