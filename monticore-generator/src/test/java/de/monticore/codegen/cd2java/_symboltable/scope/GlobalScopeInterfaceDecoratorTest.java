/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GlobalScopeInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface scopeInterface;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private MCTypeFacade mcTypeFacade;

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String PREDICATE_AUTOMATON = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol";

  private static final String PREDICATE_QUALIFIED_NAME = "java.util.function.Predicate<de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol>";

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    this.glex = new GlobalExtensionManagement();
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    this.mcTypeFacade = MCTypeFacade.getInstance();
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    GlobalScopeInterfaceDecorator decorator = new GlobalScopeInterfaceDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));

    this.scopeInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testInterfaceName() {
    assertEquals("IAutomatonGlobalScope", scopeInterface.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, scopeInterface.sizeInterface());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals("de.monticore.symboltable.IGlobalScope",
        scopeInterface.getInterface(0));
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope",
        scopeInterface.getInterface(1));
  }

  @Test
  public void testCalculateModelNamesForAutomatonMethod() {
    ASTCDMethod method = getMethodBy("calculateModelNamesForAutomaton", scopeInterface);
    assertDeepEquals("Set<String>", method.getMCReturnType().getMCType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameters(0).getMCType());
    assertEquals("name", method.getCDParameters(0).getName());
  }

  @Test
  public void testCalculateModelNamesForStateMethod() {
    ASTCDMethod method = getMethodBy("calculateModelNamesForState", scopeInterface);
    assertDeepEquals("Set<String>", method.getMCReturnType().getMCType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameters(0).getMCType());
    assertEquals("name", method.getCDParameters(0).getName());
  }


  @Test
  public void testAddLoadedFileMethod() {
    ASTCDMethod method = getMethodBy("addLoadedFile", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameters(0).getMCType());
    assertEquals("name", method.getCDParameters(0).getName());
  }

  @Test
  public void testGetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameters(0).getMCType());
    assertEquals("enclosingScope", method.getCDParameters(0).getName());
  }

  @Test
  public void testGetSymbolFileExtensionMethod() {
    ASTCDMethod method = getMethodBy("getSymbolFileExtension", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetSymbolFileExtensionMethod() {
    ASTCDMethod method = getMethodBy("setSymbolFileExtension", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameters(0).getMCType());
    assertEquals("symbolFileExtension", method.getCDParameters(0).getName());
  }

  @Test
  public void testResolveAdaptedAutomatonMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameters(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameters(0).getName());
    assertDeepEquals(String.class, method.getCDParameters(1).getMCType());
    assertEquals("name", method.getCDParameters(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameters(2).getMCType());
    assertEquals("modifier", method.getCDParameters(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameters(3).getMCType());
    assertEquals("predicate", method.getCDParameters(3).getName());
  }

  @Test
  public void testResolveAdaptedSuperProdMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedQualifiedName", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(QUALIFIED_NAME_SYMBOL, method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameters(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameters(0).getName());
    assertDeepEquals(String.class, method.getCDParameters(1).getMCType());
    assertEquals("name", method.getCDParameters(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameters(2).getMCType());
    assertEquals("modifier", method.getCDParameters(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameters(3).getMCType());
    assertEquals("predicate", method.getCDParameters(3).getName());
  }


  @Test
  public void testAddAdaptedAutomatonSymbolResolvingDelegateMethod() {
    List<ASTCDMethod> methods = getMethodsBy("addAdaptedAutomatonSymbolResolvingDelegate", 1,
        scopeInterface);

    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(
        "de.monticore.codegen.ast.automaton._symboltable.IAutomatonSymbolResolvingDelegate",
        method.getCDParameters(0).getMCType());
    assertEquals("element", method.getCDParameters(0).getName());
  }


  @Test
  public void testResolveAutomatonManyMethod() {
    ASTCDMethod method = getMethodBy("resolveAutomatonMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL),
        method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameters(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameters(0).getName());
    assertDeepEquals(String.class, method.getCDParameters(1).getMCType());
    assertEquals("name", method.getCDParameters(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameters(2).getMCType());
    assertEquals("modifier", method.getCDParameters(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameters(3).getMCType());
    assertEquals("predicate", method.getCDParameters(3).getName());
  }

  @Test
  public void testResolveAdaptedMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL),
        method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameters(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameters(0).getName());
    assertDeepEquals(String.class, method.getCDParameters(1).getMCType());
    assertEquals("name", method.getCDParameters(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameters(2).getMCType());
    assertEquals("modifier", method.getCDParameters(2).getName());
    assertDeepEquals(PREDICATE_AUTOMATON, method.getCDParameters(3).getMCType());
    assertEquals("predicate", method.getCDParameters(3).getName());
  }

  @Test
  public void testResolveAutomatonManySuperProdMethod() {
    ASTCDMethod method = getMethodBy("resolveQualifiedNameMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(QUALIFIED_NAME_SYMBOL),
        method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameters(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameters(0).getName());
    assertDeepEquals(String.class, method.getCDParameters(1).getMCType());
    assertEquals("name", method.getCDParameters(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameters(2).getMCType());
    assertEquals("modifier", method.getCDParameters(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameters(3).getMCType());
    assertEquals("predicate", method.getCDParameters(3).getName());
  }

  @Test
  public void testMethodCount() {
    assertEquals(92, scopeInterface.getCDMethodsList().size());
  }

}
