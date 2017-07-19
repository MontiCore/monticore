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

import com.google.common.collect.Lists;
import de.monticore.generating.templateengine.freemarker.FreeMarkerConfigurationBuilder;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriterMock;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link GlobalExtensionManagement}.
 *
 * @author (last commit) $Author$
 *          $Date$
 */
public class GlobalExtensionManagementGlobalVarsTest {

  private TemplateControllerMock tc;
  private GlobalExtensionManagement glex;

  @Before
  public void setup() {
    glex = new GlobalExtensionManagement();

    FreeMarkerTemplateEngine freeMarkerTemplateEngine = new FreeMarkerTemplateEngine(new FreeMarkerConfigurationBuilder().build());

    TemplateControllerConfiguration config = new TemplateControllerConfigurationBuilder()
        .glex(glex)
        .freeMarkerTemplateEngine(freeMarkerTemplateEngine)
        .fileHandler(new FileReaderWriterMock())
        .classLoader(getClass().getClassLoader())
        .externalTemplatePaths(new File[]{})
        .outputDirectory(new File("dummy"))
        .tracing(false)
        .build();

    tc = new TemplateControllerMock(config, "");
  }

  @Test
  public void testGlobalVars() {
    glex.defineGlobalVar("test");
    glex.defineGlobalVars(Lists.newArrayList("a", "b"));
    glex.defineGlobalVar("asd", new String("asd"));

    String output = tc.include(TEMPLATE_PACKAGE + "GlobalVars");
    assertEquals("asd", output.replaceAll("\\s+", ""));

    glex.changeGlobalVar("asd", new String("aaa"));
    output = tc.include(TEMPLATE_PACKAGE + "GlobalVars");
    assertEquals("aaa", output.replaceAll("\\s+", ""));

    glex.defineGlobalVar("liste", new ArrayList<>());
    glex.addToGlobalVar("liste", new String("a"));
    glex.addToGlobalVar("liste", new String("b"));
    glex.addToGlobalVar("liste", new String("c"));
    output = tc.include(TEMPLATE_PACKAGE + "GlobalListVars");
    assertEquals("abc", output.replaceAll("\\s+", ""));
  }
}
