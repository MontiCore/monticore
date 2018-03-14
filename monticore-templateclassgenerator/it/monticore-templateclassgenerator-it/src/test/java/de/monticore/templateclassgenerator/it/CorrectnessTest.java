/* (c)  https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;

/**
 * Tests the correctness of the generated template classes methods
 *
 * @author Jerome Pfeiffer
 */
public class CorrectnessTest extends AbstractSymtabTest {
  
  private static Path outputDirectory = Paths.get("target/generated-sources/templateClasses");
  
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
  
  /**
   * Tests completely empty template
   */
  @Test
  public void testEmptyTemplate() {
    JavaTypeSymbol emptyTemplateClass = symTab.<JavaTypeSymbol> resolve(
        "_templates.templates.a.EmptyTemplate", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(emptyTemplateClass);
    
    boolean hasCorrectGenerate = false;
    boolean hasCorrectToString = false;
    
    List<JavaMethodSymbol> methods = emptyTemplateClass.getMethods();
    assertEquals(6, methods.size());
    
    for (JavaMethodSymbol method : methods) {
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("void")) {
        assertEquals(3, method.getParameters().size());
        hasCorrectGenerate = true;
      }
      
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("String")) {
        assertEquals(1, method.getParameters().size());
        hasCorrectToString = true;
      }
      
    }
    assertTrue(hasCorrectGenerate);
    assertTrue(hasCorrectToString);
  }
  
  /**
   * Tests template with tc.params but without tc.result
   */
  @Test
  public void testTemplateWithoutResult() {
    JavaTypeSymbol templateWithoutResultClass = symTab.<JavaTypeSymbol> resolve(
        "_templates.templates.a.TemplateWithoutResult", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(templateWithoutResultClass);
    
    boolean hasCorrectGenerate = false;
    boolean hasCorrectToString = false;
    
    List<JavaMethodSymbol> methods = templateWithoutResultClass.getMethods();
    assertEquals(6, methods.size());
    ;
    for (JavaMethodSymbol method : methods) {
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("void")) {
        assertEquals(4, method.getParameters().size());
        hasCorrectGenerate = true;
      }
      
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("String")) {
        assertEquals(2, method.getParameters().size());
        hasCorrectToString = true;
      }
    }
    assertTrue(hasCorrectGenerate);
    assertTrue(hasCorrectToString);
  }
  
  /**
   * Template that does not contain a signature, but template code
   */
  @Test
  public void testTemplateWithoutSignature() {
    JavaTypeSymbol templateWithoutSignature = symTab.<JavaTypeSymbol> resolve(
        "_templates.templates.a.TemplateWithoutSignature", JavaTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(templateWithoutSignature);
    
    boolean hasCorrectGenerate = false;
    boolean hasCorrectToString = false;
    
    List<JavaMethodSymbol> methods = templateWithoutSignature.getMethods();
    assertEquals(6, methods.size());
    for (JavaMethodSymbol method : methods) {
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("void")) {
        assertEquals(3, method.getParameters().size());
        hasCorrectGenerate = true;
      }
      
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("String")) {
        assertEquals(1, method.getParameters().size());
        hasCorrectToString = true;
      }
    }
    assertTrue(hasCorrectGenerate);
    assertTrue(hasCorrectToString);
    
  }
  
  /**
   * Tests template with tc.params and tc.result
   */
  @Test
  public void testTemplateWithResult() {
    JavaTypeSymbol templateWithResult = symTab.<JavaTypeSymbol> resolve(
        "_templates.templates.a.TemplateWithResult", JavaTypeSymbol.KIND).orElse(null);
    assertNotNull(templateWithResult);
    
    boolean hasCorrectGenerate = false;
    boolean hasCorrectToString = false;
    boolean hasCorrectToResult = false;
    
    List<JavaMethodSymbol> methods = templateWithResult.getMethods();
    assertEquals(8, methods.size());
    for (JavaMethodSymbol method : methods) {
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("void")) {
        assertEquals(4, method.getParameters().size());
        hasCorrectGenerate = true;
      }
      
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("String")) {
        assertEquals(2, method.getParameters().size());
        hasCorrectToString = true;
      }
      
      if (method.getName().equals("generate") && method.getReturnType().getName().equals("Integer")) {
        assertEquals(3, method.getParameters().size());
        hasCorrectToResult = true;
      }
    }
    assertTrue(hasCorrectGenerate);
    assertTrue(hasCorrectToString);
    assertTrue(hasCorrectToResult);
  }
  
}
