/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.templateengine.FreeMarkerTemplateEngineMock;
import de.monticore.generating.templateengine.FreeMarkerTemplateMock;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.FileReaderWriterMock;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;

/**
 * Tests for {@link de.monticore.generating.GeneratorEngine}.
 *
 */
public class GeneratorEngineTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testGenerateInFile() {
    ASTNodeMock node = new ASTNodeMock();

    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(new File("target1"));
    FreeMarkerTemplateEngineMock freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock(setup.getConfig());
    setup.setFreeMarkerTemplateEngine(freeMarkerTemplateEngine);
    FileReaderWriterMock fileHandler = new FileReaderWriterMock();
    setup.setFileHandler(fileHandler);
    
    GeneratorEngineMock generatorEngine = new GeneratorEngineMock(setup);

    generatorEngine.generate("the.Template", Paths.get("a/GenerateInFile.test"), node);

    Assertions.assertEquals(1, freeMarkerTemplateEngine.getProcessedTemplates().size());
    FreeMarkerTemplateMock template = freeMarkerTemplateEngine.getProcessedTemplates().iterator().next();
    Assertions.assertTrue(template.isProcessed());
    Assertions.assertEquals("the.Template", template.getName());

    Assertions.assertEquals(1, fileHandler.getStoredFilesAndContents().size());
    Assertions.assertTrue(fileHandler.getStoredFilesAndContents().containsKey(Paths.get
        (new File("target1/a/GenerateInFile.test").getAbsolutePath())));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @AfterAll
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }

  @Test
  public void testGenerateStringBuilder() {
    final GeneratorSetup setup = new GeneratorSetup();
    FreeMarkerTemplateEngineMock freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock(setup.getConfig());
    setup.setFreeMarkerTemplateEngine(freeMarkerTemplateEngine);
    FileReaderWriterMock fileHandler = new FileReaderWriterMock();
    setup.setFileHandler(fileHandler);
    
    GeneratorEngineMock generatorEngine = new GeneratorEngineMock(setup);

    StringBuilder sb = generatorEngine.generateNoA("the.Template");
    Assertions.assertTrue(sb.length()>0);

    Assertions.assertEquals(1, freeMarkerTemplateEngine.getProcessedTemplates().size());
    FreeMarkerTemplateMock template = freeMarkerTemplateEngine.getProcessedTemplates().iterator().next();
    Assertions.assertTrue(template.isProcessed());
    Assertions.assertEquals("the.Template", template.getName());

    Assertions.assertEquals(0, fileHandler.getStoredFilesAndContents().size());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testWrongPath() {
    final GeneratorSetup setup = new GeneratorSetup();
    FileReaderWriterMock fileHandler = new FileReaderWriterMock();
    File file = new File("doesnotexist");
    setup.setAdditionalTemplatePaths(Lists.newArrayList(file));
    setup.setFileHandler(fileHandler);
    FreeMarkerTemplateEngineMock freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock(setup.getConfig());
    Assertions.assertEquals(1, Log.getFindingsCount());
    Assertions.assertEquals("0xA1020 Unable to load templates from non-existent path doesnotexist", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testExistHWC() {
    String classname = "test.A";
    String notExistName = "test.B";

    Assertions.assertTrue(existsHandwrittenClass(new MCPath("src/test/resources/hwc"), classname));
    Assertions.assertFalse(existsHandwrittenClass(new MCPath("src/test/resources/hwc"), notExistName));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
