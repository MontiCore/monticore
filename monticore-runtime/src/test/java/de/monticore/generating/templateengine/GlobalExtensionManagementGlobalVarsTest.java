/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.FileReaderWriterMock;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link GlobalExtensionManagement}.
 *
 */
public class GlobalExtensionManagementGlobalVarsTest {

  private TemplateControllerMock tc;
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
    assertTrue(Log.getFindings().isEmpty());
  }

  @AfterAll
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }

  @Test
  public void testGlobalVars() {
    glex.defineGlobalVar("test", "test");
    glex.defineGlobalVar("asd", new String("asd"));

    StringBuilder output = tc.include(TEMPLATE_PACKAGE + "GlobalVars");
    assertEquals("testasd", output.toString().replaceAll("\\s+", ""));

    glex.changeGlobalVar("asd", new String("aaa"));
    output = tc.include(TEMPLATE_PACKAGE + "GlobalVars");
    assertEquals("testaaa", output.toString().replaceAll("\\s+", ""));

    glex.defineGlobalVar("liste", new ArrayList<>());
    glex.addToGlobalVar("liste", new String("a"));
    glex.addToGlobalVar("liste", new String("b"));
    glex.addToGlobalVar("liste", new String("c"));
    output = tc.include(TEMPLATE_PACKAGE + "GlobalListVars");
    assertEquals("abc", output.toString().replaceAll("\\s+", ""));
    assertTrue(Log.getFindings().isEmpty());
  }
  

  @Test
  public void testVariables4() {
    GeneratorSetup s = new GeneratorSetup();
    s.setTracing(false);
    GeneratorEngine ge = new GeneratorEngine(s);
    ASTNodeMock ast = new ASTNodeMock();

    // override same variable
    String res = ge.generate(TEMPLATE_PACKAGE + "TestVariables4", ast).toString();

    assertEquals("A:16B:38C:555", res.replaceAll("\\r\\n|\\r|\\n", ""));
    assertTrue(Log.getFindings().isEmpty());
  }

}
