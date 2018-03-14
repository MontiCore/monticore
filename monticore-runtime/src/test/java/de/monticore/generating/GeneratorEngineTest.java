/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Test;

import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.templateengine.FreeMarkerTemplateEngineMock;
import de.monticore.generating.templateengine.FreeMarkerTemplateMock;
import de.monticore.io.FileReaderWriterMock;

/**
 * Tests for {@link de.monticore.generating.GeneratorEngine}.
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class GeneratorEngineTest {
  

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

    assertEquals(1, freeMarkerTemplateEngine.getProcessedTemplates().size());
    FreeMarkerTemplateMock template = freeMarkerTemplateEngine.getProcessedTemplates().iterator().next();
    assertTrue(template.isProcessed());
    assertEquals("the.Template", template.getName());

    assertEquals(1, fileHandler.getStoredFilesAndContents().size());
    assertTrue(fileHandler.getStoredFilesAndContents().containsKey(Paths.get
        (new File("target1/a/GenerateInFile.test").getAbsolutePath())));

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
    assertTrue(sb.length()>0);

    assertEquals(1, freeMarkerTemplateEngine.getProcessedTemplates().size());
    FreeMarkerTemplateMock template = freeMarkerTemplateEngine.getProcessedTemplates().iterator().next();
    assertTrue(template.isProcessed());
    assertEquals("the.Template", template.getName());

    assertEquals(0, fileHandler.getStoredFilesAndContents().size());
   

  }

}
