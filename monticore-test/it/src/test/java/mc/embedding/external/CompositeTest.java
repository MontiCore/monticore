/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.embedding.external.composite.CompositeMill;
import mc.embedding.external.composite._symboltable.ContentSymbol;
import mc.embedding.external.composite._symboltable.ICompositeGlobalScope;
import mc.embedding.external.composite._symboltable.ICompositeScope;
import mc.embedding.external.composite._symboltable.Text2ContentAdapter;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import mc.embedding.external.host._symboltable.HostSymbol;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(CompositeMill.class)
public class CompositeTest {

  @Test
  public void test() {
    final ICompositeGlobalScope scope = CompositeMill.globalScope();
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/embedding"));

    // Symbol of the host language
    final HostSymbol hostSymbol = scope.resolveHost("ZComposite").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("ZComposite", hostSymbol.getName());

    // Symbol of the embedded language
    assertInstanceOf(ICompositeScope.class, hostSymbol.getSpannedScope());
    final TextSymbol textSymbol = ((ICompositeScope)hostSymbol.getSpannedScope()).resolveText("Hello").orElse(null);
    assertNotNull(textSymbol);

    // Adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = ((ICompositeScope) hostSymbol.getSpannedScope()).resolveContent("Hello").orElse(null);
    assertNotNull(text2ContentSymbol);
    assertInstanceOf(Text2ContentAdapter.class, text2ContentSymbol);
  }

}
