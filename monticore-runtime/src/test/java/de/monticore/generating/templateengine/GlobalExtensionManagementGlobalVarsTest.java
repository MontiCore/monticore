/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.FileReaderWriterMock;

/**
 * Tests for {@link GlobalExtensionManagement}.
 *
 */
public class GlobalExtensionManagementGlobalVarsTest {

  private TemplateControllerMock tc;
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
  }
  
  @Test
  public void testVariables4() {
    GeneratorSetup s = new GeneratorSetup();
    s.setTracing(false);
    GeneratorEngine ge = new GeneratorEngine(s);
    ASTNodeMock ast = new ASTNodeMock();

    // override same variable
    String res = ge.generate(TEMPLATE_PACKAGE + "TestVariables4", ast).toString();

    assertEquals("\n\nA:16\nB:38\nC:555\n", res);
  }

}
