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
import freemarker.core.Macro;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
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
  public void testInclude2Alias() {
    String content = "Content of ast";
    StringBuilder templateOutput =
        tc.include(ALIASES_PACKAGE + "Include2Alias", new AliasTestASTNodeMock(content));
    assertEquals(content, templateOutput.toString());
  }

  @Test
  public void testIncludeArgs(){
   // TODO: Test all aliases
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
