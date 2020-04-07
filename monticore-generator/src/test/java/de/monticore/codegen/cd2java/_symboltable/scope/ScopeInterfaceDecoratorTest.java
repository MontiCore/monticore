/* (c) https://github.com/MontiCore/monticore */
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
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScopeInterfaceDecoratorTest extends DecoratorTestCase {
  private ASTCDInterface scopeInterface;

  private GlobalExtensionManagement glex;

  private de.monticore.types.MCTypeFacade MCTypeFacade;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol>";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol";

  private static final String I_LEXICAS_SCOPE = "de.monticore.codegen.ast.lexicals._symboltable.ILexicalsScope";

  private static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String PREDICATE = "java.util.function.Predicate<de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol>";


  @Before
  public void setUp() {
    Log.init();
    this.MCTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    ASTCDCompilationUnit symbolCd = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalCompilationUnit = decoratedScopeCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    ScopeInterfaceDecorator decorator = new ScopeInterfaceDecorator(this.glex, new SymbolTableService(astcdCompilationUnit),
        new VisitorService(astcdCompilationUnit), new MethodDecorator(glex, new SymbolTableService(astcdCompilationUnit)));

    //creates normal Symbol
    this.scopeInterface = decorator.decorate(decoratedScopeCompilationUnit, symbolCd);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedScopeCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("IAutomatonScope", scopeInterface.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, scopeInterface.sizeInterfaces());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals(I_LEXICAS_SCOPE, scopeInterface.getInterface(0));
    assertDeepEquals("de.monticore.IBlaScope", scopeInterface.getInterface(1));
  }

  @Test
  public void testNoAttributes() {
    assertTrue(scopeInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(137, scopeInterface.getCDMethodList().size());
  }

  @Test
  public void testIsStateSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("isStateSymbolsAlreadyResolved", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsAutomatonSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("isAutomatonSymbolsAlreadyResolved", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetAutomatonSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("setAutomatonSymbolsAlreadyResolved", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("symbolAlreadyResolved", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetStateSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("setStateSymbolsAlreadyResolved", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("symbolAlreadyResolved", method.getCDParameter(0).getName());
  }

  @Test
  public void testResolveAutomaton1ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomaton", 1, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resolveName = methodList.get(0);
    assertDeepEquals(PUBLIC, resolveName.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), resolveName.getMCReturnType().getMCType());
    assertEquals(1, resolveName.sizeCDParameters());
    assertDeepEquals(String.class, resolveName.getCDParameter(0).getMCType());
    assertEquals("name", resolveName.getCDParameter(0).getName());
  }

  @Test
  public void testResolveAutomaton2ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomaton", 2, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resolveNameModifier = methodList.get(0);
    assertDeepEquals(PUBLIC, resolveNameModifier.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), resolveNameModifier.getMCReturnType().getMCType());
    assertEquals(2, resolveNameModifier.sizeCDParameters());
    assertDeepEquals(String.class, resolveNameModifier.getCDParameter(0).getMCType());
    assertEquals("name", resolveNameModifier.getCDParameter(0).getName());
    assertDeepEquals(ACCESS_MODIFIER, resolveNameModifier.getCDParameter(1).getMCType());
    assertEquals("modifier", resolveNameModifier.getCDParameter(1).getName());
  }

  @Test
  public void testResolveAutomaton3ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomaton", 3, scopeInterface);

    assertEquals(2, methodList.size());

    ASTCDMethod resoleNameModifierPredicate = methodList.get(0);
    assertDeepEquals(PUBLIC, resoleNameModifierPredicate.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), resoleNameModifierPredicate.getMCReturnType().getMCType());
    assertEquals(3, resoleNameModifierPredicate.sizeCDParameters());
    assertDeepEquals(String.class, resoleNameModifierPredicate.getCDParameter(0).getMCType());
    assertEquals("name", resoleNameModifierPredicate.getCDParameter(0).getName());
    assertDeepEquals(ACCESS_MODIFIER, resoleNameModifierPredicate.getCDParameter(1).getMCType());
    assertEquals("modifier", resoleNameModifierPredicate.getCDParameter(1).getName());
    assertDeepEquals(PREDICATE, resoleNameModifierPredicate.getCDParameter(2).getMCType());
    assertEquals("predicate", resoleNameModifierPredicate.getCDParameter(2).getName());

    ASTCDMethod resoleFoundSymbolsNameModifier = methodList.get(1);
    assertDeepEquals(PUBLIC, resoleFoundSymbolsNameModifier.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), resoleFoundSymbolsNameModifier.getMCReturnType().getMCType());
    assertEquals(3, resoleFoundSymbolsNameModifier.sizeCDParameters());
    assertBoolean(resoleFoundSymbolsNameModifier.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", resoleFoundSymbolsNameModifier.getCDParameter(0).getName());
    assertDeepEquals(String.class, resoleFoundSymbolsNameModifier.getCDParameter(1).getMCType());
    assertEquals("name", resoleFoundSymbolsNameModifier.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, resoleFoundSymbolsNameModifier.getCDParameter(2).getMCType());
    assertEquals("modifier", resoleFoundSymbolsNameModifier.getCDParameter(2).getName());
  }


  @Test
  public void testResolveDownAutomaton1ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonDown", 1, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resolveName = methodList.get(0);
    assertDeepEquals(PUBLIC, resolveName.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), resolveName.getMCReturnType().getMCType());
    assertEquals(1, resolveName.sizeCDParameters());
    assertDeepEquals(String.class, resolveName.getCDParameter(0).getMCType());
    assertEquals("name", resolveName.getCDParameter(0).getName());
  }

  @Test
  public void testResolveDownAutomaton2ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonDown", 2, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resolveNameModifier = methodList.get(0);
    assertDeepEquals(PUBLIC, resolveNameModifier.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), resolveNameModifier.getMCReturnType().getMCType());
    assertEquals(2, resolveNameModifier.sizeCDParameters());
    assertDeepEquals(String.class, resolveNameModifier.getCDParameter(0).getMCType());
    assertEquals("name", resolveNameModifier.getCDParameter(0).getName());
    assertDeepEquals(ACCESS_MODIFIER, resolveNameModifier.getCDParameter(1).getMCType());
    assertEquals("modifier", resolveNameModifier.getCDParameter(1).getName());
  }

  @Test
  public void testResolveDownAutomaton3ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonDown", 3, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resoleNameModifierPredicate = methodList.get(0);
    assertDeepEquals(PUBLIC, resoleNameModifierPredicate.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), resoleNameModifierPredicate.getMCReturnType().getMCType());
    assertEquals(3, resoleNameModifierPredicate.sizeCDParameters());
    assertDeepEquals(String.class, resoleNameModifierPredicate.getCDParameter(0).getMCType());
    assertEquals("name", resoleNameModifierPredicate.getCDParameter(0).getName());
    assertDeepEquals(ACCESS_MODIFIER, resoleNameModifierPredicate.getCDParameter(1).getMCType());
    assertEquals("modifier", resoleNameModifierPredicate.getCDParameter(1).getName());
    assertDeepEquals(PREDICATE, resoleNameModifierPredicate.getCDParameter(2).getMCType());
    assertEquals("predicate", resoleNameModifierPredicate.getCDParameter(2).getName());
  }

  @Test
  public void testResolveDownManyAutomaton1ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonDownMany", 1, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resolveName = methodList.get(0);
    assertDeepEquals(PUBLIC, resolveName.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), resolveName.getMCReturnType().getMCType());
    assertEquals(1, resolveName.sizeCDParameters());
    assertDeepEquals(String.class, resolveName.getCDParameter(0).getMCType());
    assertEquals("name", resolveName.getCDParameter(0).getName());
  }

  @Test
  public void testResolveDownManyAutomaton2ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonDownMany", 2, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resolveNameModifier = methodList.get(0);
    assertDeepEquals(PUBLIC, resolveNameModifier.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), resolveNameModifier.getMCReturnType().getMCType());
    assertEquals(2, resolveNameModifier.sizeCDParameters());
    assertDeepEquals(String.class, resolveNameModifier.getCDParameter(0).getMCType());
    assertEquals("name", resolveNameModifier.getCDParameter(0).getName());
    assertDeepEquals(ACCESS_MODIFIER, resolveNameModifier.getCDParameter(1).getMCType());
    assertEquals("modifier", resolveNameModifier.getCDParameter(1).getName());
  }

  @Test
  public void testResolveDownManyAutomaton3ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonDownMany", 3, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resoleNameModifierPredicate = methodList.get(0);
    assertDeepEquals(PUBLIC, resoleNameModifierPredicate.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), resoleNameModifierPredicate.getMCReturnType().getMCType());
    assertEquals(3, resoleNameModifierPredicate.sizeCDParameters());
    assertDeepEquals(String.class, resoleNameModifierPredicate.getCDParameter(0).getMCType());
    assertEquals("name", resoleNameModifierPredicate.getCDParameter(0).getName());
    assertDeepEquals(ACCESS_MODIFIER, resoleNameModifierPredicate.getCDParameter(1).getMCType());
    assertEquals("modifier", resoleNameModifierPredicate.getCDParameter(1).getName());
    assertDeepEquals(PREDICATE, resoleNameModifierPredicate.getCDParameter(2).getMCType());
    assertEquals("predicate", resoleNameModifierPredicate.getCDParameter(2).getName());
  }

  @Test
  public void testResolveDownManyAutomaton4ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonDownMany", 4, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resoleFoundSymbolsNameModifier = methodList.get(0);
    assertDeepEquals(PUBLIC, resoleFoundSymbolsNameModifier.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), resoleFoundSymbolsNameModifier.getMCReturnType().getMCType());
    assertEquals(4, resoleFoundSymbolsNameModifier.sizeCDParameters());
    assertBoolean(resoleFoundSymbolsNameModifier.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", resoleFoundSymbolsNameModifier.getCDParameter(0).getName());
    assertDeepEquals(String.class, resoleFoundSymbolsNameModifier.getCDParameter(1).getMCType());
    assertEquals("name", resoleFoundSymbolsNameModifier.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, resoleFoundSymbolsNameModifier.getCDParameter(2).getMCType());
    assertEquals("modifier", resoleFoundSymbolsNameModifier.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, resoleFoundSymbolsNameModifier.getCDParameter(3).getMCType());
    assertEquals("predicate", resoleFoundSymbolsNameModifier.getCDParameter(3).getName());
  }

  @Test
  public void testResolveManyAutomaton1ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonMany", 1, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod resolveName = methodList.get(0);
    assertDeepEquals(PUBLIC, resolveName.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), resolveName.getMCReturnType().getMCType());
    assertEquals(1, resolveName.sizeCDParameters());
    assertDeepEquals(String.class, resolveName.getCDParameter(0).getMCType());
    assertEquals("name", resolveName.getCDParameter(0).getName());
  }

  @Test
  public void testResolveManyAutomaton2ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonMany", 2, scopeInterface);

    assertEquals(2, methodList.size());

    ASTCDMethod methodModifier = methodList.get(0);
    assertDeepEquals(PUBLIC, methodModifier.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), methodModifier.getMCReturnType().getMCType());
    assertEquals(2, methodModifier.sizeCDParameters());
    assertDeepEquals(String.class, methodModifier.getCDParameter(0).getMCType());
    assertEquals("name", methodModifier.getCDParameter(0).getName());
    assertDeepEquals(ACCESS_MODIFIER, methodModifier.getCDParameter(1).getMCType());
    assertEquals("modifier", methodModifier.getCDParameter(1).getName());

    ASTCDMethod methodPredicate = methodList.get(1);
    assertDeepEquals(PUBLIC, methodPredicate.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), methodPredicate.getMCReturnType().getMCType());
    assertEquals(2, methodPredicate.sizeCDParameters());
    assertDeepEquals(String.class, methodPredicate.getCDParameter(0).getMCType());
    assertEquals("name", methodPredicate.getCDParameter(0).getName());
    assertDeepEquals(PREDICATE, methodPredicate.getCDParameter(1).getMCType());
    assertEquals("predicate", methodPredicate.getCDParameter(1).getName());
  }

  @Test
  public void testResolveManyAutomaton3ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonMany", 3, scopeInterface);

    assertEquals(2, methodList.size());

    ASTCDMethod resoleNameModifierPredicate = methodList.get(0);
    assertDeepEquals(PUBLIC, resoleNameModifierPredicate.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), resoleNameModifierPredicate.getMCReturnType().getMCType());
    assertEquals(3, resoleNameModifierPredicate.sizeCDParameters());
    assertDeepEquals(String.class, resoleNameModifierPredicate.getCDParameter(0).getMCType());
    assertEquals("name", resoleNameModifierPredicate.getCDParameter(0).getName());
    assertDeepEquals(ACCESS_MODIFIER, resoleNameModifierPredicate.getCDParameter(1).getMCType());
    assertEquals("modifier", resoleNameModifierPredicate.getCDParameter(1).getName());
    assertDeepEquals(PREDICATE, resoleNameModifierPredicate.getCDParameter(2).getMCType());
    assertEquals("predicate", resoleNameModifierPredicate.getCDParameter(2).getName());

    ASTCDMethod resoleFoundSymbolsNameModifier = methodList.get(1);
    assertDeepEquals(PUBLIC, resoleFoundSymbolsNameModifier.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), resoleFoundSymbolsNameModifier.getMCReturnType().getMCType());
    assertEquals(3, resoleFoundSymbolsNameModifier.sizeCDParameters());
    assertBoolean(resoleFoundSymbolsNameModifier.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", resoleFoundSymbolsNameModifier.getCDParameter(0).getName());
    assertDeepEquals(String.class, resoleFoundSymbolsNameModifier.getCDParameter(1).getMCType());
    assertEquals("name", resoleFoundSymbolsNameModifier.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, resoleFoundSymbolsNameModifier.getCDParameter(2).getMCType());
    assertEquals("modifier", resoleFoundSymbolsNameModifier.getCDParameter(2).getName());
  }

  @Test
  public void testResolveManyAutomaton4ParamMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("resolveAutomatonMany", 4, scopeInterface);

    assertEquals(1, methodList.size());

    ASTCDMethod method = methodList.get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAutomatonLocallyMethod() {
    ASTCDMethod method = getMethodBy("resolveAutomatonLocally", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testResolveAutomatonImportedMethod() {
    ASTCDMethod method = getMethodBy("resolveAutomatonImported", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testResolveAutomatonLocallyManyMethod() {
    ASTCDMethod method = getMethodBy("resolveAutomatonLocallyMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testResolveAdaptedAutomatonLocallyManyMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomatonLocallyMany", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testContinueWithEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("continueAutomatonWithEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testContinueAsSubScopeMethod() {
    ASTCDMethod method = getMethodBy("continueAsAutomatonSubScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testFilterMethod() {
    ASTCDMethod method = getMethodBy("filterAutomaton", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createOptionalTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL_MAP, method.getCDParameter(1).getMCType());
    assertEquals("symbols", method.getCDParameter(1).getName());
  }

  @Test
  public void testGetSymbolsMethod() {
    ASTCDMethod method = getMethodBy("getAutomatonSymbols", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals(AUTOMATON_SYMBOL_MAP, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetLocalSymbolsMethod() {
    ASTCDMethod method = getMethodBy("getLocalAutomatonSymbols", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAddMethod() {
    List<ASTCDMethod> method = getMethodsBy("add", 1, scopeInterface);

    assertEquals(3, method.size());

    ASTCDMethod automatonMethod = method.get(0);
    assertDeepEquals(PUBLIC_ABSTRACT, automatonMethod.getModifier());
    assertTrue(automatonMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, automatonMethod.sizeCDParameters());
    assertDeepEquals(AUTOMATON_SYMBOL, automatonMethod.getCDParameter(0).getMCType());
    assertEquals("symbol", automatonMethod.getCDParameter(0).getName());
  }

  @Test
  public void testRemoveMethod() {
    List<ASTCDMethod> method = getMethodsBy("remove", 1, scopeInterface);

    assertEquals(3, method.size());

    ASTCDMethod automatonMethod = method.get(0);
    assertDeepEquals(PUBLIC_ABSTRACT, automatonMethod.getModifier());
    assertTrue(automatonMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, automatonMethod.sizeCDParameters());
    assertDeepEquals(AUTOMATON_SYMBOL, automatonMethod.getCDParameter(0).getMCType());
    assertEquals("symbol", automatonMethod.getCDParameter(0).getName());
  }

  @Test
  public void testGetSubScopesMethod() {
    ASTCDMethod method = getMethodBy("getSubScopes", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals("List<? extends IAutomatonScope>", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAddSubScopeMethod() {
    ASTCDMethod method = getMethodBy("addSubScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("IAutomatonScope", method.getCDParameter(0).getMCType());
    assertEquals("subScope", method.getCDParameter(0).getName());
  }


  @Test
  public void testRemoveSubScopeMethod() {
    ASTCDMethod method = getMethodBy("removeSubScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("IAutomatonScope", method.getCDParameter(0).getMCType());
    assertEquals("subScope", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("IAutomatonScope", method.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals("IAutomatonScope", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodBy("accept", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._visitor.AutomatonScopeVisitor", method.getCDParameter(0).getMCType());
    assertEquals("visitor", method.getCDParameter(0).getName());
  }


  @Test
  public void testIsExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("isExtraAttribute", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("setExtraAttribute", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("extraAttribute", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetFooListMethod() {
    ASTCDMethod method = getMethodBy("getFooList", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetFooListMethod() {
    ASTCDMethod method = getMethodBy("setFooList", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertListOf(String.class, method.getCDParameter(0).getMCType());
    assertEquals("foo", method.getCDParameter(0).getName());
  }

  @Test
  public void testScopeRuleMethod() {
    ASTCDMethod method = getMethodBy("toString", scopeInterface);

    assertTrue(method.getModifier().isPublic());
    assertTrue(method.getModifier().isAbstract());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
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
