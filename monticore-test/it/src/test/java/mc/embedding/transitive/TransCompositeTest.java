/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.embedding.composite._symboltable.Text2ContentAdapter;
import mc.embedding.embedded._symboltable.IEmbeddedScope;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._symboltable.ContentSymbol;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.host._symboltable.IHostScope;
import mc.embedding.transitive.transcomposite.TransCompositeMill;
import mc.embedding.transitive.transcomposite._symboltable.ITransCompositeGlobalScope;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TransCompositeMill.class)
public class TransCompositeTest {

  @Test
  public void test() {
    final ITransCompositeGlobalScope scope = TransCompositeMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding/transitive/"));

    // Symbol of the host language
    final TransStartSymbol transHostSymbol = scope.resolveTransStart("TransComposite").orElse(null);
    assertNotNull(transHostSymbol);
    assertEquals("TransComposite", transHostSymbol.getName());

    // Symbol of the embedded language
    assertInstanceOf(IHostScope.class, transHostSymbol.getSpannedScope());
    final HostSymbol hostSymbol = ((IHostScope)transHostSymbol.getSpannedScope()).resolveHost("TransHost").orElse(null);
    assertNotNull(hostSymbol);

    // Symbol of the transitive embedded language
    assertInstanceOf(IEmbeddedScope.class, hostSymbol.getSpannedScope());
    final TextSymbol textSymbol = ((IEmbeddedScope)hostSymbol.getSpannedScope()).resolveText("Hello").orElse(null);
    assertNotNull(textSymbol);

    // transitive adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = hostSymbol.getSpannedScope().resolveContent("Hello").orElse(null);
    assertNotNull(text2ContentSymbol);
    assertInstanceOf(Text2ContentAdapter.class, text2ContentSymbol);
  }

}
