/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external;

import de.monticore.io.paths.ModelPath;
import mc.GeneratorIntegrationsTest;
import mc.embedding.external.composite._symboltable.*;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import mc.embedding.external.host._symboltable.HostSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CompositeTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final CompositeLanguage language = new CompositeLanguage();

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding"));

    final CompositeGlobalScope scope = new CompositeGlobalScope(modelPath, language);

    // Symbol of the host language
    final HostSymbol hostSymbol = scope.resolveHost("ZComposite").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("ZComposite", hostSymbol.getName());

    // Symbol of the embedded language
    assertTrue(hostSymbol.getSpannedScope() instanceof ICompositeScope);
    final TextSymbol textSymbol = ((ICompositeScope)hostSymbol.getSpannedScope()).resolveText("Hello").orElse(null);
    assertNotNull(textSymbol);

    // Adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = ((ICompositeScope) hostSymbol.getSpannedScope()).resolveContent("Hello").orElse(null);
    assertNotNull(text2ContentSymbol);
    assertTrue(text2ContentSymbol instanceof Text2ContentAdapter);



  }

}
