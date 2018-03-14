/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.GeneratorIntegrationsTest;
import mc.embedding.composite._symboltable.Text2ContentAdapter;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._symboltable.ContentSymbol;
import mc.embedding.host._symboltable.HostSymbol;
import mc.embedding.transitive.transcomposite._symboltable.TransCompositeLanguage;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TransCompositeTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final TransCompositeLanguage language = new TransCompositeLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding/transitive/"));

    final GlobalScope scope = new GlobalScope(modelPath, language, resolvingConfiguration);

    // Symbol of the host language
    final TransStartSymbol transHostSymbol = scope.<TransStartSymbol>resolve("TransComposite", TransStartSymbol.KIND).orElse(null);
    assertNotNull(transHostSymbol);
    assertEquals("TransComposite", transHostSymbol.getName());

    // Symbol of the embedded language
    final HostSymbol hostSymbol = transHostSymbol.getSpannedScope().<HostSymbol>resolve("TransHost", HostSymbol.KIND).orElse(null);
    assertNotNull(hostSymbol);

    // Symbol of the transitive embedded language
    final TextSymbol textSymbol = hostSymbol.getSpannedScope().<TextSymbol>resolve("Hello", TextSymbol.KIND).orElse(null);
    assertNotNull(textSymbol);

    // transitive adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = hostSymbol.getSpannedScope().<ContentSymbol>resolve("Hello", ContentSymbol.KIND).orElse(null);
    assertNotNull(text2ContentSymbol);
    assertTrue(text2ContentSymbol instanceof Text2ContentAdapter);

  }

}
