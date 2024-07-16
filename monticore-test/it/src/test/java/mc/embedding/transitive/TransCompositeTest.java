/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.embedding.composite._symboltable.Text2ContentAdapter;
import mc.embedding.embedded._symboltable.IEmbeddedScope;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._symboltable.ContentSymbol;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.host._symboltable.IHostScope;
import mc.embedding.transitive.transcomposite.TransCompositeMill;
import mc.embedding.transitive.transcomposite._symboltable.ITransCompositeGlobalScope;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class TransCompositeTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void setUp() throws IOException {
    TransCompositeMill.reset();
    TransCompositeMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void test() {
    final ITransCompositeGlobalScope scope = TransCompositeMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding/transitive/"));

    // Symbol of the host language
    final TransStartSymbol transHostSymbol = scope.resolveTransStart("TransComposite").orElse(null);
    Assertions.assertNotNull(transHostSymbol);
    Assertions.assertEquals("TransComposite", transHostSymbol.getName());

    // Symbol of the embedded language
    Assertions.assertTrue(transHostSymbol.getSpannedScope() instanceof IHostScope);
    final HostSymbol hostSymbol = ((IHostScope)transHostSymbol.getSpannedScope()).resolveHost("TransHost").orElse(null);
    Assertions.assertNotNull(hostSymbol);

    // Symbol of the transitive embedded language
    Assertions.assertTrue(hostSymbol.getSpannedScope() instanceof IEmbeddedScope);
    final TextSymbol textSymbol = ((IEmbeddedScope)hostSymbol.getSpannedScope()).resolveText("Hello").orElse(null);
    Assertions.assertNotNull(textSymbol);

    // transitive adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = hostSymbol.getSpannedScope().resolveContent("Hello").orElse(null);
    Assertions.assertNotNull(text2ContentSymbol);
    Assertions.assertTrue(text2ContentSymbol instanceof Text2ContentAdapter);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
