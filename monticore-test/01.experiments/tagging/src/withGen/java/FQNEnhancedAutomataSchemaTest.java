/* (c) https://github.com/MontiCore/monticore */

import com.google.common.base.Preconditions;
import de.monticore.fqn.fqnautomata._ast.ASTAutomaton;
import de.monticore.fqn.fqnautomata._tagging.FQNAutomataTagConformsToSchemaCoCo;
import de.monticore.fqn.fqnenhancedautomata.FQNEnhancedAutomataMill;
import de.monticore.fqn.fqnenhancedautomata._tagging.FQNEnhancedAutomataTagConformsToSchemaCoCo;
import de.monticore.fqn.fqnenhancedautomatatagdefinition.FQNEnhancedAutomataTagDefinitionMill;
import de.monticore.fqn.fqnenhancedautomatatagdefinition._cocos.FQNEnhancedAutomataTagDefinitionCoCoChecker;
import de.monticore.fqn.fqnenhancedautomatatagschema.FQNEnhancedAutomataTagSchemaMill;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tagschema._ast.ASTTagSchema;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FQNEnhancedAutomataSchemaTest {
  protected static ASTAutomaton model;

  @BeforeAll
  public static void prepare() throws Exception {
    LogStub.init();
    Log.enableFailQuick(false);

    FQNEnhancedAutomataMill.init();
    model = FQNEnhancedAutomataMill.parser()
            .parse("src/test/resources/models/Simple.aut").orElseThrow();
    FQNEnhancedAutomataMill.scopesGenitorDelegator().createFromAST(model);

    FQNEnhancedAutomataTagSchemaMill.init();
    FQNEnhancedAutomataTagSchemaMill.globalScope().getSymbolPath().addEntry(new File("src/test/resources/").toPath());

    for (File f : Preconditions.checkNotNull(
            new File("src/test/resources/schema/").listFiles(x -> x.getName().endsWith(".tagschema")))) {
      Optional<ASTTagSchema> schemaOpt = FQNEnhancedAutomataTagSchemaMill.parser().parse(f.getAbsolutePath());
      if (schemaOpt.isPresent()) {
        new de.monticore.tagging.tagschema.TagSchemaAfterParseTrafo().transform(schemaOpt.get());
        FQNEnhancedAutomataTagSchemaMill.scopesGenitorDelegator().createFromAST(schemaOpt.get());
      } else
        Log.warn("Failed to load TagSchema " + f);
    }

    FQNEnhancedAutomataTagDefinitionMill.init();
  }

  @BeforeEach
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void testValidTags1() throws IOException {
    testCoCo("src/test/resources/models/Simple.tags");
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSpotRootWithSimpleInsteadOfValued() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags1.tags");
    assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNWithInvalidSimpleTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags2.tags");
    assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNHWithInvalidSimpleTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags3.tags");
    assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNHWithInvalidValuedTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags4.tags");
    assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNWithinWithInvalidSimpleTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags5.tags");
    assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNWithinWithInvalidPrivateTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTagsPrivate.tags");
    assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testEnhancedFQNNonExtendedTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidEnhancedTags1.tags");
    assertEquals(2, Log.getErrorCount());
  }

  protected void testCoCo(String file) throws IOException {
    FQNEnhancedAutomataTagDefinitionCoCoChecker coCoChecker = new FQNEnhancedAutomataTagDefinitionCoCoChecker();
    coCoChecker.addCoCo(new FQNEnhancedAutomataTagConformsToSchemaCoCo(model));
    coCoChecker.addCoCo(new FQNAutomataTagConformsToSchemaCoCo(model)); // TODO: Move this into one CoCo?

    Optional<ASTTagUnit> n = FQNEnhancedAutomataTagDefinitionMill.parser().parse(file);
    FQNEnhancedAutomataTagDefinitionMill.scopesGenitorDelegator().createFromAST(n.get());

    coCoChecker.checkAll(n.get());
  }
}
