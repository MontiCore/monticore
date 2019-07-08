package de.monticore.codegen.cd2java._ast_emf.factory;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class EmfNodeFactoryDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass emfClass;

  @Before
  public void setup() {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");

    this.glex.setGlobalValue("service", new EmfService(compilationUnit));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    EmfNodeFactoryDecorator decorator = new EmfNodeFactoryDecorator(this.glex, new NodeFactoryService(compilationUnit));

    this.emfClass = decorator.decorate(compilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonNodeFactory", emfClass.getName());
  }


  @Test
  public void testSuperClass() {
    assertTrue(emfClass.isPresentSuperclass());
    assertEquals("org.eclipse.emf.ecore.impl.EFactoryImpl", emfClass.printSuperClass());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(4, emfClass.getCDAttributeList().size());
  }

  @Test
  public void testMethodSize() {
    assertFalse(emfClass.getCDMethodList().isEmpty());
    assertEquals(15, emfClass.getCDMethodList().size());
  }

  @Test
  public void testGetFactoryMethod() {
    ASTCDMethod method = getMethodBy("getFactory", emfClass);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());
    assertDeepEquals("AutomatonNodeFactory", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCreateMethod() {
    ASTCDMethod method = getMethodBy("create", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EObject", method.getReturnType());

    assertEquals(1, method.sizeCDParameters());
    assertEquals("eClass", method.getCDParameter(0).getName());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getCDParameter(0).getType());
  }

  @Test
  public void testAutomatonPackageMethod() {
    ASTCDMethod method = getMethodBy("getAutomatonPackage", emfClass);
    assertDeepEquals(PACKAGE_PRIVATE, method.getModifier());
    assertDeepEquals("AutomatonPackage", method.getReturnType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, emfClass, emfClass);
    System.out.println(sb.toString());
  }

}
