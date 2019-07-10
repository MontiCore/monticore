package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTEmfDecoratorTest extends DecoratorTestCase {
  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass emfClass;

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "Automaton");

    this.glex.setGlobalValue("service", new EmfService(ast));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    SymbolTableService symbolTableService = new SymbolTableService(ast);
    ASTEmfDecorator decorator = new ASTEmfDecorator(this.glex, new ASTService(ast), new VisitorService(ast), new NodeFactoryService(ast),
        new ASTSymbolDecorator(glex, symbolTableService), new ASTScopeDecorator(glex, symbolTableService), new MethodDecorator(glex),
        new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("ASTAutomaton", ast);
    this.emfClass = decorator.decorate(clazz);
  }

  @Test
  public void testClassName() {
    assertEquals("ASTAutomaton", emfClass.getName());
  }

  @Test
  public void testSuperInterface() {
    assertEquals(2, emfClass.sizeInterfaces());
    assertDeepEquals("de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode", emfClass.getInterface(0));
    assertDeepEquals("org.eclipse.emf.ecore.EPackage", emfClass.getInterface(1));
  }

  @Test
  public void testSuperClass() {
    assertTrue(emfClass.isPresentSuperclass());
    assertEquals("de.monticore.emf._ast.ASTECNode", emfClass.printSuperClass());
  }


  @Test
  public void testAttributeSize() {
    assertEquals(8, emfClass.getCDAttributeList().size());
  }

  @Test
  public void testMethodSize() {
    assertFalse(emfClass.getCDMethodList().isEmpty());
    assertEquals(39, emfClass.getCDMethodList().size());
  }

  @Test
  public void testEGetMethod() {
    ASTCDMethod method = getMethodBy("eGet", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(Object.class, method.getReturnType());

    assertEquals(3, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getType());
    assertEquals("resolve", method.getCDParameter(1).getName());
    assertBoolean(method.getCDParameter(1).getType());
    assertEquals("coreType", method.getCDParameter(2).getName());
    assertBoolean(method.getCDParameter(2).getType());
  }

  @Test
  public void testESetMethod() {
    ASTCDMethod method = getMethodBy("eSet", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());

    assertEquals(2, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getType());
    assertEquals("newValue", method.getCDParameter(1).getName());
    assertDeepEquals(Object.class,method.getCDParameter(1).getType());
  }

  @Test
  public void testEUnsetMethod() {
    ASTCDMethod method = getMethodBy("eUnset", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());

    assertEquals(1, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getType());
  }



  @Test
  public void testEBaseStructuralFeatureIDMethod() {
    ASTCDMethod method = getMethodBy("eBaseStructuralFeatureID", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertInt(method.getReturnType());

    assertEquals(2, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getType());
    assertEquals("baseClass", method.getCDParameter(1).getName());
    assertDeepEquals(CDTypeFacade.getInstance().createComplexReferenceType("Class<?>"),
        method.getCDParameter(1).getType());
  }

  @Test
  public void testEDerivedStructuralFeatureIDMethod() {
    ASTCDMethod method = getMethodBy("eDerivedStructuralFeatureID", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertInt(method.getReturnType());

    assertEquals(2, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getType());
    assertEquals("baseClass", method.getCDParameter(1).getName());
    assertDeepEquals(CDTypeFacade.getInstance().createComplexReferenceType("Class<?>"),
        method.getCDParameter(1).getType());
  }

  @Test
  public void testEToStringMethod() {
    ASTCDMethod method = getMethodBy("toString", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class,method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testEStaticClassMethod() {
    ASTCDMethod method = getMethodBy("eStaticClass", emfClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",method.getReturnType());
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
