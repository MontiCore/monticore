package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
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
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ScopeClassDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private CDTypeFacade cdTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String ENCLOSING_SCOPE_TYPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  private static final String STATE_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.ast.automaton._symboltable.StateSymbol>";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String STATE_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.StateSymbol";

  private static final String A_NODE_TYPE = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  private static final String ACCESS_MODIFIER_TYPE = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_VISITOR = "de.monticore.codegen.ast.automaton._visitor.AutomatonSymbolVisitor";

  @Before
  public void setUp() {
    Log.init();
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ScopeClassDecorator decorator = new ScopeClassDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit),
        new MethodDecorator(glex));

    //creates normal Symbol
    this.scopeClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassName() {
    assertEquals("AutomatonScope", scopeClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(1, scopeClass.sizeInterfaces());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals(I_AUTOMATON_SCOPE, scopeClass.getInterface(0));
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(scopeClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(4, scopeClass.sizeCDConstructors());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testShadowingConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(1);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertBoolean(cdConstructor.getCDParameter(0).getMCType());
    assertEquals("shadowing", cdConstructor.getCDParameter(0).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testEnclosingScopeConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(2);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testShadowingAndEnclosingScopeConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(3);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());

    assertBoolean(cdConstructor.getCDParameter(1).getMCType());
    assertEquals("shadowing", cdConstructor.getCDParameter(1).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(4, scopeClass.sizeCDAttributes());
  }

  @Test
  public void testAutomatonSymbolAlreadyResolvedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbolsAlreadyResolved", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  }

  @Test
  public void testStateSymbolAlreadyResolvedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("stateSymbolsAlreadyResolved", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  }

  @Test
  public void testStateSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("stateSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(STATE_SYMBOL_MAP, astcdAttribute.getMCType());
  }

  @Test
  public void testAutomatonSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_SYMBOL_MAP, astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(10, scopeClass.getCDMethodList().size());
  }

  @Test
  public void testRemoveSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("remove", scopeClass);

    assertEquals(2, methodList.size());

    ASTCDMethod automatonRemove = methodList.get(0);
    assertDeepEquals(PUBLIC, automatonRemove.getModifier());
    assertTrue(automatonRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, automatonRemove.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(AUTOMATON_SYMBOL),
        automatonRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", automatonRemove.getCDParameter(0).getName());

    ASTCDMethod stateRemove = methodList.get(1);
    assertDeepEquals(PUBLIC, stateRemove.getModifier());
    assertTrue(stateRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, stateRemove.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(STATE_SYMBOL),
        stateRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", stateRemove.getCDParameter(0).getName());
  }

  @Test
  public void testAddSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("add", scopeClass);

    assertEquals(2, methodList.size());

    ASTCDMethod automatonRemove = methodList.get(0);
    assertDeepEquals(PUBLIC, automatonRemove.getModifier());
    assertTrue(automatonRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, automatonRemove.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(AUTOMATON_SYMBOL),
        automatonRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", automatonRemove.getCDParameter(0).getName());

    ASTCDMethod stateRemove = methodList.get(1);
    assertDeepEquals(PUBLIC, stateRemove.getModifier());
    assertTrue(stateRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, stateRemove.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(STATE_SYMBOL),
        stateRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", stateRemove.getCDParameter(0).getName());
  }

  @Test
  public void testGetAutomatonSymbolsMethod() {
    ASTCDMethod method = getMethodBy("getAutomatonSymbols", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(AUTOMATON_SYMBOL_MAP, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGetStateSymbolsMethod() {
    ASTCDMethod method = getMethodBy("getStateSymbols", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(STATE_SYMBOL_MAP, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
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
