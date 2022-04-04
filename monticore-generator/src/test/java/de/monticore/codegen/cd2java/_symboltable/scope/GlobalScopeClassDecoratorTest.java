/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.*;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class GlobalScopeClassDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private MCTypeFacade mcTypeFacade;

  private static final String MC_PATH = "de.monticore.io.paths.MCPath";

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.AutomatonScope";

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    this.glex = new GlobalExtensionManagement();
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    this.mcTypeFacade = MCTypeFacade.getInstance();
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    GlobalScopeClassDecorator decorator = new GlobalScopeClassDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));

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
    assertEquals(1, scopeClass.getInterfaceList().size());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.IAutomatonGlobalScope",
        scopeClass.getInterfaceList().get(0));
  }

  @Test
  public void testSuperClass() {
    assertDeepEquals(AUTOMATON_SCOPE, scopeClass.getCDExtendUsage().getSuperclass(0));
  }

  @Test
  public void testConstructorCount() {
    assertEquals(2, scopeClass.getCDConstructorList().size());
  }

  @Test
  public void testConstructors() {
    // this(symbolPath, modelFileExtension)
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonGlobalScope", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(MC_PATH, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("symbolPath", cdConstructor.getCDParameter(0).getName());

    assertDeepEquals("String", cdConstructor.getCDParameter(1).getMCType());
    assertEquals("fileExt", cdConstructor.getCDParameter(1).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());

    ASTCDConstructor zeroArgsConstructor = scopeClass.getCDConstructorList().get(1);
    assertDeepEquals(PUBLIC, zeroArgsConstructor.getModifier());
    assertEquals("AutomatonGlobalScope", zeroArgsConstructor.getName());

    assertEquals(0, zeroArgsConstructor.sizeCDParameters());

    assertFalse(zeroArgsConstructor.isPresentCDThrowsDeclaration());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(9, scopeClass.getCDAttributeList().size());
  }

  @Test
  public void testSymbolPathAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("symbolPath", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(MC_PATH, astcdAttribute.getMCType());
  }

  @Test
  public void testFileExtensionAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fileExt", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("String", astcdAttribute.getMCType());
  }

  @Test
  public void testLoadMethod() {
    ASTCDMethod method = getMethodBy("loadAutomaton", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testLoadSuperProdMethod() {
    ASTCDMethod method = getMethodBy("loadQualifiedName", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testCacheAttribute(){
    ASTCDAttribute astcdAttribute = getAttributeBy("cache", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Set<String>", astcdAttribute.getMCType());
  }

  @Test
  public void testAdaptedAutomatonSymbolResolverListAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("adaptedAutomatonSymbolResolver",
        scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(
        "List<de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolver>",
        astcdAttribute.getMCType());
  }

  @Test
  public void testAdaptedStateSymbolResolverListAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("adaptedStateSymbolResolver",
        scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(
        "List<de.monticore.codegen.ast.automaton._symboltable.IStateSymbolResolver>",
        astcdAttribute.getMCType());
  }

  @Test
  public void testAdaptedQualifiedNameSymbolResolverListAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("adaptedQualifiedNameSymbolResolver",
        scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(
        "List<de.monticore.codegen.ast.lexicals._symboltable.IQualifiedNameSymbolResolver>",
        astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(29, scopeClass.getCDMethodList().size());
  }

  @Test
  public void testGetSymbolPathMethod() {
    ASTCDMethod method = getMethodBy("getSymbolPath", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MC_PATH, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAddLoadedFileMethod() {
    ASTCDMethod method = getMethodBy("addLoadedFile", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetAdaptedAutomatonSymbolResolverListMethod() {
    ASTCDMethod method = getMethodBy("setAdaptedAutomatonSymbolResolverList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(
        "List<de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolver>",
        method.getCDParameter(0).getMCType());
    assertEquals("adaptedAutomatonSymbolResolver", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetAdaptedAutomatonSymbolResolverListMethod() {
    ASTCDMethod method = getMethodBy("getAdaptedAutomatonSymbolResolverList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(
        "List<de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolver>",
        method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testLoadFileForModelNameMethod(){
    ASTCDMethod method = getMethodBy("loadFileForModelName", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("modelName", method.getCDParameter(0).getName());
  }

  @Test
  public void testClearMethod(){
    ASTCDMethod method = getMethodBy("clear", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetSymbolPathMethod(){
    ASTCDMethod method = getMethodBy("setSymbolPath", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("symbolPath", method.getCDParameter(0).getName());
    assertDeepEquals(MC_PATH, method.getCDParameter(0).getMCType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeClass, scopeClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
  
  @Test
  public void testPutStateDeSer() {
    ASTCDMethod method = getMethodBy("putStateSymbolDeSer", scopeClass);
    
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("kind", method.getCDParameter(0).getName());
  }

}
