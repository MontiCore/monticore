/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import com.google.common.base.Joiner;
import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.alias.Alias;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.FileReaderWriterMock;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TemplateAliasingTest {

  private static final File TARGET_DIR = new File("target");
  private static final int NUMBER_ALIASES = 20;
  public static final String ALIASES_PACKAGE = "de.monticore.generating.templateengine.templates.aliases.";


  private TemplateController tc;
  
  private GeneratorSetup config;


  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setup() {
    FileReaderWriterMock fileHandler = new FileReaderWriterMock();
    FileReaderWriter.init(fileHandler);

    config = new GeneratorSetup();
    config.setOutputDirectory(TARGET_DIR);
    config.setTracing(false);
    
    tc = new TemplateController(config, "");

    LogStub.getPrints().clear();
  }

  @AfterClass
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }

  @Test
  public void testIncludeAlias() {
    StringBuilder templateOutput =
        tc.include(ALIASES_PACKAGE + "IncludeAlias");
    assertEquals("Plain is included.", templateOutput.toString());
  }

  @Test
  public void testIncludeDispatching(){
    StringBuilder templateOutput =
        tc.include(ALIASES_PACKAGE + "IncludeDispatching");

    assertEquals(
        "String argument\n" +
        "Plain is included.\n" +
        "Plain is included.\n" +
        "\n" +
        "List argument\n" +
        "Plain is included.Plain is included.\n" +
        "Plain is included.Plain is included.", templateOutput.toString());
  }

  @Test
  public void testInclude2Alias() {
    String content = "Content of ast";
    StringBuilder templateOutput =
        tc.include(ALIASES_PACKAGE + "Include2Alias", new AliasTestASTNodeMock(content));
    assertEquals(content, templateOutput.toString());
  }

  @Test
  public void testIncludeArgsAndSignatureAlias(){
    StringBuilder templateOut =
        tc.include(ALIASES_PACKAGE + "IncludeArgsAndSignatureAlias");

    assertEquals("Name is Charly, age is 30, city is Aachen", templateOut.toString());
  }

  @Test
  public void testSignature(){
    StringBuilder templateOut =
        tc.includeArgs(ALIASES_PACKAGE + "SignatureAliasWithThreeParameters", "Max Mustermann", "45", "Berlin");

    assertEquals("Name is Max Mustermann, age is 45, city is Berlin", templateOut.toString());
  }

  @Test
  public void testSimpleDefineHookPoint() throws IOException {
    AliasTestASTNodeMock ast = new AliasTestASTNodeMock("c1");
    AliasTestASTNodeMock alternativeAst = new AliasTestASTNodeMock("c2");

    String templateName = ALIASES_PACKAGE + "DefineHookPointAlias";
    StringBuilder templateOut =
        tc.includeArgs(templateName, ast, Collections.singletonList(alternativeAst));

    assertEquals("/* Hookpoint: WithoutAst */\n" +
        "/* Hookpoint: WithAst */\n" +
        "/* Hookpoint: WithAlternativeAst */", templateOut.toString());

    GlobalExtensionManagement glex = tc.getGeneratorSetup().getGlex();

    glex.bindHookPoint("WithoutAst", new StringHookPoint("a1"));
    templateOut =
        tc.includeArgs(templateName, ast, Collections.singletonList(alternativeAst));
    assertEquals("a1\n" +
        "/* Hookpoint: WithAst */\n" +
        "/* Hookpoint: WithAlternativeAst */", templateOut.toString());


    glex.bindHookPoint("WithAst", new TemplateStringHookPoint("${ast.content}"));
    templateOut =
        tc.includeArgs(templateName, ast, Collections.singletonList(alternativeAst));
    assertEquals("a1\n" +
        "c1\n" +
        "/* Hookpoint: WithAlternativeAst */", templateOut.toString());

    glex.bindHookPoint("WithAlternativeAst", new TemplateStringHookPoint("${ast.content}"));
    templateOut =
        tc.includeArgs(templateName, ast, Collections.singletonList(alternativeAst));
    assertEquals("a1\n" +
        "c1\n" +
        "c2", templateOut.toString());
  }

  @Test
  public void testDefineHookPointWithDefaultAlias() throws IOException {
    AliasTestASTNodeMock ast = new AliasTestASTNodeMock("c1");
    AliasTestASTNodeMock alternativeAst = new AliasTestASTNodeMock("c2");

    String templateName = ALIASES_PACKAGE + "DefineHookPointWithDefaultAlias";
    StringBuilder templateOut =
        tc.includeArgs(templateName, ast, Collections.singletonList(alternativeAst));

    assertEquals("default text 1\n" +
        "default text 2\n" +
        "default text 3", templateOut.toString());

    GlobalExtensionManagement glex = tc.getGeneratorSetup().getGlex();

    glex.bindHookPoint("WithoutAst", new StringHookPoint("a1"));
    templateOut =
        tc.includeArgs(templateName, ast, Collections.singletonList(alternativeAst));
    assertEquals("a1\n" +
        "default text 2\n" +
        "default text 3", templateOut.toString());


    glex.bindHookPoint("WithAst", new TemplateStringHookPoint("${ast.content}"));
    templateOut =
        tc.includeArgs(templateName, ast, Collections.singletonList(alternativeAst));
    assertEquals("a1\n" +
        "c1\n" +
        "default text 3", templateOut.toString());

    glex.bindHookPoint("WithAlternativeAst", new TemplateStringHookPoint("${ast.content}"));
    templateOut =
        tc.includeArgs(templateName, ast, Collections.singletonList(alternativeAst));
    assertEquals("a1\n" +
        "c1\n" +
        "c2", templateOut.toString());
  }

  @Test
  public void testBindHookPointAlias(){
    AliasTestASTNodeMock ast = new AliasTestASTNodeMock("c1");
    AliasTestASTNodeMock alternativeAst = new AliasTestASTNodeMock("c2");

    String templateName = ALIASES_PACKAGE + "BindHookPointAlias";

    StringBuilder templateOut =
        tc.includeArgs(templateName, ast, Arrays.asList(alternativeAst));

    assertEquals("bound\n" +
        "/* Hookpoint: WithAst */\n" +
        "/* Hookpoint: WithAlternativeAst */", templateOut.toString());
  }

  @Test
  public void testRequireGlobalVarsValid() {
    GlobalExtensionManagement glex = tc.getGeneratorSetup().getGlex();
    try {
      glex.setGlobalValue("a", "a");
      glex.setGlobalValue("b", "b");
      glex.setGlobalValue("c", "c");
      StringBuilder templateOut =
          tc.include(ALIASES_PACKAGE + "RequireGlobalVarsAlias");
    }finally {
      glex.getGlobalData().remove("a");
      glex.getGlobalData().remove("b");
      glex.getGlobalData().remove("c");
    }

    assertTrue("Log not empty!\n" + LogStub.getPrints(), LogStub.getPrints().isEmpty());
  }

  @Test
  public void testRequireGlobalVarsInvalid() {
    StringBuilder templateOut =
        tc.include(ALIASES_PACKAGE + "RequireGlobalVarsAlias");

    assertFalse("Log empty, expected error", LogStub.getPrints().isEmpty());
  }

  @Test
  public void testLogAliases() {
    assertTrue(config.getAliases().isEmpty());
    tc.include(ALIASES_PACKAGE + "LogAliases");
    assertAliases(tc, NUMBER_ALIASES);

    Collection<String> expectedLogs = Arrays.asList(
        "Info Message",
        "Warn Message",
        "Error Message"
        );

    assertEquals(3, LogStub.getPrints().size());
    assertErrors(expectedLogs, LogStub.getPrints());
  }


  @Test
  public void testExistsHookPoint(){
    StringBuilder templateOut =
        tc.include(ALIASES_PACKAGE + "ExistsHookPointAlias");
    assertEquals("false\nfalse", templateOut.toString());

    tc.config.getGlex().bindHookPoint("hp1", new StringHookPoint("a"));
    templateOut =
        tc.include(ALIASES_PACKAGE + "ExistsHookPointAlias");
    assertEquals("true\nfalse", templateOut.toString());

    tc.config.getGlex().bindHookPoint("hp2", new StringHookPoint("a"));
    templateOut =
        tc.include(ALIASES_PACKAGE + "ExistsHookPointAlias");
    assertEquals("true\ntrue", templateOut.toString());
  }

  /**
   * Asserts that each of the expectedErrors is found at least once in the
   * actualErrors.
   *
   * @param expectedErrors
   * @param actualErrors
   */
  private static void assertErrors(Collection<String> expectedErrors,
      Collection<String> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    for (String expectedError : expectedErrors) {
      boolean found = actualErrors.stream().filter(s -> s.contains(expectedError)).count() >= 1;
      assertTrue("The following expected error was not found: " + expectedError
          + actualErrorsJoined, found);
    }
  }

  private void assertAliases(TemplateController tc, int expectedNumberAliases) {
    List<Alias> aliases = config.getAliases();
    assertNotNull(aliases);
    assertEquals(expectedNumberAliases, aliases.size());
  }

  public static class AliasTestASTNodeMock extends ASTCNode {
    private final String content;

    public AliasTestASTNodeMock(String content) {
      this.content = content;
    }

    public String getContent(){
      return content;
    }

    @Override
    public IScope getEnclosingScope() {
      return null;
    }

    @Override
    public ASTNode deepClone() {
      return null;
    }
  }

}
