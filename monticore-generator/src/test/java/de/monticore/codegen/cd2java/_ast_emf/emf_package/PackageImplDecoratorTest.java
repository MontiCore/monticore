package de.monticore.codegen.cd2java._ast_emf.emf_package;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class PackageImplDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass packageClass;

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "_ast_emf", "Automata");

    this.glex.setGlobalValue("service", new EmfService(ast));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    PackageImplDecorator decorator = new PackageImplDecorator(this.glex, new MandatoryAccessorDecorator(glex), new DecorationHelper());
    packageClass = decorator.decorate(ast);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomataPackageImpl", packageClass.getName());
  }

  @Test
  public void testSuperInterface() {
    assertEquals(1, packageClass.sizeInterfaces());
    assertDeepEquals("AutomataPackage", packageClass.getInterface(0));
  }

  @Test
  public void testSuperClass() {
    assertTrue(packageClass.isPresentSuperclass());
    assertEquals("org.eclipse.emf.ecore.impl.EPackageImpl", packageClass.printSuperClass());
  }


  @Test
  public void testAttributeSize() {
    assertEquals(9, packageClass.getCDAttributeList().size());
  }

  @Test
  public void testMethodSize() {
    assertFalse(packageClass.getCDMethodList().isEmpty());
    assertEquals(27, packageClass.getCDMethodList().size());
  }

  @Test
  public void testConstructor() {
    assertEquals(1, packageClass.sizeCDConstructors());
    ASTCDConstructor cdConstructor = packageClass.getCDConstructor(0);
    assertEquals("AutomataPackageImpl", cdConstructor.getName());
    assertDeepEquals(PRIVATE, cdConstructor.getModifier());
    assertTrue(cdConstructor.isEmptyCDParameters());
  }

  @Test
  public void testConstantAutomataAttribute() {
    ASTCDAttribute constantsAutomata = getAttributeBy("constantsAutomata", packageClass);
    assertTrue(constantsAutomata.isPresentModifier());
    assertDeepEquals(PRIVATE, constantsAutomata.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EEnum", constantsAutomata.getType());
  }

  @Test
  public void testASTAutomatonAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTAutomaton", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getType());
  }

  @Test
  public void testASTStateAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTState", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getType());
  }

  @Test
  public void testASTTransitionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTTransition", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getType());
  }

  @Test
  public void testIsCreatedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isCreated", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertBoolean(attribute.getType());
  }

  @Test
  public void testIsInitializedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isInitialized", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertBoolean(attribute.getType());
  }

  @Test
  public void testIsInitedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isInited", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE_STATIC, attribute.getModifier());
    assertBoolean(attribute.getType());
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod method = getMethodBy("init", packageClass);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());
    assertDeepEquals("AutomataPackage", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetAutomataFactoryMethod() {
    ASTCDMethod method = getMethodBy("getAutomataFactory", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AutomataNodeFactory", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetConstantsAutomataMethod() {
    ASTCDMethod method = getMethodBy("getConstantsAutomata", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EEnum", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTESuperPackagesMethod() {
    ASTCDMethod method = getMethodBy("getASTESuperPackages", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(CDTypeFacade.getInstance().createListTypeOf("de.monticore.emf._ast.ASTEPackage"), method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGetASTAutomatonMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTStateMethod() {
    ASTCDMethod method = getMethodBy("getASTState", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTTransitionMethod() {
    ASTCDMethod method = getMethodBy("getASTTransition", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_NameMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Name", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_StatesMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_States", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_TransitionsMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Transitions", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference", method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCreatePackageContentsMethod() {
    ASTCDMethod method = getMethodBy("createPackageContents", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, packageClass, packageClass);
    System.out.println(sb.toString());
  }

}
