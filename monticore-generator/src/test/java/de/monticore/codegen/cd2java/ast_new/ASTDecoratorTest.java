package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private CDTypeFactory cdTypeFactory = CDTypeFactory.getInstance();

  private ASTCDClass astClass;

  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.loadModel("de", "monticore", "codegen", "ast", "AST");
    ASTDecorator decorator = new ASTDecorator(this.glex, ast);
    ASTCDClass clazz = getClassBy("A", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClassName() {
    assertEquals("A", astClass.getName());
  }

  @Test
  public void testEmptyAttributes() {
    assertEquals(0, astClass.getCDAttributeList().size());
  }

  @Test
  public void testEmptyConstructors() {
    assertEquals(0, astClass.getCDConstructorList().size());
  }

  @Test
  public void testMethods() {
    assertFalse(astClass.getCDMethodList().isEmpty());
    assertEquals(3, astClass.getCDMethodList().size());
  }

  @Test
  public void testAcceptMethod() {
    List<ASTCDMethod> methods = getMethodsBy("accept", 1, astClass);
    ASTType visitorType = this.cdTypeFactory.createSimpleReferenceType("de.monticore.codegen.ast.ast._visitor.ASTVisitor");

    methods = methods.stream().filter(m -> visitorType.deepEquals(m.getCDParameter(0).getType())).collect(Collectors.toList());
    assertEquals(1, methods.size());

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);

    assertDeepEquals(visitorType, parameter.getType());
    assertEquals("visitor", parameter.getName());
  }

  @Test
  public void testAcceptSuperMethod() {
    List<ASTCDMethod> methods = getMethodsBy("accept", 1, astClass);
    ASTType visitorType = this.cdTypeFactory.createSimpleReferenceType("de.monticore.codegen.ast.super._visitor.SuperVisitor");

    methods = methods.stream().filter(m -> visitorType.deepEquals(m.getCDParameter(0).getType())).collect(Collectors.toList());
    assertEquals(1, methods.size());

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(visitorType, parameter.getType());
    assertEquals("visitor", parameter.getName());
  }

  @Test
  public void testConstructMethod() {
    ASTCDMethod method = getMethodBy("_construct", astClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(astClass.getName());
    assertDeepEquals(astType, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astClass, astClass);
    System.out.println(sb.toString());
  }

  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/de/monticore/codegen/ast").toFile());
    Path generatedFiles = Paths.get("ASTAutomaton.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, astClass, astClass);
  }
}
