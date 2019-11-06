package de.monticore.codegen.cd2java._symboltable.symTablMill;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.symbTabMill.SymTabMillDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class SymTabMillDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symTabMill;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymTabMillDecorator decorator = new SymTabMillDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit));
    decorator.setLanguageTop(true);

    //creates normal Symbol
    this.symTabMill = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonSymTabMill", symTabMill.getName());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertTrue(symTabMill.isEmptyInterfaces());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(symTabMill.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symTabMill.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = symTabMill.getCDConstructor(0);
    assertDeepEquals(PROTECTED, cdConstructor.getModifier());
    assertEquals("AutomatonSymTabMill", cdConstructor.getName());
    assertTrue(cdConstructor.isEmptyCDParameters());
    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(14, symTabMill.sizeCDAttributes());
  }

  @Test
  public void testMillAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("mill", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillAutomatonSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbol", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillStateSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("stateSymbol", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillSymbolInterfaceSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("symbolInterfaceSymbol", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillAutomatonSymbolReferenceAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbolLoader", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillStateSymbolReferenceAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("stateSymbolLoader", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillSymbolInterfaceSymbolReferenceAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("symbolInterfaceSymbolReference", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillModelLoaderAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonModelLoader", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillLanguageAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonLanguage", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillSymbolTableCreatorAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbolTableCreator", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillSymbolTableCreatorDelegatorAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbolTableCreatorDelegator", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillGlobalScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonGlobalScope", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillArtifactScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonArtifactScope", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMillScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonScope", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymTabMill", astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(31, symTabMill.getCDMethodList().size());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod method = getMethodBy("getMill", symTabMill);
    assertDeepEquals(PROTECTED_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymTabMill", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testInitMeMethod() {
    ASTCDMethod method = getMethodBy("initMe", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("AutomatonSymTabMill", method.getCDParameter(0).getMCType());
    assertEquals("a", method.getCDParameter(0).getName());
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod method = getMethodBy("init", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testResetMethod() {
    ASTCDMethod method = getMethodBy("reset", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void test_AutomatonSymbolBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonSymbolBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_StateSymbolBuilderMethod() {
    ASTCDMethod method = getMethodBy("_stateSymbolBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("StateSymbolBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_SymbolInterfaceSymbolBuilderMethod() {
    ASTCDMethod method = getMethodBy("_symbolInterfaceSymbolBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("SymbolInterfaceSymbolBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_AutomatonSymbolReferenceBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonSymbolLoaderBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolLoaderBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_StateSymbolReferenceBuilderMethod() {
    ASTCDMethod method = getMethodBy("_stateSymbolLoaderBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("StateSymbolLoaderBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_SymbolInterfaceSymbolReferenceBuilderMethod() {
    ASTCDMethod method = getMethodBy("_symbolInterfaceSymbolReferenceBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("SymbolInterfaceSymbolReferenceBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_ModelLoaderBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonModelLoaderBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonModelLoaderBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_LanguageBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonLanguageBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonLanguageBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_SymbolTableCreatorBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonSymbolTableCreatorBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolTableCreatorBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_SymbolTableCreatorDelegatorBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonSymbolTableCreatorDelegatorBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolTableCreatorDelegatorBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_GlobalScopeBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonGlobalScopeBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonGlobalScopeBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_ArtifactScopeBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonArtifactScopeBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonArtifactScopeBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void test_ScopeBuilderMethod() {
    ASTCDMethod method = getMethodBy("_automatonScopeBuilder", symTabMill);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonScopeBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testAutomatonSymbolBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonSymbolBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testStateSymbolBuilderMethod() {
    ASTCDMethod method = getMethodBy("stateSymbolBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("StateSymbolBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSymbolInterfaceSymbolBuilderMethod() {
    ASTCDMethod method = getMethodBy("symbolInterfaceSymbolBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("SymbolInterfaceSymbolBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAutomatonSymbolReferenceBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonSymbolLoaderBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolLoaderBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testStateSymbolReferenceBuilderMethod() {
    ASTCDMethod method = getMethodBy("stateSymbolLoaderBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("StateSymbolLoaderBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSymbolInterfaceSymbolReferenceBuilderMethod() {
    ASTCDMethod method = getMethodBy("symbolInterfaceSymbolReferenceBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("SymbolInterfaceSymbolReferenceBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testModelLoaderBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonModelLoaderBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonModelLoaderBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testLanguageBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonLanguageBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonLanguageBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testSymbolTableCreatorBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonSymbolTableCreatorBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolTableCreatorBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testSymbolTableCreatorDelegatorBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonSymbolTableCreatorDelegatorBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonSymbolTableCreatorDelegatorBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGlobalScopeBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonGlobalScopeBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonGlobalScopeBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testArtifactScopeBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonArtifactScopeBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonArtifactScopeBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testScopeBuilderMethod() {
    ASTCDMethod method = getMethodBy("automatonScopeBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonScopeBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testQualifiedNameSymbolBuilderMethod() {
    ASTCDMethod method = getMethodBy("qualifiedNameSymbolBuilder", symTabMill);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbolBuilder", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symTabMill, symTabMill);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
