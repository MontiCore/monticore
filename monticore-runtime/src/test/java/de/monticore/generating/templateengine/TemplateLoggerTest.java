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

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriterMock;

/**
 * A simple unit test invoking a template which uses the new template logger.
 *
 * @since 4.0.1
 */
public class TemplateLoggerTest {
  
  private static final File TARGET_DIR = new File("target");
  
  private TemplateControllerMock tc;
  
  private GlobalExtensionManagement glex;
    
  private FileReaderWriterMock fileHandler;
  
  /**
   * Sets up the template controller for this 'test'. This is copied entirely
   * from {@link TemplateControllerTest} but uses the production
   * {@link FreeMarkerTemplateEngine} instead of the mock in order to have the
   * template actually executed. Otherwise the log statements would do nothing.
   */
  @Before
  public void setup() {
    glex = new GlobalExtensionManagement();
 
    fileHandler = new FileReaderWriterMock();
    GeneratorSetup config = new GeneratorSetup();
    config.setGlex(glex);
    config.setFileHandler(fileHandler);
    config.setOutputDirectory(TARGET_DIR);
    config.setTracing(false);
    // .externalTemplatePaths(new File[]{})
    tc = new TemplateControllerMock(config, "");
  }
  
  /**
   * Executes a test templates which invokes the template logger.
   */
  @Test
  public void demonstrateTemplateLogging() {
    StringBuilder result = tc.include(TEMPLATE_PACKAGE + "Log");
    assertNotNull(result);
    assertEquals("A", result.toString().trim());
  }
  
}
