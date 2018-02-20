/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class GlobalScopeTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

//  @Test
  public void testLoadTopLevelSymbol() {
    ModelingLanguage entityLanguage = new EntityLanguage();
    
    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/modelloader/modelpath"));

    ResolvingConfiguration resolverConfig = new ResolvingConfiguration();
    resolverConfig.addDefaultFilter(CommonResolvingFilter.create(EntitySymbol.KIND));

    final MutableScope globalScope =
        new GlobalScope(modelPath, entityLanguage, resolverConfig);
    
    EntitySymbol entitySymbol = globalScope.<EntitySymbol> resolve("models.E", EntitySymbol.KIND).orElse(null);
    
    assertNotNull(entitySymbol);
    assertEquals("E", entitySymbol.getName());
    assertEquals("models.E", entitySymbol.getFullName());
    assertEquals("models", entitySymbol.getPackageName());
  }
}
