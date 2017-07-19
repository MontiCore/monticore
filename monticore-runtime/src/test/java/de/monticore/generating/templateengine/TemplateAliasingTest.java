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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Joiner;

import de.monticore.generating.templateengine.freemarker.FreeMarkerConfigurationBuilder;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriterMock;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import freemarker.core.Macro;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class TemplateAliasingTest {

  private static final File TARGET_DIR = new File("target");
  private static final int NUMBER_ALIASES = 18;
  public static final String ALIASES_PACKAGE = "de.monticore.generating.templateengine.templates.aliases.";


  private TemplateControllerMock tc;


  @BeforeClass
  public static void init() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setup() {
    GlobalExtensionManagement globalsManager = new GlobalExtensionManagement();
    FreeMarkerTemplateEngine freeMarkerTemplateEngine = new FreeMarkerTemplateEngine(new
        FreeMarkerConfigurationBuilder().build());

    FileReaderWriterMock fileHandler = new FileReaderWriterMock();


    TemplateControllerConfiguration config = new TemplateControllerConfigurationBuilder()
                                                    .glex(globalsManager)
                                                    .freeMarkerTemplateEngine(freeMarkerTemplateEngine)
                                                    .fileHandler(fileHandler)
                                                    .classLoader(getClass().getClassLoader())
                                                    .outputDirectory(TARGET_DIR)
                                                    .tracing(false)
                                                    .build();
    tc = new TemplateControllerMock(config, "");

    Slf4jLog.getFindings().clear();
  }

  @Test
  public void testIncludeAlias() {
    String templateOutput =
        tc.include(ALIASES_PACKAGE + "IncludeAlias");

    assertEquals("Plain is included.", templateOutput);
  }

  @Test
  public void testIncludeArgsAndSignatureAlias() {
    assertNull(tc.getAliases());
    String templateOutput =
        tc.include(ALIASES_PACKAGE + "IncludeArgsAndSignatureAlias");
    TemplateController tcChild = tc.getSubController().getSubController();
    assertNotNull(tcChild);
    assertTrue(tcChild.isSignatureInitialized());
    
    assertEquals(3, tcChild.getSignature().size());
    assertEquals("name", tcChild.getSignature().get(0));
    assertEquals("age", tcChild.getSignature().get(1));
    assertEquals("city", tcChild.getSignature().get(2));
    
    assertEquals(3, tcChild.getArguments().size());
    assertEquals("Charly", tcChild.getArguments().get(0));
    assertEquals("30", tcChild.getArguments().get(1));
    assertEquals("Aachen", tcChild.getArguments().get(2));
    
    assertEquals("Name is Charly, age is 30, city is Aachen", templateOutput);
    
    assertAliases(tcChild, tc.getAliases().size()
    );
  }

  @Test
  public void testLogAliases() {
    assertNull(tc.getAliases());
    tc.include(ALIASES_PACKAGE + "LogAliases");
    assertAliases(tc, NUMBER_ALIASES);

    /* TODO GV, RH: is deprecated?
    Collection<String> expectedLogs = Arrays.asList(
        "Trace Message",
        "Debug Message",
        "Info Message",
        "Warn Message",
        "Error Message"
        );
*/
    Collection<String> expectedLogs = Arrays.asList(
        "Warn Message",
        "Error Message"
        );

    assertEquals(2, Slf4jLog.getFindings().size());
    assertErrors(expectedLogs, Slf4jLog.getFindings());
  }

  
  /**
   * Asserts that each of the expectedErrors is found at least once in the
   * actualErrors.
   * 
   * @param expectedErrors
   * @param actualErrors
   */
  private static void assertErrors(Collection<String> expectedErrors,
      Collection<Finding> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    for (String expectedError : expectedErrors) {
      boolean found = actualErrors.stream().filter(s -> s.getMsg().equals(expectedError)).count() >= 1;
      assertTrue("The following expected error was not found: " + expectedError
          + actualErrorsJoined, found);
    }
  }

  private void assertAliases(TemplateController tc, int expectedNumberAliases) {
    List<Macro> aliases = tc.getAliases();
    assertNotNull(aliases);
    assertEquals(expectedNumberAliases, aliases.size());
    
    for (Macro macro : aliases) {
      assertTrue(macro.isFunction());
    }
  }
  
}
