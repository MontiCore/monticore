package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GlobalScopeClassDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  public static final String MODEL_PATH = "de.monticore.io.paths.ModelPath";

  public static final String AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.AutomatonScope";

  public static final String I_AUTOMATON_GLOBAL_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonGlobalScope";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    GlobalScopeClassDecorator decorator = new GlobalScopeClassDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit),
        new MethodDecorator(glex));

    //creates normal Symbol
    this.scopeClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonGlobalScope", scopeClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(1, scopeClass.sizeInterfaces());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals(I_AUTOMATON_GLOBAL_SCOPE, scopeClass.getInterface(0));
  }

  @Test
  public void testSuperClass() {
    assertDeepEquals(AUTOMATON_SCOPE, scopeClass.getSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, scopeClass.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonGlobalScope", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(MODEL_PATH, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("modelPath", cdConstructor.getCDParameter(0).getName());

    assertDeepEquals("AutomatonLanguage", cdConstructor.getCDParameter(1).getMCType());
    assertEquals("language", cdConstructor.getCDParameter(1).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(6, scopeClass.sizeCDAttributes());
  }

  @Test
  public void testModelPathAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("modelPath", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(MODEL_PATH, astcdAttribute.getMCType());
  }

  @Test
  public void testLanguageAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonLanguage", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonLanguage", astcdAttribute.getMCType());
  }

  @Test
  public void testModelName2ModelLoaderCacheAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("modelName2ModelLoaderCache", scopeClass);
    assertDeepEquals(PROTECTED_FINAL, astcdAttribute.getModifier());
    assertDeepEquals("Map<String,Set<AutomatonModelLoader>>", astcdAttribute.getMCType());
  }

  @Test
  public void testAdaptedAutomatonSymbolResolvingDelegateListAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("adaptedAutomatonSymbolResolvingDelegateList", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Collection<de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolvingDelegate>", astcdAttribute.getMCType());
  }

  @Test
  public void testAdaptedStateSymbolResolvingDelegateListAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("adaptedStateSymbolResolvingDelegateList", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Collection<de.monticore.codegen.ast.automaton._symboltable.IStateSymbolResolvingDelegate>", astcdAttribute.getMCType());
  }

  @Test
  public void testAdaptedQualifiedNameSymbolResolvingDelegateListAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("adaptedQualifiedNameSymbolResolvingDelegateList", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Collection<de.monticore.codegen.ast.lexicals._symboltable.IQualifiedNameSymbolResolvingDelegate>", astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(20, scopeClass.getCDMethodList().size());
  }

  @Test
  public void testGetModelPathMethod() {
    ASTCDMethod method = getMethodBy("getModelPath", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MODEL_PATH, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetAutomatonLanguageMethod() {
    ASTCDMethod method = getMethodBy("getAutomatonLanguage", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AutomatonLanguage", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCacheMethod() {
    ASTCDMethod method = getMethodBy("cache", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("calculatedModelName", method.getCDParameter(0).getName());
  }


  @Test
  public void testContinueWithModelLoaderMethod() {
    ASTCDMethod method = getMethodBy("continueWithModelLoader", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("calculatedModelName", method.getCDParameter(0).getName());
    assertDeepEquals("AutomatonModelLoader", method.getCDParameter(1).getMCType());
    assertEquals("modelLoader", method.getCDParameter(1).getName());
  }

  @Test
  public void testSetAdaptedAutomatonSymbolResolvingDelegateListMethod() {
    ASTCDMethod method = getMethodBy("setAdaptedAutomatonSymbolResolvingDelegateList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("Collection<de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolvingDelegate>", method.getCDParameter(0).getMCType());
    assertEquals("adaptedAutomatonSymbolResolvingDelegateList", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetAdaptedAutomatonSymbolResolvingDelegateListMethod() {
    ASTCDMethod method = getMethodBy("getAdaptedAutomatonSymbolResolvingDelegateList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("Collection<de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolvingDelegate>",
        method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testAddAdaptedAutomatonSymbolResolvingDelegateMethod() {
    List<ASTCDMethod> methods = getMethodsBy("addAdaptedAutomatonSymbolResolvingDelegate",1,  scopeClass);

    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolvingDelegate", method.getCDParameter(0).getMCType());
    assertEquals("adaptedAutomatonSymbolResolvingDelegateList", method.getCDParameter(0).getName());
  }

  @Test
  public void testIsStateSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("isStateSymbolsAlreadyResolved", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsAutomatonSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("isAutomatonSymbolsAlreadyResolved", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetAutomatonSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("setAutomatonSymbolsAlreadyResolved", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("automatonSymbolsAlreadyResolved", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetStateSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("setStateSymbolsAlreadyResolved", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("stateSymbolsAlreadyResolved", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeClass, scopeClass);
    System.out.println(sb.toString());
    StaticJavaParser.parse(sb.toString());
  }
}
