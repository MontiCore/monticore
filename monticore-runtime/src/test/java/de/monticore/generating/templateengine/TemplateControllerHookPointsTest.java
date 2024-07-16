/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import de.monticore.ast.ASTNode;
import de.monticore.ast.ASTNodeMock;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.monticore.generating.GeneratorSetup;
import de.monticore.io.FileReaderWriterMock;

/**
 * Tests hook point methods of {@link TemplateController}
 *
 *
 */
public class TemplateControllerHookPointsTest {
  
  private TemplateController tc;
  private GlobalExtensionManagement glex;
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setup() {
    glex = new GlobalExtensionManagement();
        
    GeneratorSetup config = new GeneratorSetup();
    config.setGlex(glex);
    config.setFileHandler(new FileReaderWriterMock());
    config.setOutputDirectory(new File("dummy"));
    config.setTracing(false);
    tc = new TemplateControllerMock(config, "");
  }

  @AfterAll
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }
  
  @Test
  public void testUndefinedHook() {
    tc.getGeneratorSetup().setTracing(true);
    Assertions.assertEquals("/* Hookpoint: hp1 */", glex.defineHookPoint(tc, "hp1"));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testDefaultHook() {
    tc.getGeneratorSetup().setTracing(false);
    String hpValue;
    hpValue = glex.defineHookPointWithDefault(tc, "hp1", "default");
    Assertions.assertEquals("default", hpValue);
    glex.bindHookPoint("hp1", new StringHookPoint("value of hp1"));
    hpValue = glex.defineHookPointWithDefault(tc, "hp1", "default");
    Assertions.assertEquals("value of hp1", hpValue);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testSetStringHook() {
    String hpValue = null;
    
    
    // define new hook point hp1
    glex.bindHookPoint("hp1", new StringHookPoint("value of hp1"));
    hpValue = glex.defineHookPoint(tc, "hp1");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("value of hp1", hpValue);
    
    // overwrite value of hook point hp1
    glex.bindHookPoint("hp1", new StringHookPoint("new value of hp1"));
    hpValue = glex.defineHookPoint(tc, "hp1");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("new value of hp1", hpValue);
    
    // define new hook point hp2
    glex.bindHookPoint("hp2", new StringHookPoint("value of hp2"));
    hpValue = glex.defineHookPoint(tc, "hp2");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("value of hp2", hpValue);
    // hp1 still exists
    Assertions.assertEquals("new value of hp1", glex.defineHookPoint(tc, "hp1"));
  }
  
  
  @Test
  public void testSetTemplateHook() {
    String hpValue = null;
    
    // define new hook point hp1
    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "HelloWorld"));
    hpValue = glex.defineHookPoint(tc, "hp1");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("Hello World!", hpValue);
    
    // overwrite value of hook point hp1
    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "HowAreYou"));
    hpValue = glex.defineHookPoint(tc, "hp1");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("How Are You?", hpValue);
    
    // define new hook point hp2
    glex.bindHookPoint("hp2", new TemplateHookPoint(TEMPLATE_PACKAGE + "HelloWorld"));
    hpValue = glex.defineHookPoint(tc, "hp2");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("Hello World!", hpValue);
    // hp1 still exists
    Assertions.assertEquals("How Are You?", glex.defineHookPoint(tc, "hp1"));
  }
  
  @Test
  public void testSetCodeHook() {
    String hpValue = null;
    
    // define new hook point hp1
    CodeHookPointMock command = new CodeHookPointMock("command1");
    glex.bindHookPoint("hp1", command);
    hpValue = glex.defineHookPoint(tc, "hp1");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("command1", hpValue);
    
    // overwrite value of hook point hp1
    command = new CodeHookPointMock("command2");
    glex.bindHookPoint("hp1", command);
    hpValue = glex.defineHookPoint(tc, "hp1");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("command2", hpValue);
    
    // overwrite value of hook point hp1
    command = new CodeHookPointMock("command3");
    glex.bindHookPoint("hp2", command);
    hpValue = glex.defineHookPoint(tc, "hp2");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("command3", hpValue);
    // hp1 still exists
    Assertions.assertEquals("command2", glex.defineHookPoint(tc, "hp1"));
  }
  
  @Test
  public void testStringTemplateCodeHookCombinations() {
    final String hp = "hp";
    
    glex.bindHookPoint(hp, new StringHookPoint("StringHook"));
    Assertions.assertEquals("StringHook", glex.defineHookPoint(tc, hp));
    
    glex.bindHookPoint(hp, new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    Assertions.assertEquals("A", glex.defineHookPoint(tc, hp));
    
    CodeHookPointMock command = new CodeHookPointMock("command");
    glex.bindHookPoint(hp, command);
    Assertions.assertEquals("command", glex.defineHookPoint(tc, hp));
    
    glex.bindHookPoint(hp, new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    Assertions.assertEquals("A", glex.defineHookPoint(tc, hp));
    
    glex.bindHookPoint(hp, new StringHookPoint("StringHook"));
    Assertions.assertEquals("StringHook", glex.defineHookPoint(tc, hp));
  }
  
  @Test
  public void testStringHookInSubtemplate() {
    Assertions.assertEquals("TopStringHook Hello Brave New World!", tc.include(TEMPLATE_PACKAGE + "TopStringHook").toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testTemplateHookInSubtemplate() {
    Assertions.assertEquals("TopTemplateHook A", tc.include(TEMPLATE_PACKAGE + "TopTemplateHook").toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBeforeTemplates() {
    Assertions.assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("BA", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // previously set template is overwritten
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("CA", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // pass a list of templates
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    Assertions.assertEquals("BCA", tc.include(TEMPLATE_PACKAGE + "A").toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSpecificBeforeTemplates() {
    ASTNode ast1 = new ASTNodeMock();
    ASTNode ast2 = new ASTNodeMock();

    Assertions.assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A", ast1).toString());

    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", ast1, new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("BA", tc.include(TEMPLATE_PACKAGE + "A", ast1).toString());
    Assertions.assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A", ast2).toString());

    // previously set template is overwritten
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", ast1, new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("CA", tc.include(TEMPLATE_PACKAGE + "A", ast1).toString());
    Assertions.assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A", ast2).toString());

    // add a new template
    glex.addBeforeTemplate(TEMPLATE_PACKAGE + "A", ast1, new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("CBA", tc.include(TEMPLATE_PACKAGE + "A", ast1).toString());
    Assertions.assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A", ast2).toString());

  }

  @Test
  public void testAfterTemplates() {
    Assertions.assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("AB", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // previously set template is overwritten
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("AC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // pass a list of templates
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    Assertions.assertEquals("ABC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddAfterTemplates() {
    Assertions.assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A").toString());

    glex.addAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("AB", tc.include(TEMPLATE_PACKAGE + "A").toString());

    // previously set template is not overwritten
    glex.addAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("ABC", tc.include(TEMPLATE_PACKAGE + "A").toString());
  }
  
  @Test
  public void testReplaceTemplate() {
    StringBuilder r = tc.include(TEMPLATE_PACKAGE + "A");
    Assertions.assertEquals("A", r.toString());
    
    // self-replacement
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "A"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    Assertions.assertEquals("A", r.toString());
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    Assertions.assertEquals("B", r.toString());
    
    // previously set template is overwritten
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    Assertions.assertEquals("C", r.toString());
    
    // pass a list of templates
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", Arrays.asList(
        new TemplateHookPoint(TEMPLATE_PACKAGE + "B"), 
        new TemplateHookPoint(TEMPLATE_PACKAGE + "C")));
    r = tc.include(TEMPLATE_PACKAGE + "A");
    Assertions.assertEquals("BC", r.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBeforeReplaceAfterCombinations() {
    Assertions.assertEquals("A", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("BA", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("BAC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("BCC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("BBC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    
    // replacing B has no effect on A
    glex.replaceTemplate(TEMPLATE_PACKAGE + "B", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("BBC", tc.include(TEMPLATE_PACKAGE + "A").toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  
  @Test
  public void testBeforeReplaceAfterInSubtemplates() {
    Assertions.assertEquals("TopA A", tc.include(TEMPLATE_PACKAGE + "TopA").toString());
    
    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("TopA BA", tc.include(TEMPLATE_PACKAGE + "TopA").toString());
    
    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("TopA BC", tc.include(TEMPLATE_PACKAGE + "TopA").toString());
    
    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("TopA BCB", tc.include(TEMPLATE_PACKAGE + "TopA").toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSpecificBeforeReplaceAfterInSubtemplates() {
    ASTNode ast1 = new ASTNodeMock();

    Assertions.assertEquals("TopA A", tc.include(TEMPLATE_PACKAGE + "TopA").toString());

    glex.setBeforeTemplate(TEMPLATE_PACKAGE + "A", ast1, new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("TopA BA", tc.include(TEMPLATE_PACKAGE + "TopA", ast1).toString());

    glex.replaceTemplate(TEMPLATE_PACKAGE + "A", ast1, new TemplateHookPoint(TEMPLATE_PACKAGE + "C"));
    Assertions.assertEquals("TopA BC", tc.include(TEMPLATE_PACKAGE + "TopA", ast1).toString());

    glex.setAfterTemplate(TEMPLATE_PACKAGE + "A", ast1, new TemplateHookPoint(TEMPLATE_PACKAGE + "B"));
    Assertions.assertEquals("TopA BCB", tc.include(TEMPLATE_PACKAGE + "TopA", ast1).toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testTemplateStringHook() throws IOException{
    // define new hook point hp1
    glex.bindHookPoint("hp1", new TemplateStringHookPoint("<#if true>true</#if>"));
    String hpValue = glex.defineHookPoint(tc, "hp1");
    Assertions.assertNotNull(hpValue);
    Assertions.assertEquals("true", hpValue);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefineHookPointWithArgs() {
    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "SignatureWithOneParameter"));
    String hpValue = glex.defineHookPoint(tc, "hp1", "A");
    Assertions.assertEquals("Name is A", hpValue);

    glex.bindHookPoint("hp1", new TemplateHookPoint(TEMPLATE_PACKAGE + "SignatureWithThreeParameters"));
    hpValue = glex.defineHookPoint(tc, "hp1", "B", 42, "LA");
    Assertions.assertEquals("Name is B, age is 42, city is LA", hpValue);
  }
}
