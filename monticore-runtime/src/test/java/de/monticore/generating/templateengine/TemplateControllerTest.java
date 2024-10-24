/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.FileReaderWriterMock;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import freemarker.template.Template;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;

/**
 * Tests for {@link TemplateController}.
 */
public class TemplateControllerTest {

  private static final File TARGET_DIR = new File("targetDir");

  private static final Path HWC_DIR = Paths.get("src", "test", "resources", "hwc");

  private TemplateControllerMock tc;

  private FreeMarkerTemplateEngineMock freeMarkerTemplateEngine;

  private FileReaderWriterMock fileHandler;

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {

    final GeneratorSetupMock setup = new GeneratorSetupMock();

    freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock(setup.getConfig());
    fileHandler = new FileReaderWriterMock();
    FileReaderWriter.init(fileHandler);
    setup.setOutputDirectory(TARGET_DIR);
    setup.setFreeMarkerTemplateEngine(freeMarkerTemplateEngine);
    setup.setHandcodedPath(new MCPath(HWC_DIR));
    setup.setFileHandler(fileHandler);
    setup.setTracing(false);

    tc = setup.getNewTemplateController("");
  }

  @AfterAll
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }

  @Disabled
  @Test
  public void testImplicitAstPassing() {
    Assertions.assertNull(tc.getAST());

    tc.include(TEMPLATE_PACKAGE + "A");
    Assertions.assertNull(tc.getAST());

    // pass ast explicit
    tc.include(TEMPLATE_PACKAGE + "A", ASTNodeMock.INSTANCE);

    Assertions.assertNotNull(tc.getAST());
    Assertions.assertSame(ASTNodeMock.INSTANCE, tc.getAST());
    Assertions.assertTrue(Log.getFindings().isEmpty());

  }

  @Test
  public void testWriteArgs() {
    String TEMPLATE_NAME = "the.Template";
    tc.writeArgs(TEMPLATE_NAME, "path.to.file", ".ext", ASTNodeMock.INSTANCE, new ArrayList<>());

    Assertions.assertEquals(1, freeMarkerTemplateEngine.getProcessedTemplates().size());
    FreeMarkerTemplateMock template = freeMarkerTemplateEngine.getProcessedTemplates().iterator()
        .next();
    Assertions.assertTrue(template.isProcessed());
    Assertions.assertEquals(TEMPLATE_NAME, template.getName());
    Assertions.assertNotNull(template.getData());

    Assertions.assertEquals(1, fileHandler.getStoredFilesAndContents().size());

    Path writtenFilePath = Paths.get(TARGET_DIR.getAbsolutePath(), "path/to/file.ext");
    Assertions.assertTrue(fileHandler.getStoredFilesAndContents().containsKey(writtenFilePath));
    Assertions.assertEquals("Content of template: " + TEMPLATE_NAME, fileHandler.getContentForFile(writtenFilePath.toString()).get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testDefaultMethods() {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();

    fileHandler = new FileReaderWriterMock();
    GeneratorSetup config = new GeneratorSetup();
    config.setGlex(glex);
    config.setFileHandler(fileHandler);
    config.setOutputDirectory(TARGET_DIR);
    config.setTracing(false);
    // .externalTemplatePaths(new File[]{})
    TemplateController tc = new TemplateControllerMock(config, "");
    DefaultImpl def = new DefaultImpl();
    StringBuilder result = tc
        .includeArgs(TEMPLATE_PACKAGE + "DefaultMethodCall", Lists.newArrayList(def));
    Assertions.assertNotNull(result);
    Assertions.assertEquals("A", result.toString().trim());
    FileReaderWriter.init();
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * tests if comments are being generated for the Blacklisted Templates using the normal
   * Template Constructor
   */
  @Test
  public void testBlacklisteTemplatesI() throws IOException {
    // init Template Controller under test
    GeneratorSetup setup = new GeneratorSetup();
    TemplateController tc = new TemplateController(setup, "");

    // init test data
    String templateNameI = "foo";
    String templateNameII = "bar";
    Template templateI = new Template(templateNameI, "", null);
    Template templateII = new Template(templateNameII, "", null);
    List<String> blackList = new ArrayList<>();
    blackList.add(templateNameI);

    // configure Template Controller with black list
    tc.setTemplateBlackList(blackList);

    Assertions.assertEquals(1, tc.getTemplateBlackList().size());
    Assertions.assertFalse(tc.isTemplateNoteGenerated(templateI));
    Assertions.assertTrue(tc.isTemplateNoteGenerated(templateII));

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * tests if comments are being generated for the Blacklisted Templates using the second Template
   * Constructor
   */
  @Test
  public void testBlacklistTemplatesII() throws IOException {
    // init Template Controller with black-list under test
    List<String> blackList = new ArrayList<>();
    GeneratorSetup setup = new GeneratorSetup();
    TemplateController tc = new TemplateController(setup, "", blackList);

    // init test data
    String templateNameI = "foo";
    String templateNameII = "bar";
    blackList.add(templateNameI);
    Template templateI = new Template(templateNameI, "", null);
    Template templateII = new Template(templateNameII, "", null);


    Assertions.assertEquals(1, tc.getTemplateBlackList().size());
    Assertions.assertFalse(tc.isTemplateNoteGenerated(templateI));
    Assertions.assertTrue(tc.isTemplateNoteGenerated(templateII));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIncludeArgsDoesNotOverrideParameters() {
    TemplateController controller = new TemplateController(new GeneratorSetup(), "de.monticore.geneing.templateengine.IncludeArgsDoesntOverride1");
    controller.config.getGlex().setGlobalValue("Global", "lolol");
    String result = controller.includeArgs("de.monticore.generating.templateengine.IncludeArgsDoesntOverride1",
        "a", "b").toString();

    Assertions.assertEquals("aba", result.strip()
        .replaceAll("\n", "")
        .replaceAll(" ", "")
        .replaceAll("/\\*(.)*?\\*/", ""));
  }

  @Test
  public void testParametersOfOuterTemplateNotVisible() {
    TemplateController controller = new TemplateController(
        new GeneratorSetup(),
        "de.monticore.generating.templateengine." +
            "OuterParametersNotVisible1");
    try {
      controller.includeArgs(
          "de.monticore.generating.templateengine." +
              "OuterParametersNotVisible1",
          "A");
    } catch (MontiCoreFreeMarkerException e) {
      Assertions.assertTrue(e.getMessage().contains(
          "The following has evaluated to null or missing:\n==> A  [in " +
              "template \"de.monticore.generating.templateengine." +
              "OuterParametersNotVisible2\" at line 2, column 3]"));
    }
  }

  @Test
  public void testInnerParametersNotVisible() {
    TemplateController controller = new TemplateController(
        new GeneratorSetup(),
        "de.monticore.generating.templateengine." +
            "InnerParametersNotVisible1");

    try {
      controller.includeArgs(
          "de.monticore.generating.templateengine." +
              "InnerParametersNotVisible1",
          "A");

    } catch (MontiCoreFreeMarkerException e) {
      Assertions.assertTrue(e.getMessage().contains("The following has evaluated to " +
          "null or missing:\n==> B  [in template \"de.monticore.generating." +
          "templateengine.InnerParametersNotVisible1\" at line 4, column 3]"));
    }
  }

}
