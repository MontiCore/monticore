/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
    FreeMarkerTemplateEngineMock freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock();
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
    FreeMarkerTemplateEngineMock freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock();
    setup.setFreeMarkerTemplateEngine(freeMarkerTemplateEngine);
    FileReaderWriterMock fileHandler = new FileReaderWriterMock();
    setup.setFileHandler(fileHandler);
    
    GeneratorEngineMock generatorEngine = new GeneratorEngineMock(setup);

    StringBuilder sb = generatorEngine.generate("the.Template");
    assertTrue(sb.length()>0);

    assertEquals(1, freeMarkerTemplateEngine.getProcessedTemplates().size());
    FreeMarkerTemplateMock template = freeMarkerTemplateEngine.getProcessedTemplates().iterator().next();
    assertTrue(template.isProcessed());
    assertEquals("the.Template", template.getName());

    assertEquals(0, fileHandler.getStoredFilesAndContents().size());
   

  }

}
