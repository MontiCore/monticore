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

import com.google.common.collect.Lists;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.monticore.io.FileReaderWriterMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests for parameterized calls of the {@link TemplateController} 
 *
 */

public class TemplateControllerSignatureUsageTest {

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
  }

  @AfterAll
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }


  // =================================================
  // Tests with templates
  // =================================================

  @Test
  public void testSignatureWithOneParameter() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithOneParameter", Lists.<Object>newArrayList("Charly"));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Assertions.assertEquals("Name is Charly", output.toString());
  }

  @Test
  public void testSignatureWithThreeParameters() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithThreeParameters",
        Lists.<Object>newArrayList("Charly", "30", "Aachen"));

    Assertions.assertEquals("Name is Charly, age is 30, city is Aachen", output.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSignatureWithManyParameters() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithManyParameters",
        Lists.<Object>newArrayList("Charly", "30", "Aachen", "52062", "Engineer", "No friends"));

    Assertions.assertEquals("Name=Charly, age=30, city=Aachen, zip=52062, job=Engineer, friends=No friends", output.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNestedSignatureCalls() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "NestedSignatureCalls",
        Lists.<Object>newArrayList("T1"));

    Assertions.assertEquals("T1 -> Name is T2", output.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testWrongNumberOfArguments() {
    try {
      tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithOneParameter",
          Lists.<Object>newArrayList("Charly", "tooMuch"));
      Assertions.fail("Argument list is too long.");
    } catch (MontiCoreFreeMarkerException e) {
      Assertions.assertTrue(e.getCause() instanceof IllegalArgumentException);
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Disabled
  @Test
  public void testArgumentsAreOnlyVisibleInIncludedTemplate() {
    StringBuilder templateOutput = tc.includeArgs(TEMPLATE_PACKAGE + "ArgumentsAreOnlyVisibleInIncludedTemplate",
        Lists.<Object>newArrayList("Charly"));

    Assertions.assertEquals("Hello Charly\nSorry, what was your name?", templateOutput.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
    
  @Test
  public void testParameterizedInclusionUsage() {
    StringBuilder templateOutput = tc.include(TEMPLATE_PACKAGE + "ParameterizedInclusionUsage");
    
    Assertions.assertEquals("Name is Charly\n" +
    "Name is Charly, age is 30, city is Aachen\n" +
    "Name=Charly, age=30, city=Aachen, zip=52062, job=Engineer, friends=No friends", templateOutput.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}