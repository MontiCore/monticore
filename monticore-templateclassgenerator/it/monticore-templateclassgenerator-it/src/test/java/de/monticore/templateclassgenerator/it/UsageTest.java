/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator.it;

import _templates._setup.GeneratorConfig;
import _templates.templates.b.JavaClass;
import _templates.templates.maintemplates.HelloMainImpl;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorSetup;
import de.monticore.java.javadsl._ast.ASTConstructorDeclaration;
import de.monticore.java.javadsl._parser.JavaDSLParser;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.templateclassgenerator.EmptyNode;
import de.monticore.templateclassgenerator.util.GeneratorInterface;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;
import types.Attribute;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UsageTest extends AbstractSymtabTest {
  private static Path outputDirectory = Paths.get("target/generated-sources/templateClasses/");
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    symTab = createJavaSymTab(outputDirectory);
  }
  
  /**
   * Tests template classes generate to file method
   */
  @Test
  public void testJavaClassTemplateClass() {
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDirectory.toFile());
    GeneratorConfig.init(setup);
    String classname = "Test1";
    List<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute("Integer", "i"));
    attributes.add(new Attribute("String", "s"));
    Path filePath = Paths.get("test/" + classname + ".java");
    JavaClass.generate(filePath, new EmptyNode(), "test", classname,
        attributes);
    JavaTypeSymbol testClass = symTab.<JavaTypeSymbol> resolve("test.Test1", JavaTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(testClass);
    ASTNode node = new EmptyNode();
  }
  
  /**
   * Tests including Templates over their template class
   */
  @Test
  public void testToStringMethod() {
    String classname = "Test1";
    List<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute("Integer", "i"));
    attributes.add(new Attribute("String", "s"));
    String s = JavaClass.generate("test", classname, attributes);
    assertNotNull(s);
  }
  
  @Test
  public void testMainTemplate() {
    GeneratorInterface gi = new HelloMainImpl();
    gi.generate(Paths.get("Test.txt"), new EmptyNode(), new JavaTypeSymbol("ts"));
    assertTrue(Paths.get("gen"+File.separator+"Test.txt").toFile().exists());
  }
  
  private ASTConstructorDeclaration parseToASTConstructorDecl(String s) {
    JavaDSLParser parser = new JavaDSLParser();
    try {
      return parser.parse_StringConstructorDeclaration(s).get();
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
}
