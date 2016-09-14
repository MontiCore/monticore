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

import de.monticore.generating.templateengine.freemarker.FreeMarkerConfigurationBuilder;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriterMock;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A simple unit test invoking a template which uses the new template logger.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date: 2015-09-03 19:22:26 +0200 (Do, 03 Sep 2015) $
 * @since 4.0.1
 */
public class TemplateLoggerTest {
  
  private static final File TARGET_DIR = new File("target");
  
  private TemplateControllerMock tc;
  
  private GlobalExtensionManagement glex;
  
  private FreeMarkerTemplateEngine freeMarkerTemplateEngine;
  
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
    
    freeMarkerTemplateEngine = new FreeMarkerTemplateEngine(
        new FreeMarkerConfigurationBuilder().build());
    
    fileHandler = new FileReaderWriterMock();
    TemplateControllerConfiguration config = new TemplateControllerConfigurationBuilder()
        .glex(glex)
        .freeMarkerTemplateEngine(freeMarkerTemplateEngine)
        .fileHandler(fileHandler)
        .classLoader(getClass().getClassLoader())
        .externalTemplatePaths(new File[] {})
        .outputDirectory(TARGET_DIR)
        .tracing(false)
        .build();
    
    tc = new TemplateControllerMock(config, "");
  }
  
  /**
   * Executes a test templates which invokes the template logger.
   */
  @Test
  public void demonstrateTemplateLogging() {
    String result = tc.include(TEMPLATE_PACKAGE + "Log");
    assertNotNull(result);
    assertEquals("A", result.trim());
  }
  
}
