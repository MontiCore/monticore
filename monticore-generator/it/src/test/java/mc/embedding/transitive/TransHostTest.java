/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.GeneratorIntegrationsTest;
import mc.embedding.transitive.transhost._symboltable.TransHostLanguage;
import mc.embedding.transitive.transhost._symboltable.TransStartSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransHostTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final TransHostLanguage language = new TransHostLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/mc/embedding/transitive"));

    final GlobalScope scope = new GlobalScope(modelPath, language, resolvingConfiguration);

    TransStartSymbol hostSymbol = scope.<TransStartSymbol>resolve("TH", TransStartSymbol.KIND).orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("TH", hostSymbol.getName());
  }

}
