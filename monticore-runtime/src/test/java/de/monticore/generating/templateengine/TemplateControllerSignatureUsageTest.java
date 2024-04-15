/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.*;

import com.google.common.collect.Lists;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.monticore.io.FileReaderWriterMock;

/**
 * Tests for parameterized calls of the {@link TemplateController} 
 *
 */

public class TemplateControllerSignatureUsageTest {

  private TemplateControllerMock tc;
  private GlobalExtensionManagement glex;
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

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

  @AfterClass
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }


  // =================================================
  // Tests with templates
  // =================================================

  @Test
  public void testSignatureWithOneParameter() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithOneParameter", Lists.<Object>newArrayList("Charly"));
  
    assertTrue(Log.getFindings().isEmpty());
    assertEquals("Name is Charly", output.toString());
  }

  @Test
  public void testSignatureWithThreeParameters() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithThreeParameters",
        Lists.<Object>newArrayList("Charly", "30", "Aachen"));

    assertEquals("Name is Charly, age is 30, city is Aachen", output.toString());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSignatureWithManyParameters() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithManyParameters",
        Lists.<Object>newArrayList("Charly", "30", "Aachen", "52062", "Engineer", "No friends"));

    assertEquals("Name=Charly, age=30, city=Aachen, zip=52062, job=Engineer, friends=No friends", output.toString());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNestedSignatureCalls() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "NestedSignatureCalls",
        Lists.<Object>newArrayList("T1"));

    assertEquals("T1 -> Name is T2", output.toString());
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testWrongNumberOfArguments() {
    try {
      tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithOneParameter",
          Lists.<Object>newArrayList("Charly", "tooMuch"));
      fail("Argument list is too long.");
    } catch (MontiCoreFreeMarkerException e) {
      assertTrue(e.getCause() instanceof IllegalArgumentException);
    }
    assertTrue(Log.getFindings().isEmpty());
  }

  @Ignore
  @Test
  public void testArgumentsAreOnlyVisibleInIncludedTemplate() {
    StringBuilder templateOutput = tc.includeArgs(TEMPLATE_PACKAGE + "ArgumentsAreOnlyVisibleInIncludedTemplate",
        Lists.<Object>newArrayList("Charly"));

    assertEquals("Hello Charly\nSorry, what was your name?", templateOutput.toString());
    assertTrue(Log.getFindings().isEmpty());
  }
    
  @Test
  @Ignore
  public void testParameterizedInclusionUsage() {
    StringBuilder templateOutput = tc.include(TEMPLATE_PACKAGE + "ParameterizedInclusionUsage");
    
    assertEquals(
        "Name is Charly\n" +
        "Name is Charly, age is 30, city is Aachen\n" +
        "Name=Charly, age=30, city=Aachen, zip=52062, job=Engineer, friends=No friends"
        , templateOutput.toString());
    assertTrue(Log.getFindings().isEmpty());
  }
}