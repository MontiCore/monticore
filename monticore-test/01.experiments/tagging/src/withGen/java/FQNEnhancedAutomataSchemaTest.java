/* (c) https://github.com/MontiCore/monticore */

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class FQNEnhancedAutomataSchemaTest {
  protected static ASTAutomaton model;

  @BeforeClass
  public static void prepare() throws Exception {
    LogStub.init();
    Log.enableFailQuick(false);

    FQNEnhancedAutomataMill.init();
    model = FQNEnhancedAutomataMill.parser()
            .parse("src/test/resources/models/Simple.aut").orElseThrow();
    FQNEnhancedAutomataMill.scopesGenitorDelegator().createFromAST(model);

    FQNEnhancedAutomataTagSchemaMill.init();
    FQNEnhancedAutomataTagSchemaMill.globalScope().getSymbolPath().addEntry(new File("src/test/resources/").toPath());

    for (File f : Objects.requireNonNull(
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

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void testValidTags1() throws IOException {
    testCoCo("src/test/resources/models/Simple.tags");
    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSpotRootWithSimpleInsteadOfValued() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags1.tags");
    Assert.assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNWithInvalidSimpleTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags2.tags");
    Assert.assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNHWithInvalidSimpleTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags3.tags");
    Assert.assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNHWithInvalidValuedTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags4.tags");
    Assert.assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNWithinWithInvalidSimpleTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTags5.tags");
    Assert.assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNWithinWithInvalidPrivateTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidTagsPrivate.tags");
    Assert.assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testEnhancedFQNNonExtendedTag() throws IOException {
    testCoCo("src/test/resources/models/InvalidEnhancedTags1.tags");
    Assert.assertEquals(2, Log.getErrorCount());
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
