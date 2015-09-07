/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.generating.templateengine;

import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.templateengine.freemarker.FreeMarkerConfigurationBuilder;
import de.monticore.io.FileReaderWriterMock;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.*;

/**
 * Tests for {@link TemplateController}.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class TemplateControllerTest {
  
  private static final File TARGET_DIR = new File("targetDir");
  
  private TemplateControllerMock tc;
  private FreeMarkerTemplateEngineMock freeMarkerTemplateEngine;
  private FileReaderWriterMock fileHandler;
  
  @Before
  public void setup() {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock(new FreeMarkerConfigurationBuilder().build());
    
    fileHandler = new FileReaderWriterMock();
    TemplateControllerConfiguration config = new TemplateControllerConfigurationBuilder()
                                                .glex(glex)
                                                .freeMarkerTemplateEngine(freeMarkerTemplateEngine)
                                                .fileHandler(fileHandler)
                                                .classLoader(getClass().getClassLoader())
                                                .externalTemplatePaths(new File[]{})
                                                .targetDir(TARGET_DIR)
                                                .tracing(false)
                                                .build();
    
    tc = new TemplateControllerMock(config, "");
  }
  
  
  @Test
  public void testImplicitAstPassing() {
    assertNull(tc.getAST()); 
    
    tc.include(TEMPLATE_PACKAGE + "A");
    assertNull(tc.getSubController().getAST());
    
    // pass ast explicit
    tc.include(TEMPLATE_PACKAGE + "A", ASTNodeMock.INSTANCE);
    TemplateControllerMock subTC = tc.getSubController();
    
    assertNotNull(subTC.getAST());
    assertSame(ASTNodeMock.INSTANCE, subTC.getAST());
    
    // pass ast implicit
    subTC.include(TEMPLATE_PACKAGE + "B");
    TemplateControllerMock subSubTC = subTC.getSubController();
    
    assertSame(ASTNodeMock.INSTANCE, subSubTC.getAST());
    
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
  
    
}
