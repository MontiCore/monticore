/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.monticore.io.paths.ModelPath;
import mc.GeneratorIntegrationsTest;
import mc.embedding.composite._symboltable.Text2ContentAdapter;
import mc.embedding.embedded._symboltable.IEmbeddedScope;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._symboltable.ContentSymbol;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.host._symboltable.IHostScope;
import mc.embedding.transitive.transcomposite._symboltable.TransCompositeGlobalScope;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TransCompositeTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding/transitive/"));

    final TransCompositeGlobalScope scope = new TransCompositeGlobalScope(modelPath, "transhost");

    // Symbol of the host language
    final TransStartSymbol transHostSymbol = scope.resolveTransStart("TransComposite").orElse(null);
    assertNotNull(transHostSymbol);
    assertEquals("TransComposite", transHostSymbol.getName());

    // Symbol of the embedded language
    assertTrue(transHostSymbol.getSpannedScope() instanceof IHostScope);
    final HostSymbol hostSymbol = ((IHostScope)transHostSymbol.getSpannedScope()).resolveHost("TransHost").orElse(null);
    assertNotNull(hostSymbol);

    // Symbol of the transitive embedded language
    assertTrue(hostSymbol.getSpannedScope() instanceof IEmbeddedScope);
    final TextSymbol textSymbol = ((IEmbeddedScope)hostSymbol.getSpannedScope()).resolveText("Hello").orElse(null);
    assertNotNull(textSymbol);

    // transitive adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = hostSymbol.getSpannedScope().resolveContent("Hello").orElse(null);
    assertNotNull(text2ContentSymbol);
    assertTrue(text2ContentSymbol instanceof Text2ContentAdapter);

  }

}
