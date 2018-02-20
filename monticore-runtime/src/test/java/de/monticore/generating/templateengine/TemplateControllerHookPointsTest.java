/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.monticore.generating.GeneratorSetup;
import de.monticore.io.FileReaderWriterMock;

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
        
    GeneratorSetup config = new GeneratorSetup();
    config.setGlex(glex);
    config.setFileHandler(new FileReaderWriterMock());
    config.setOutputDirectory(new File("dummy"));
    config.setTracing(false);
    tc = new TemplateControllerMock(config, "");
  }
  
  @Test
  public void testUndefinedHookReturnsEmptyString() {
    assertEquals("", glex.defineHookPoint(tc, "hp1"));
  }
  
  @Test
  public void testSetStringHook() {
    String hpValue = null;
    
    
    // define new hook point hp1
    glex.bindHookPoint("hp1", new StringHookPoint("value of hp1"));
    hpValue = glex.defineHookPoint(tc, "hp1");
    assertNotNull(hpValue);
    assertEquals("value of hp1", hpValue);
    
    // overwrite value of hook point hp1
    glex.bindHookPoint("hp1", new StringHookPoint("new value of hp1"));
    hpValue = glex.defineHookPoint(tc, "hp1");
    assertNotNull(hpValue);
    assertEquals("new value of hp1", hpValue);
    
    // define new hook point hp2
    glex.bindHookPoint("hp2", new StringHookPoint("value of hp2"));
    hpValue = glex.defineHookPoint(tc, "hp2");
    assertNotNull(hpValue);
    assertEquals("value of hp2", hpValue);
    // hp1 still exists
    assertEquals("new value of hp1", glex.defineHookPoint(tc, "hp1"));
  }
  
  
  @Test
  public void testSetTemplateHook() {
    String hpValue = null;
    
    // define new hook point hp1
    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "HelloWorld"));
    hpValue = glex.defineHookPoint(tc, "hp1");
    assertNotNull(hpValue);
    assertEquals("Hello World!", hpValue);
    
    // overwrite value of hook point hp1
    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "HowAreYou"));
    hpValue = glex.defineHookPoint(tc, "hp1");
    assertNotNull(hpValue);
    assertEquals("How Are You?", hpValue);
    
    // define new hook point hp2
    glex.bindHookPoint("hp2", new TemplateHookPoint(TEMPLATE_PACKAGE + "HelloWorld"));
    hpValue = glex.defineHookPoint(tc, "hp2");
    assertNotNull(hpValue);
    assertEquals("Hello World!", hpValue);
    // hp1 still exists
    assertEquals("How Are You?", glex.defineHookPoint(tc, "hp1"));
  }
  
  @Test
  public void testSetCodeHook() {
    String hpValue = null;
    
    // define new hook point hp1
    CodeHookPointMock command = new CodeHookPointMock("command1");
    glex.bindHookPoint("hp1", command);
    hpValue = glex.defineHookPoint(tc, "hp1");
    assertNotNull(hpValue);
    assertEquals("command1", hpValue);
    
    // overwrite value of hook point hp1
    command = new CodeHookPointMock("command2");
    glex.bindHookPoint("hp1", command);
    hpValue = glex.defineHookPoint(tc, "hp1");
    assertNotNull(hpValue);
    assertEquals("command2", hpValue);
    
    // overwrite value of hook point hp1
    command = new CodeHookPointMock("command3");
    glex.bindHookPoint("hp2", command);
    hpValue = glex.defineHookPoint(tc, "hp2");
    assertNotNull(hpValue);
    assertEquals("command3", hpValue);
    // hp1 still exists
    assertEquals("command2", glex.defineHookPoint(tc, "hp1"));
  }
  
  @Test
  public void testStringTemplateCodeHookCombinations() {
    final String hp = "hp";
    
    glex.bindHookPoint(hp, new StringHookPoint("StringHook"));
    assertEquals("StringHook", glex.defineHookPoint(tc, hp));
    
    glex.bindHookPoint(hp, new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    assertEquals("A", glex.defineHookPoint(tc, hp));
    
    CodeHookPointMock command = new CodeHookPointMock("command");
    glex.bindHookPoint(hp, command);
    assertEquals("command", glex.defineHookPoint(tc, hp));
    
    glex.bindHookPoint(hp, new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    assertEquals("A", glex.defineHookPoint(tc, hp));
    
    glex.bindHookPoint(hp, new StringHookPoint("StringHook"));
    assertEquals("StringHook", glex.defineHookPoint(tc, hp));
  }
  
  @Test
  public void testStringHookInSubtemplate() {
    assertEquals("TopStringHook Hello Brave New World!", tc.include(TEMPLATE_PACKAGE + "TopStringHook").toString());
  }
  
  @Test
  public void testTemplateHookInSubtemplate() {
    assertEquals("TopTemplateHook A", tc.include(TEMPLATE_PACKAGE + "TopTemplateHook").toString());
  }
  
  @Test
  public void testBeforeTemplates() {
    assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("BA", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // previously set template is overwritten
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("CA", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // pass a list of templates
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    assertEquals("BCA", tc.include(TEMPLATE_PACKAGE + "A").toString());
  }

  @Test
  public void testAfterTemplates() {
    assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("AB", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // previously set template is overwritten
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("AC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // pass a list of templates
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    assertEquals("ABC", tc.include(TEMPLATE_PACKAGE + "A").toString());
  }
  
  @Test
  public void testReplaceTemplate() {
    StringBuilder r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("A", r.toString());
    
    // self-replacement
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("A", r.toString());
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("B", r.toString());
    
    // previously set template is overwritten
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("C", r.toString());
    
    // pass a list of templates
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    assertEquals("BC", r.toString());
  }
  
  @Test
  public void testBeforeReplaceAfterCombinations() {
    assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("BA", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("BAC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("BCC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("BBC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // replacing B has no effect on A
    glex.replaceTemplate(TEMPLATE_PACKAGE + "B", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("BBC", tc.include(TEMPLATE_PACKAGE + "A").toString());
  }
  
  
  @Test
  public void testBeforeReplaceAfterInSubtemplates() {
    assertEquals("TopA A", tc.include(TEMPLATE_PACKAGE + "TopA").toString());
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("TopA BA", tc.include(TEMPLATE_PACKAGE + "TopA").toString());
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    assertEquals("TopA BC", tc.include(TEMPLATE_PACKAGE + "TopA").toString());
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    assertEquals("TopA BCB", tc.include(TEMPLATE_PACKAGE + "TopA").toString());
  }
  
  @Test
  public void testTemplateStringHook() throws IOException{
    // define new hook point hp1
    glex.bindHookPoint("hp1", new TemplateStringHookPoint("<#if true>true</#if>"));
    String hpValue = glex.defineHookPoint(tc, "hp1");
    assertNotNull(hpValue);
    assertEquals("true", hpValue);
  }

  @Test
  public void testDefineHookPointWithArgs() {
    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "SignatureWithOneParameter"));
    String hpValue = glex.defineHookPoint(tc, "hp1", "A");
    assertEquals("Name is A", hpValue);

    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "SignatureWithThreeParameters"));
    hpValue = glex.defineHookPoint(tc, "hp1", "B", 42, "LA");
    assertEquals("Name is B, age is 42, city is LA", hpValue);
  }
}
