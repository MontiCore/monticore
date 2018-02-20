/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.monticore.io.FileReaderWriterMock;

/**
 * Tests for parameterized calls of the {@link TemplateController} 
 *
 * @author  Pedram Nazari
 *
 */

//TODO MB: Refactor test
@Ignore
public class TemplateControllerSignatureUsageTest {
  
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
  
  
  // =================================================
  // Tests with templates
  // =================================================
  
  @Ignore
  @Test
  public void testSignatureWithOneParameter() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithOneParameter", Lists.<Object>newArrayList("Charly"));
    
    TemplateController tcChild = tc.getSubController();
    assertNotNull(tcChild);
    
    assertEquals(1, tcChild.getArguments().size());
    assertEquals("name", tcChild.getArguments().get(0));
    
    assertEquals(1, tcChild.getArguments().size());
    assertEquals("Charly", tcChild.getArguments().get(0));
    
    assertEquals("Name is Charly", output.toString());
  }
  
  @Ignore
  @Test
  public void testSignatureWithThreeParameters() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithThreeParameters", 
        Lists.<Object>newArrayList("Charly", "30", "Aachen"));
    
    TemplateController tcChild = tc.getSubController();
    assertNotNull(tcChild);
    
    assertEquals(3, tcChild.getArguments().size());
    assertEquals("name", tcChild.getArguments().get(0));
    assertEquals("age", tcChild.getArguments().get(1));
    assertEquals("city", tcChild.getArguments().get(2));
    
    assertEquals(3, tcChild.getArguments().size());
    assertEquals("Charly", tcChild.getArguments().get(0));
    assertEquals("30", tcChild.getArguments().get(1));
    assertEquals("Aachen", tcChild.getArguments().get(2));
    
    assertEquals("Name is Charly, age is 30, city is Aachen", output.toString());
  }
  
  @Ignore
  @Test
  public void testSignatureWithManyParameters() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithManyParameters", 
        Lists.<Object>newArrayList("Charly", "30", "Aachen", "52062", "Engineer", "No friends"));
    
    TemplateController tcChild = tc.getSubController();
    assertNotNull(tcChild);
    
    assertEquals(6, tcChild.getArguments().size());
    assertEquals("name", tcChild.getArguments().get(0));
    assertEquals("age", tcChild.getArguments().get(1));
    assertEquals("city", tcChild.getArguments().get(2));
    assertEquals("zip", tcChild.getArguments().get(3));
    assertEquals("job", tcChild.getArguments().get(4));
    assertEquals("friends", tcChild.getArguments().get(5));
    
    assertEquals(6, tcChild.getArguments().size());
    assertEquals("Charly", tcChild.getArguments().get(0));
    assertEquals("30", tcChild.getArguments().get(1));
    assertEquals("Aachen", tcChild.getArguments().get(2));
    assertEquals("52062", tcChild.getArguments().get(3));
    assertEquals("Engineer", tcChild.getArguments().get(4));
    assertEquals("No friends", tcChild.getArguments().get(5));
    
    assertEquals("Name=Charly, age=30, city=Aachen, zip=52062, job=Engineer, friends=No friends", output.toString());
  }
  
  @Test
  public void testNestedSignatureCalls() {
    StringBuilder output = tc.includeArgs(TEMPLATE_PACKAGE + "NestedSignatureCalls", 
        Lists.<Object>newArrayList("T1"));
    
    assertEquals("T1 -> Name is T2", output.toString());
  }
    
  
  @Test
  public void testWrongNumberOfArguments() {
    try {
      tc.includeArgs(TEMPLATE_PACKAGE + "SignatureWithOneParameter",
          Lists.<Object>newArrayList("Charly", "tooMuch"));
      fail("Argument list is too long.");
    }
    catch(MontiCoreFreeMarkerException e) {
      assertTrue(e.getCause() instanceof IllegalArgumentException);
    }
  }
  
  @Test
  public void testArgumentsAreOnlyVisibleInIncludedTemplate() {
    StringBuilder templateOutput = tc.includeArgs(TEMPLATE_PACKAGE + "ArgumentsAreOnlyVisibleInIncludedTemplate",
        Lists.<Object>newArrayList("Charly"));
    
    assertEquals("Hello Charly\nSorry, what was your name?", templateOutput.toString());
  }
    
  @Test
  public void testParameterizedInclusionUsage() {
    StringBuilder templateOutput = tc.include(TEMPLATE_PACKAGE + "ParameterizedInclusionUsage");
    
    assertEquals(
        "Name is Charly\n" +
        "Name is Charly, age is 30, city is Aachen\n" +
        "Name=Charly, age=30, city=Aachen, zip=52062, job=Engineer, friends=No friends"
        , templateOutput.toString());
  }
}
