/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.templateclassgenerator.it;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import templates.b.ConstructorTemplate;
import templates.b.JavaClassTemplate;
import types.Attribute;
import types.Helper;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.MyGeneratorEngine;
import de.monticore.java.javadsl._ast.ASTConstructorDeclaration;
import de.monticore.java.javadsl._parser.JavaDSLParser;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.templateclassgenerator.EmptyNode;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class UsageTest extends AbstractSymtabTest {
  private static Path outputDirectory = Paths.get("target/generated-sources/templateClasses");
  
  private static Scope symTab = null;
  
  @BeforeClass
  public static void setup() {
    symTab = createJavaSymTab(outputDirectory);
  }
  
  @Test
  public void testJavaClassTemplateClass() {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory.toFile());
    MyGeneratorEngine generator = new MyGeneratorEngine(setup);
    String classname = "Test1";
    List<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute("Integer", "i"));
    attributes.add(new Attribute("String", "s"));
    Path filePath = Paths.get("test/" + classname + ".java");
    JavaClassTemplate.generateToFile(generator, filePath, new EmptyNode(), "test", classname,
        attributes);
    JavaTypeSymbol testClass = symTab.<JavaTypeSymbol> resolve("test.Test1", JavaTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(testClass);
  }
  
  @Test
  public void testReturnMethod() throws RecognitionException, IOException {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory.toFile());
    MyGeneratorEngine generator = new MyGeneratorEngine(setup);
    List<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute("Integer", "i"));
    attributes.add(new Attribute("String", "s"));
    Function<String, ASTConstructorDeclaration> function = (String s) -> parseToASTMethodDecl(s);
    ASTConstructorDeclaration meth = ConstructorTemplate.generateToResult(generator, new EmptyNode(), "Test2", attributes, new Helper(), function);
    assertNotNull(meth);
  }
  
  @Test
  public void testToStringMethod() {
    final GeneratorSetup setup = new GeneratorSetup(outputDirectory.toFile());
    MyGeneratorEngine generator = new MyGeneratorEngine(setup);
    String classname = "Test1";
    List<Attribute> attributes = new ArrayList<>();
    attributes.add(new Attribute("Integer", "i"));
    attributes.add(new Attribute("String", "s"));
    String s = JavaClassTemplate.generateToString(generator, new EmptyNode(), "test", classname, attributes);
    assertNotNull(s);
  }
  
  private ASTConstructorDeclaration parseToASTMethodDecl(String s) {
    JavaDSLParser parser = new JavaDSLParser();
    try {
      return parser.parseString_ConstructorDeclaration(s).get();
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    return null;
    
  }
}
