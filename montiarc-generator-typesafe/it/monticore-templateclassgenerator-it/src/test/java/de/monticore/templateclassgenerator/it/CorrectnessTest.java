/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.templateclassgenerator.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class CorrectnessTest extends AbstractSymtabTest{
  
private static Path outputDirectory = Paths.get("target/generated-sources/templateClasses");
  
  private static Path modelPath = Paths.get("src/test/resources/templates/a");
  
  private static CorrectnessTest theInstance = new CorrectnessTest();
  
  // set once in doSetup
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    theInstance.doSetup();
  }
  
  private void doSetup() {
    symTab = createJavaSymTab(outputDirectory);
  }
  
  
  @Test
  public void testEmptyTemplate(){
    JavaTypeSymbol emptyTemplateClass = symTab.<JavaTypeSymbol> resolve("templates.templates.a.EmptyTemplateTemplate", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(emptyTemplateClass);
    
    boolean hasCorrectGenerate = false;
    boolean hasCorrectToString = false;
    
    List<JavaMethodSymbol> methods = emptyTemplateClass.getMethods();
    assertEquals(2, methods.size());;
    for(JavaMethodSymbol method :methods){
      if(method.getName().equals("generateToFile")){
        assertEquals(2, method.getParameters().size());
        hasCorrectGenerate = true;
      }
      
      if(method.getName().equals("generateToString")){
        assertEquals(0, method.getParameters().size());
        hasCorrectToString = true;
      }
    }
    assertTrue(hasCorrectGenerate);
    assertTrue(hasCorrectToString);
  }
  
  @Test
  public void testTemplateWithoutResult(){
    JavaTypeSymbol templateWithoutResultClass = symTab.<JavaTypeSymbol> resolve("templates.templates.a.TemplateWithoutResultTemplate", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(templateWithoutResultClass);  
    
    boolean hasCorrectGenerate = false;
    boolean hasCorrectToString = false;
    
    List<JavaMethodSymbol> methods = templateWithoutResultClass.getMethods();
    assertEquals(2, methods.size());;
    for(JavaMethodSymbol method :methods){
      if(method.getName().equals("generateToFile")){
        assertEquals(4, method.getParameters().size());
        hasCorrectGenerate = true;
      }
      
      if(method.getName().equals("generateToString")){
        assertEquals(2, method.getParameters().size());
        hasCorrectToString = true;
      }
    }
    assertTrue(hasCorrectGenerate);
    assertTrue(hasCorrectToString);
  }
  
  @Test
  public void testTemplateWithoutSignature(){
    JavaTypeSymbol templateWithoutSignature= symTab.<JavaTypeSymbol> resolve("templates.templates.a.TemplateWithoutSignatureTemplate", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(templateWithoutSignature);  
    
    boolean hasCorrectGenerate = false;
    boolean hasCorrectToString = false;
    
    List<JavaMethodSymbol> methods = templateWithoutSignature.getMethods();
    assertEquals(2, methods.size());;
    for(JavaMethodSymbol method :methods){
      if(method.getName().equals("generateToFile")){
        assertEquals(2, method.getParameters().size());
        hasCorrectGenerate = true;
      }
      
      if(method.getName().equals("generateToString")){
        assertEquals(0, method.getParameters().size());
        hasCorrectToString = true;
      }
    }
    assertTrue(hasCorrectGenerate);
    assertTrue(hasCorrectToString);
    
  }
  
  @Test
  public void testTemplateWithResult(){
    JavaTypeSymbol templateWithResult = symTab.<JavaTypeSymbol> resolve("templates.templates.a.TemplateWithResultTemplate", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(templateWithResult);  
    
    boolean hasCorrectGenerate = false;
    boolean hasCorrectToString = false;
    boolean hasCorrectToResult = false;
    
    List<JavaMethodSymbol> methods = templateWithResult.getMethods();
    assertEquals(3, methods.size());;
    for(JavaMethodSymbol method :methods){
      if(method.getName().equals("generateToFile")){
        assertEquals(4, method.getParameters().size());
        hasCorrectGenerate = true;
      }
      
      if(method.getName().equals("generateToString")){
        assertEquals(2, method.getParameters().size());
        hasCorrectToString = true;
      }
      
      if(method.getName().equals("generateToResult")){
        assertEquals(3, method.getParameters().size());
        assertEquals("Integer", method.getReturnType().getName());
        hasCorrectToResult = true;
      }
    }
    assertTrue(hasCorrectGenerate);
    assertTrue(hasCorrectToString);
    assertTrue(hasCorrectToResult);
  }
  
}
