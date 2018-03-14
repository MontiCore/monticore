/* (c) https://github.com/MontiCore/monticore */

package mc.embedding;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.GeneratorIntegrationsTest;
import mc.embedding.embedded._symboltable.EmbeddedLanguage;
import mc.embedding.embedded._symboltable.TextSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmbeddedTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final EmbeddedLanguage language = new EmbeddedLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding"));

    final GlobalScope scope = new GlobalScope(modelPath, language, resolvingConfiguration);

    final TextSymbol textSymbol = scope.<TextSymbol>resolve("E", TextSymbol.KIND).orElse(null);
    assertNotNull(textSymbol);
    assertEquals("E", textSymbol.getName());
  }

}
