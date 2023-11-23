/* (c) https://github.com/MontiCore/monticore */

import automata._ast.ASTAutomaton;
import automata._tagging.AutomataTagConformsToSchemaCoCo;
import automatatagdefinition.AutomataTagDefinitionMill;
import automatatagdefinition._cocos.AutomataTagDefinitionCoCoChecker;
import automatatagschema._symboltable.AutomataTagSchemaSymbols2Json;
import automatatagschema._symboltable.IAutomataTagSchemaArtifactScope;
import de.monticore.fqn.fqnenhancedautomata._symboltable.IFQNEnhancedAutomataArtifactScope;
import de.monticore.fqn.fqnenhancedautomatatagschema.FQNEnhancedAutomataTagSchemaMill;
import de.monticore.fqn.fqnenhancedautomatatagschema._symboltable.FQNEnhancedAutomataTagSchemaSymbols2Json;
import de.monticore.fqn.fqnenhancedautomatatagschema._symboltable.IFQNEnhancedAutomataTagSchemaArtifactScope;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tagschema._ast.ASTTagSchema;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class TagSchemaSerializationTest {
  protected static ASTAutomaton model;

  @BeforeClass
  public static void prepare() throws Exception {
    LogStub.init();
    Log.enableFailQuick(false);

    FQNEnhancedAutomataTagSchemaMill.init();

    // Load all schemas into the global scope
    for (File f : Objects.requireNonNull(
            new File("src/test/resources/schema/").listFiles(x -> x.getName().endsWith(".tagschema")))) {
      Optional<ASTTagSchema> schemaOpt = FQNEnhancedAutomataTagSchemaMill.parser().parse(f.getAbsolutePath());
      if (schemaOpt.isPresent())
        FQNEnhancedAutomataTagSchemaMill.scopesGenitorDelegator().createFromAST(schemaOpt.get());
      else
        Log.warn("Failed to load TagSchema " + f);
    }

  }

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void test() throws IOException {
    for (File f : Objects.requireNonNull(
            new File("src/test/resources/schema/").listFiles(x -> x.getName().endsWith(".tagschema")))) {
      Optional<ASTTagSchema> schemaOpt = FQNEnhancedAutomataTagSchemaMill.parser().parse(f.getAbsolutePath());
      if (schemaOpt.isEmpty()) {
        Log.warn("Failed to load TagSchema " + f);
        return;
      }
      Log.warn(f.getName());
      IFQNEnhancedAutomataTagSchemaArtifactScope s = FQNEnhancedAutomataTagSchemaMill.scopesGenitorDelegator().createFromAST(schemaOpt.get());

      FQNEnhancedAutomataTagSchemaSymbols2Json schemaSymbols2Json = new FQNEnhancedAutomataTagSchemaSymbols2Json();
      String serialized = schemaSymbols2Json.serialize(s);

      IFQNEnhancedAutomataTagSchemaArtifactScope copy = schemaSymbols2Json.deserialize(serialized);

      String serializedCopy = schemaSymbols2Json.serialize(copy);

      Assert.assertEquals(serialized, serializedCopy);

    }
  }

}
