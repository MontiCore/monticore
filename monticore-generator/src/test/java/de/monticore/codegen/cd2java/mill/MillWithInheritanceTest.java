package de.monticore.codegen.cd2java.mill;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertVoid;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillWithInheritanceTest extends DecoratorTestCase {

  private CDTypeFactory cdTypeFacade;

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFactory.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "factory", "CGrammar");
    MillDecorator decorator = new MillDecorator(this.glex);
    this.millClass = decorator.decorate(compilationUnit);
  }

  @Test
  public void testAttributeName() {
    assertEquals("mill", millClass.getCDAttribute(0).getName());
    assertEquals("millASTBlub", millClass.getCDAttribute(1).getName());
    assertEquals("millASTBli", millClass.getCDAttribute(2).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : millClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, millClass.sizeCDConstructors());
    assertTrue(PROTECTED.build().deepEquals(millClass.getCDConstructor(0).getModifier()));
    assertEquals("CGrammarMill", millClass.getCDConstructor(0).getName());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod getMill = millClass.getCDMethod(0);
    //test Method Name
    assertEquals("getMill", getMill.getName());
    //test Parameters
    assertTrue(getMill.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("CGrammarMill");
    assertDeepEquals(returnType, getMill.getReturnType());
    //test Modifier
    assertTrue(PROTECTED_STATIC.build().deepEquals(getMill.getModifier()));
  }

  @Test
  public void testInitMeMethod() {
    ASTCDMethod initMe = millClass.getCDMethod(1);
    //test Method Name
    assertEquals("initMe", initMe.getName());
    //test Parameters
    assertEquals(1, initMe.sizeCDParameters());
    ASTType type = cdTypeFacade.createSimpleReferenceType("CGrammarMill");
    assertDeepEquals(type, initMe.getCDParameter(0).getType());
    assertEquals("a", initMe.getCDParameter(0).getName());
    //test ReturnType
    assertVoid(initMe.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(initMe.getModifier()));
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod init = millClass.getCDMethod(2);
    //test Method Name
    assertEquals("init", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertVoid(init.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(init.getModifier()));
  }

  @Test
  public void testResetMethod() {
    ASTCDMethod init = millClass.getCDMethod(3);
    //test Method Name
    assertEquals("reset", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertVoid(init.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(init.getModifier()));
  }

  @Test
  public void testCBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(8);
    //test Method Name
    assertEquals("aSTBBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.bgrammar._ast.ASTB");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testFooBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(9);
    //test Method Name
    assertEquals("aSTFooBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void tesBarBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(10);
    //test Method Name
    assertEquals("aSTBarBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTBar");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, millClass, millClass);
    System.out.println(sb.toString());
  }

  @Ignore
  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/de/monticore/codegen/mill").toFile());
    Path generatedFiles = Paths.get("CGrammarMill.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, millClass, millClass);
  }
}
