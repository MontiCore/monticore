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
import freemarker.core._TemplateModelException;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.*;

/**
 * Tests for {@link TemplateController}.
 */
public class TemplateControllerTest {

  private static final File TARGET_DIR = new File("targetDir");

  private static final Path HWC_DIR = Paths.get("src", "test", "resources", "hwc");

  private TemplateControllerMock tc;

  private FreeMarkerTemplateEngineMock freeMarkerTemplateEngine;

  private FileReaderWriterMock fileHandler;

  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
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

  @AfterClass
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }

  @Ignore
  @Test
  public void testImplicitAstPassing() {
    assertNull(tc.getAST());

    tc.include(TEMPLATE_PACKAGE + "A");
    assertNull(tc.getAST());

    // pass ast explicit
    tc.include(TEMPLATE_PACKAGE + "A", ASTNodeMock.INSTANCE);

    assertNotNull(tc.getAST());
    assertSame(ASTNodeMock.INSTANCE, tc.getAST());
    assertTrue(Log.getFindings().isEmpty());

  }

  @Test
  public void testWriteArgs() {
    String TEMPLATE_NAME = "the.Template";
    tc.writeArgs(TEMPLATE_NAME, "path.to.file", ".ext", ASTNodeMock.INSTANCE, new ArrayList<>());

    assertEquals(1, freeMarkerTemplateEngine.getProcessedTemplates().size());
    FreeMarkerTemplateMock template = freeMarkerTemplateEngine.getProcessedTemplates().iterator()
        .next();
    assertTrue(template.isProcessed());
    assertEquals(TEMPLATE_NAME, template.getName());
    assertNotNull(template.getData());

    assertEquals(1, fileHandler.getStoredFilesAndContents().size());

    Path writtenFilePath = Paths.get(TARGET_DIR.getAbsolutePath(), "path/to/file.ext");
    assertTrue(fileHandler.getStoredFilesAndContents().containsKey(writtenFilePath));
    assertEquals("Content of template: " + TEMPLATE_NAME,
        fileHandler.getContentForFile(writtenFilePath.toString()).get());
    assertTrue(Log.getFindings().isEmpty());
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
    assertNotNull(result);
    assertEquals("A", result.toString().trim());
    FileReaderWriter.init();
    assertTrue(Log.getFindings().isEmpty());
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

    assertEquals(1, tc.getTemplateBlackList().size());
    assertFalse(tc.isTemplateNoteGenerated(templateI));
    assertTrue(tc.isTemplateNoteGenerated(templateII));

    assertTrue(Log.getFindings().isEmpty());
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


    assertEquals(1, tc.getTemplateBlackList().size());
    assertFalse(tc.isTemplateNoteGenerated(templateI));
    assertTrue(tc.isTemplateNoteGenerated(templateII));
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIncludeArgsDoesNotOverrideParameters() {
    TemplateController controller = new TemplateController(new GeneratorSetup(), "de.monticore.geneing.templateengine.IncludeArgsDoesntOverride1");
    controller.config.getGlex().setGlobalValue("Global", "lolol");
    String result = controller.includeArgs("de.monticore.generating.templateengine.IncludeArgsDoesntOverride1",
        "a", "b").toString();

    assertEquals(
        "aba",
        result.strip()
            .replaceAll("\n", "")
            .replaceAll("\r", "")
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
      assertTrue(e.getMessage().contains(
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
      assertTrue(e.getMessage().contains("The following has evaluated to " +
          "null or missing:\n==> B  [in template \"de.monticore.generating." +
          "templateengine.InnerParametersNotVisible1\" at line 4, column 3]"));
    }
  }

}
