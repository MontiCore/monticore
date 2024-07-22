/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._tagging.AutomataTagConformsToSchemaCoCo;
import automatatagdefinition.AutomataTagDefinitionMill;
import automatatagdefinition._cocos.AutomataTagDefinitionCoCoChecker;
import automatatagschema.AutomataTagSchemaMill;
import automatatagschema._symboltable.AutomataTagSchemaSymbols2Json;
import automatatagschema._symboltable.IAutomataTagSchemaArtifactScope;
import com.google.common.io.Files;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tagschema._ast.ASTTagSchema;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class AutomataSchemaTest {
  protected static ASTAutomaton model;

  static File tempDir;

  @BeforeClass
  public static void prepare() throws Exception {
    tempDir = java.nio.file.Files.createTempDirectory("AutomataSchemaTest").toFile();
    if (!tempDir.exists())
      tempDir.mkdirs();
    LogStub.init();
    Log.enableFailQuick(false);

    AutomataMill.init();
    model = AutomataMill.parser()
            .parse("src/test/resources/models/Simple.aut").orElseThrow();
    AutomataMill.scopesGenitorDelegator().createFromAST(model);

    AutomataTagSchemaMill.init();

    AutomataTagSchemaSymbols2Json symbols2Json = new AutomataTagSchemaSymbols2Json();

    // Serialize all schemas and store the .sym files (so they can be loaded later on)
    // Note, that the resolving using packages is not supported for tagschemas
    for (File f : Objects.requireNonNull(
            new File("src/test/resources/schema/").listFiles(x -> x.getName().endsWith(".tagschema")))) {
      Optional<ASTTagSchema> schemaOpt = AutomataTagSchemaMill.parser().parse(f.getAbsolutePath());
      if (schemaOpt.isPresent()) {
        new de.monticore.tagging.tagschema.TagSchemaAfterParseTrafo().transform(schemaOpt.get());
        IAutomataTagSchemaArtifactScope artifactScope = AutomataTagSchemaMill.scopesGenitorDelegator().createFromAST(schemaOpt.get());
        String symFile = new File(new File(tempDir, "schema"), Files.getNameWithoutExtension(f.getName()) + ".sym").toString();
        symbols2Json.store(artifactScope, symFile);
        Assert.assertEquals("TagSchema " + f + " logged errors",0, Log.getErrorCount());
      } else {
        Log.clearFindings();
        Log.warn("Failed to load TagSchema " + f);
      }
    }
    // Clear the scope again
    AutomataTagSchemaMill.globalScope().clear();
    // And add the serialized schema-symbols files to the symbol path
    AutomataTagSchemaMill.globalScope().getSymbolPath().addEntry(new File(tempDir, "schema").toPath());

    // Finally, switch to the TagDef language
    AutomataTagDefinitionMill.init();
    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @Test
  public void testResolve() throws IOException {
    Assert.assertTrue(AutomataTagSchemaMill.globalScope().resolveTagSchema("AutomataSchema").isPresent());
    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testValidTags1() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/Simple.tags");
    Assert.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSpotRootWithSimpleInsteadOfValued() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidTags1.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNWithInvalidSimpleTag() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidTags2.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNHWithInvalidSimpleTag() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidTags3.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNHWithInvalidValuedTag() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidTags4.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNWithinWithInvalidSimpleTag() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidTags5.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testComplexMissing() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidTags7.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testComplexTooMany() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidTags8.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }


  @Test
  public void testFQNWithinWithInvalidPrivateTag() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidTagsPrivate.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNComplexMissingInner() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidComplexTags1.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNComplexTooManyInner() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidComplexTags2.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNComplexTypoInner() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidComplexTags3.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNComplexNotANumber() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidComplexTags4.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNComplexNotABoolean() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidComplexTags5.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNComplexUnknownInnerComplex() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidComplexTags6.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNComplexInvalidInnerComplex() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidComplexTags7.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNPattern() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidPatternTags1.tags");
    Assert.assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testFQNPatternWithin() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/InvalidPatternTags2.tags");
    Assert.assertEquals(2, Log.getErrorCount());
  }

  @Test
  public void testFQNNonConformingTags() throws IOException {
    testTagDefCoCoChecker("src/test/resources/models/NonConformingTags.tags");
    Assert.assertEquals(0, Log.getErrorCount());
  }

  protected void testTagDefCoCoChecker(String file) throws IOException {
    AutomataTagDefinitionCoCoChecker coCoChecker = new AutomataTagDefinitionCoCoChecker();
    coCoChecker.addCoCo(new AutomataTagConformsToSchemaCoCo(model));

    Optional<ASTTagUnit> n = AutomataTagDefinitionMill.parser().parse(file);
    AutomataTagDefinitionMill.scopesGenitorDelegator().createFromAST(n.get());

    coCoChecker.checkAll(n.get());
  }

  @AfterClass
  public static void cleanUp() throws IOException {
    if (tempDir.exists()) {
      FileUtils.deleteDirectory(tempDir);
    }
  }
}
