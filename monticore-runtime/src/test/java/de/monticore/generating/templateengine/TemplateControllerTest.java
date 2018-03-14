/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.ast.ASTNodeMock;
import de.monticore.io.FileReaderWriterMock;
import de.monticore.io.paths.IterablePath;

/**
 * Tests for {@link TemplateController}.
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class TemplateControllerTest {
  
  private static final File TARGET_DIR = new File("targetDir");
  
  private static final Path HWC_DIR = Paths.get("src", "test", "resources", "hwc");
  
  private TemplateControllerMock tc;
  private FreeMarkerTemplateEngineMock freeMarkerTemplateEngine;
  private FileReaderWriterMock fileHandler;
  
  @Before
  public void setup() {
   
    final GeneratorSetupMock setup = new GeneratorSetupMock();
    
    freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock(setup.getConfig());
    fileHandler = new FileReaderWriterMock();

    setup.setOutputDirectory(TARGET_DIR);
    setup.setFreeMarkerTemplateEngine(freeMarkerTemplateEngine);
    setup.setHandcodedPath(
        IterablePath.from(HWC_DIR.toFile(), setup.getDefaultFileExtension()));
    setup.setFileHandler(fileHandler);
    setup.setTracing(false);
    
    tc = setup.getNewTemplateController("");
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
    
  }
  
  @Test
  public void testWriteArgs() {
    String TEMPLATE_NAME = "the.Template";
    tc.writeArgs(TEMPLATE_NAME, "path.to.file", ".ext", ASTNodeMock.INSTANCE, new ArrayList<>());
    
    assertEquals(1, freeMarkerTemplateEngine.getProcessedTemplates().size());
    FreeMarkerTemplateMock template = freeMarkerTemplateEngine.getProcessedTemplates().iterator().next();
    assertTrue(template.isProcessed());
    assertEquals(TEMPLATE_NAME, template.getName());
    assertNotNull(template.getData());
    
    assertEquals(1, fileHandler.getStoredFilesAndContents().size());

    Path writtenFilePath = Paths.get(TARGET_DIR.getAbsolutePath(), "path/to/file.ext");
    assertTrue(fileHandler.getStoredFilesAndContents().containsKey(writtenFilePath));
    assertEquals("Content of template: " + TEMPLATE_NAME, fileHandler.getContentForFile(writtenFilePath.toString()).get());
  }
  
  @Test
  public void testExistHWC() {
    String filename = Paths.get("test", "A.java").toString();
    String fileNameNoExtension = Paths.get("test", "A")
        .toString();
    
    assertTrue(tc.existsHandwrittenFile(filename));
    assertTrue(tc.existsHandwrittenFile(fileNameNoExtension));
    assertTrue(!tc.existsHandwrittenFile(fileNameNoExtension + ".txt"));
    
    String classname = "test.A";
    String notExistName = "test.B";
    
    assertTrue(tc.existsHandwrittenClass(classname));
    assertTrue(!tc.existsHandwrittenClass(notExistName));
  }
    
}
