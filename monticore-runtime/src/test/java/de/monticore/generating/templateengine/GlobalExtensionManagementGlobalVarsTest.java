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

import com.google.common.collect.Lists;

import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.FreeMarkerConfigurationBuilder;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriterMock;
import de.se_rwth.commons.logging.Log;

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
    FreeMarkerTemplateEngine freeMarkerTemplateEngine = new FreeMarkerTemplateEngine(new FreeMarkerConfigurationBuilder().build());
    glex = new GlobalExtensionManagement();
    
    GeneratorSetup config = new GeneratorSetup();
    config.setFreeMarkerTemplateEngine(freeMarkerTemplateEngine);
    config.setGlex(glex);
    config.setFileHandler(new FileReaderWriterMock());
    config.setOutputDirectory(new File("dummy"));
    config.setTracing(false);
    tc = new TemplateControllerMock(config, "");
  }

  @Test
  public void testGlobalVars() {
    glex.defineGlobalVar("test", "test");
    glex.defineGlobalVar("asd", new String("asd"));

    String output = tc.include(TEMPLATE_PACKAGE + "GlobalVars");
    assertEquals("testasd", output.replaceAll("\\s+", ""));

    glex.changeGlobalVar("asd", new String("aaa"));
    output = tc.include(TEMPLATE_PACKAGE + "GlobalVars");
    assertEquals("testaaa", output.replaceAll("\\s+", ""));

    glex.defineGlobalVar("liste", new ArrayList<>());
    glex.addToGlobalVar("liste", new String("a"));
    glex.addToGlobalVar("liste", new String("b"));
    glex.addToGlobalVar("liste", new String("c"));
    output = tc.include(TEMPLATE_PACKAGE + "GlobalListVars");
    assertEquals("abc", output.replaceAll("\\s+", ""));
  }
  
  @Test
  public void testVariables4() {
    GeneratorSetup s = new GeneratorSetup();
    s.setTracing(false);
    GeneratorEngine ge = new GeneratorEngine(s);
    ASTNodeMock ast = new ASTNodeMock();

    // override same variable
    String res = ge.generate(TEMPLATE_PACKAGE + "TestVariables4", ast);

    assertEquals("\n\nA:16\nB:38\nC:555\n", res);
  }

}
