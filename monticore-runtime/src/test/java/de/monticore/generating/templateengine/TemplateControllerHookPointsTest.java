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
import java.io.IOException;
import java.util.Arrays;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests hook point methods of {@link TemplateController}
 *
 * @author  Pedram Nazari
 *
 */
public class TemplateControllerHookPointsTest {
  
  private TemplateController tc;
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
                                                .targetDir(new File("dummy"))
                                                .tracing(false)
                                                .build();
    
    tc = new TemplateController(config, "");
  }
  
  @Test
  public void testUndefinedHookReturnsEmptyString() {
    assertEquals("", tc.defineHookPoint("hp1"));
  }
  
  @Test
  public void testSetStringHook() {
    String hpValue = null;
    
    
    // define new hook point hp1
    glex.bindHookPoint("hp1", new StringHookPoint("value of hp1"));
    hpValue = tc.defineHookPoint("hp1");
    assertNotNull(hpValue);
    assertEquals("value of hp1", hpValue);
    
    // overwrite value of hook point hp1
    glex.bindHookPoint("hp1", new StringHookPoint("new value of hp1"));
    hpValue = tc.defineHookPoint("hp1");
    assertNotNull(hpValue);
    assertEquals("new value of hp1", hpValue);
    
    // define new hook point hp2
    glex.bindHookPoint("hp2", new StringHookPoint("value of hp2"));
    hpValue = tc.defineHookPoint("hp2");
    assertNotNull(hpValue);
    assertEquals("value of hp2", hpValue);
    // hp1 still exists
    assertEquals("new value of hp1", tc.defineHookPoint("hp1"));
  }
  
  
  @Test
  public void testSetTemplateHook() {
    String hpValue = null;
    
    // define new hook point hp1
    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "HelloWorld"));
    hpValue = tc.defineHookPoint("hp1");
    assertNotNull(hpValue);
    assertEquals("Hello World!", hpValue);
    
    // overwrite value of hook point hp1
    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "HowAreYou"));
    hpValue = tc.defineHookPoint("hp1");
    assertNotNull(hpValue);
    assertEquals("How Are You?", hpValue);
    
    // define new hook point hp2
    glex.bindHookPoint("hp2", new TemplateHookPoint(TEMPLATE_PACKAGE + "HelloWorld"));
    hpValue = tc.defineHookPoint("hp2");
    assertNotNull(hpValue);
    assertEquals("Hello World!", hpValue);
    // hp1 still exists
    assertEquals("How Are You?", tc.defineHookPoint("hp1"));
  }
  
  @Test
  public void testSetCodeHook() {
    String hpValue = null;
    
    // define new hook point hp1
    CodeHookPointMock command = new CodeHookPointMock("command1");
    glex.bindHookPoint("hp1", command);
    hpValue = tc.defineHookPoint("hp1");
    assertNotNull(hpValue);
    assertEquals("command1", hpValue);
    
    // overwrite value of hook point hp1
    command = new CodeHookPointMock("command2");
    glex.bindHookPoint("hp1", command);
    hpValue = tc.defineHookPoint("hp1");
    assertNotNull(hpValue);
    assertEquals("command2", hpValue);
    
    // overwrite value of hook point hp1
    command = new CodeHookPointMock("command3");
    glex.bindHookPoint("hp2", command);
    hpValue = tc.defineHookPoint("hp2");
    assertNotNull(hpValue);
    assertEquals("command3", hpValue);
    // hp1 still exists
    assertEquals("command2", tc.defineHookPoint("hp1"));
  }
  
  @Test
  public void testStringTemplateCodeHookCombinations() {
    final String hp = "hp";
    
    glex.bindHookPoint(hp, new StringHookPoint("StringHook"));
    assertEquals("StringHook", tc.defineHookPoint(hp));
    
    glex.bindHookPoint(hp, new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    assertEquals("A", tc.defineHookPoint(hp));
    
    CodeHookPointMock command = new CodeHookPointMock("command");
    glex.bindHookPoint(hp, command);
    assertEquals("command", tc.defineHookPoint(hp));
    
    glex.bindHookPoint(hp, new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    assertEquals("A", tc.defineHookPoint(hp));
    
    glex.bindHookPoint(hp, new StringHookPoint("StringHook"));
    assertEquals("StringHook", tc.defineHookPoint(hp));
  }
  
  @Test
  public void testStringHookInSubtemplate() {
    assertEquals("TopStringHook Hello Brave New World!", tc.include(TEMPLATE_PACKAGE + "TopStringHook"));
  }
  
  @Test
  public void testTemplateHookInSubtemplate() {
    assertEquals("TopTemplateHook A", tc.include(TEMPLATE_PACKAGE + "TopTemplateHook"));
  }
  
  @Test
  public void testBeforeTemplates() {
    assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A"));
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("BA", tc.include(TEMPLATE_PACKAGE + "A"));
    
    // previously set template is overwritten
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("CA", tc.include(TEMPLATE_PACKAGE + "A"));
    
    // pass a list of templates
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    assertEquals("BCA", tc.include(TEMPLATE_PACKAGE + "A"));
  }

  @Test
  public void testAfterTemplates() {
    assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A"));
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("AB", tc.include(TEMPLATE_PACKAGE + "A"));
    
    // previously set template is overwritten
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("AC", tc.include(TEMPLATE_PACKAGE + "A"));
    
    // pass a list of templates
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    assertEquals("ABC", tc.include(TEMPLATE_PACKAGE + "A"));
  }
  
  @Test
  public void testReplaceTemplate() {
    String r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("A", r);
    
    // self-replacement
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("A", r);
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("B", r);
    
    // previously set template is overwritten
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("C", r);
    
    // pass a list of templates
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("BC", r);
  }
  
  @Test
  public void testBeforeReplaceAfterCombinations() {
    assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A"));
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("BA", tc.include(TEMPLATE_PACKAGE + "A"));
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("BAC", tc.include(TEMPLATE_PACKAGE + "A"));
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("BCC", tc.include(TEMPLATE_PACKAGE + "A"));
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("BBC", tc.include(TEMPLATE_PACKAGE + "A"));
    
    // replacing B has no effect on A
    glex.replaceTemplate(TEMPLATE_PACKAGE + "B", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("BBC", tc.include(TEMPLATE_PACKAGE + "A"));
  }
  
  
  @Test
  public void testBeforeReplaceAfterInSubtemplates() {
    assertEquals("TopA A", tc.include(TEMPLATE_PACKAGE + "TopA"));
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("TopA BA", tc.include(TEMPLATE_PACKAGE + "TopA"));
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("TopA BC", tc.include(TEMPLATE_PACKAGE + "TopA"));
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("TopA BCB", tc.include(TEMPLATE_PACKAGE + "TopA"));
  }
  
  @Test
  public void testTemplateStringHook() throws IOException{
    // define new hook point hp1
    glex.bindHookPoint("hp1", new TemplateStringHookPoint("<#if true>true</#if>"));
    String hpValue = tc.defineHookPoint("hp1");
    assertNotNull(hpValue);
    assertEquals("true", hpValue);
  }
}
