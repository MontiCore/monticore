/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.emf_package;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class PackageImplDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass packageClass;

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "_ast_emf", "Automata");

    this.glex.setGlobalValue("service", new EmfService(ast));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    PackageImplDecorator decorator = new PackageImplDecorator(this.glex, new MandatoryAccessorDecorator(glex), new EmfService(ast));
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
    assertEquals(11, packageClass.getCDAttributeList().size());
  }

  @Test
  public void testMethodSize() {
    assertFalse(packageClass.getCDMethodList().isEmpty());
    assertEquals(26, packageClass.getCDMethodList().size());
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
    assertDeepEquals("org.eclipse.emf.ecore.EEnum", constantsAutomata.getMCType());
  }

  @Test
  public void testASTAutomatonAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTAutomaton", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  }

  @Test
  public void testASTStateAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTState", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  }

  @Test
  public void testASTTransitionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTTransition", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  }

  @Test
  public void testASTTransitionWithActionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTTransitionWithAction", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  }

  @Test
  public void testASTAutNameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTAutName", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  }

  @Test
  public void testASTBodyExtAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTBodyExt", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  }

  @Test
  public void testIsCreatedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isCreated", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertBoolean(attribute.getMCType());
  }

  @Test
  public void testIsInitializedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isInitialized", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE, attribute.getModifier());
    assertBoolean(attribute.getMCType());
  }

  @Test
  public void testIsInitedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isInited", packageClass);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PRIVATE_STATIC, attribute.getModifier());
    assertBoolean(attribute.getMCType());
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod method = getMethodBy("init", packageClass);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());
    assertDeepEquals("AutomataPackage", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetAutomataFactoryMethod() {
    ASTCDMethod method = getMethodBy("getAutomataFactory", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AutomataNodeFactory", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetConstantsAutomataMethod() {
    ASTCDMethod method = getMethodBy("getConstantsAutomata", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EEnum", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTESuperPackagesMethod() {
    ASTCDMethod method = getMethodBy("getASTESuperPackages", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.getInstance().createListTypeOf("de.monticore.emf._ast.ASTEPackage"), method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGetASTAutomatonMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTStateMethod() {
    ASTCDMethod method = getMethodBy("getASTState", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTTransitionMethod() {
    ASTCDMethod method = getMethodBy("getASTTransition", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_NameMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Name", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_StatesMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_States", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_TransitionsMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Transitions", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetInterfaceAttributeMethod() {
    ASTCDMethod method = getMethodBy("getASTBodyExt_Varname", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testNoInheritedAttributeMethodsMethod() {
    assertTrue(packageClass.getCDMethodList()
        .stream()
        .noneMatch(m->m.getName().equals("getASTAutName_Input")));
  }

  @Test
  public void testCreatePackageContentsMethod() {
    ASTCDMethod method = getMethodBy("createPackageContents", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, packageClass, packageClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

}
