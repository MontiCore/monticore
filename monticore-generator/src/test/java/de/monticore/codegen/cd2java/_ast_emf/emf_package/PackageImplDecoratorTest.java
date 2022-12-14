/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.emf_package;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
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
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "_ast_emf", "Automata");

    this.glex.setGlobalValue("service", new EmfService(ast));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    PackageImplDecorator decorator = new PackageImplDecorator(this.glex, new MandatoryAccessorDecorator(glex), new EmfService(ast));
    packageClass = decorator.decorate(ast);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomataPackageImpl", packageClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterface() {
    assertEquals(1, packageClass.getInterfaceList().size());
    assertDeepEquals("AutomataPackage", packageClass.getCDInterfaceUsage().getInterface(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperClass() {
    assertTrue(packageClass.isPresentCDExtendUsage());
    assertEquals("org.eclipse.emf.ecore.impl.EPackageImpl", packageClass.printSuperclasses());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testAttributeSize() {
    assertEquals(11, packageClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodSize() {
    assertFalse(packageClass.getCDMethodList().isEmpty());
    assertEquals(26, packageClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructor() {
    assertEquals(1, packageClass.getCDConstructorList().size());
    ASTCDConstructor cdConstructor = packageClass.getCDConstructorList().get(0);
    assertEquals("AutomataPackageImpl", cdConstructor.getName());
    assertDeepEquals(PRIVATE, cdConstructor.getModifier());
    assertTrue(cdConstructor.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstantAutomataAttribute() {
    ASTCDAttribute constantsAutomata = getAttributeBy("constantsAutomata", packageClass);
    assertDeepEquals(PROTECTED, constantsAutomata.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EEnum", constantsAutomata.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTAutomatonAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTAutomaton", packageClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTStateAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTState", packageClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTTransitionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTTransition", packageClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTTransitionWithActionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTTransitionWithAction", packageClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTAutNameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTAutName", packageClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTBodyExtAttribute() {
    ASTCDAttribute attribute = getAttributeBy("aSTBodyExt", packageClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsCreatedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isCreated", packageClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertBoolean(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsInitializedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isInitialized", packageClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertBoolean(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsInitedAttribute() {
    ASTCDAttribute attribute = getAttributeBy("isInited", packageClass);
    assertDeepEquals(PROTECTED_STATIC, attribute.getModifier());
    assertBoolean(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod method = getMethodBy("init", packageClass);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());
    assertDeepEquals("AutomataPackage", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetConstantsAutomataMethod() {
    ASTCDMethod method = getMethodBy("getConstantsAutomata", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EEnum", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTESuperPackagesMethod() {
    ASTCDMethod method = getMethodBy("getASTESuperPackages", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.getInstance().createListTypeOf("de.monticore.emf._ast.ASTEPackage"), method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testGetASTAutomatonMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTStateMethod() {
    ASTCDMethod method = getMethodBy("getASTState", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTTransitionMethod() {
    ASTCDMethod method = getMethodBy("getASTTransition", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTAutomaton_NameMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Name", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTAutomaton_StatesMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_States", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTAutomaton_TransitionsMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Transitions", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetInterfaceAttributeMethod() {
    ASTCDMethod method = getMethodBy("getASTBodyExt_Varname", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoInheritedAttributeMethodsMethod() {
    assertTrue(packageClass.getCDMethodList()
        .stream()
        .noneMatch(m->m.getName().equals("getASTAutName_Input")));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreatePackageContentsMethod() {
    ASTCDMethod method = getMethodBy("createPackageContents", packageClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, packageClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
