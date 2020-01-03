package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableConstants;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GlobalScopeInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface scopeInterface;

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol";

  private static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String PREDICATE_AUTOMATON = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  private static final String PREDICATE_QUALIFIED_NAME = "java.util.function.Predicate<de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol>";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  @Before
  public void setUp() {
    Log.init();
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    GlobalScopeInterfaceDecorator decorator = new GlobalScopeInterfaceDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit));

    this.scopeInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("IAutomatonGlobalScope", scopeInterface.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, scopeInterface.sizeInterfaces());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals(I_AUTOMATON_SCOPE, scopeInterface.getInterface(0));
    assertDeepEquals(SymbolTableConstants.I_GLOBAL_SCOPE_TYPE, scopeInterface.getInterface(1));
  }

  @Test
  public void testNoAttributes() {
    assertTrue(scopeInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(14, scopeInterface.getCDMethodList().size());
  }


  @Test
  public void testGetAutomatonLanguageMethod() {
    ASTCDMethod method = getMethodBy("getAutomatonLanguage", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals("AutomatonLanguage", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCacheMethod() {
    ASTCDMethod method = getMethodBy("cache", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("calculatedModelName", method.getCDParameter(0).getName());
  }


  @Test
  public void testContinueWithModelLoaderMethod() {
    ASTCDMethod method = getMethodBy("continueWithModelLoader", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("calculatedModelName", method.getCDParameter(0).getName());
    assertDeepEquals("AutomatonModelLoader", method.getCDParameter(1).getMCType());
    assertEquals("modelLoader", method.getCDParameter(1).getName());
  }

  @Test
  public void testResolveAutomatonManyMethod() {
    ASTCDMethod method = getMethodBy("resolveAutomatonMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAdaptedMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testLoadModelsForMethod() {
    ASTCDMethod method = getMethodBy("loadModelsForAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testResolveAutomatonManySuperProdMethod() {
    ASTCDMethod method = getMethodBy("resolveQualifiedNameMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(QUALIFIED_NAME_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAdaptedSuperProdMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedQualifiedName", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(QUALIFIED_NAME_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }


  @Test
  public void testLoadModelsForSuperProdMethod() {
    ASTCDMethod method = getMethodBy("loadModelsForQualifiedName", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, scopeInterface, scopeInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
