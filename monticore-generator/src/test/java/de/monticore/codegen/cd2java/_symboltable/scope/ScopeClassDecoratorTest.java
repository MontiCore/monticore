/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cdbasis._ast.*;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScopeClassDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private de.monticore.types.MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol>";

  private static final String STATE_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol>";

  private static final String QUALIFIED_NAME_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol>";

  private static final String FOO_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.symboltable.automaton._symboltable.FooSymbol>";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol";

  private static final String STATE_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol";

  private static final String FOO_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.FooSymbol";

  private static final String UNKNOWN_SYMBOL = "de.monticore.symboltable.SymbolWithScopeOfUnknownKind";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol";

  private static final String AST_NODE_TYPE = "de.monticore.ast.ASTNode";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String I_SCOPE_SPANNING_SYMBOL = "de.monticore.symboltable.IScopeSpanningSymbol";

  private static final String I_LEXICAS_SCOPE = "de.monticore.codegen.ast.lexicals._symboltable.ILexicalsScope";


  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    this.mcTypeFacade = mcTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    ScopeClassDecorator decorator = new ScopeClassDecorator(this.glex, new SymbolTableService(astcdCompilationUnit),
        new VisitorService(decoratedSymbolCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedScopeCompilationUnit)));

    //creates normal Symbol
    this.scopeClass = decorator.decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonScope", scopeClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(1, scopeClass.getInterfaceList().size());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals(I_AUTOMATON_SCOPE, scopeClass.getCDInterfaceUsage().getInterface(0));
  }

  @Test
  public void testSuperClass() {
    assertTrue(scopeClass.isPresentCDExtendUsage());
    assertDeepEquals("de.monticore.FooScope", scopeClass.getCDExtendUsage().getSuperclass(0));
  }

  @Test
  public void testConstructorCount() {
    assertEquals(4, scopeClass.getCDConstructorList().size());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  }

  @Test
  public void testShadowingConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(1);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertBoolean(cdConstructor.getCDParameter(0).getMCType());
    assertEquals("shadowing", cdConstructor.getCDParameter(0).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  }

  @Test
  public void testEnclosingScopeConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(2);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  }

  @Test
  public void testShadowingAndEnclosingScopeConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(3);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());

    assertBoolean(cdConstructor.getCDParameter(1).getMCType());
    assertEquals("shadowing", cdConstructor.getCDParameter(1).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(21, scopeClass.getCDAttributeList().size());
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
  public void testQualifiedNameSymbolAlreadyResolvedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("qualifiedNameSymbolsAlreadyResolved", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  }

  @Test
  public void testFooSymbolAlreadyResolvedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fooSymbolsAlreadyResolved", scopeClass);
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
  public void testQualifiedNameSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("qualifiedNameSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(QUALIFIED_NAME_SYMBOL_MAP, astcdAttribute.getMCType());
  }

  @Test
  public void testFooSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fooSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(FOO_SYMBOL_MAP, astcdAttribute.getMCType());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("enclosingScope", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, astcdAttribute.getMCType());
  }

  @Test
  public void testSubScopesAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("subScopes", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertListOf(I_AUTOMATON_SCOPE, astcdAttribute.getMCType());
  }

  @Test
  public void testSpanningSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("spanningSymbol", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(I_SCOPE_SPANNING_SYMBOL, astcdAttribute.getMCType());
  }

  @Test
  public void testShadowingAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("shadowing", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  }

  @Test
  public void testExportsSymbolsAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("exportingSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  }

  @Test
  public void testOrderedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("ordered", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("name", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testAstNodeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("astNode", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(AST_NODE_TYPE, astcdAttribute.getMCType());
  }

  @Test
  public void testScopeRuleAttributes() {
    ASTCDAttribute fooAttribute = getAttributeBy("foo", scopeClass);
    assertDeepEquals(PROTECTED, fooAttribute.getModifier());
    assertListOf(String.class, fooAttribute.getMCType());

    ASTCDAttribute blaAttribute = getAttributeBy("bla", scopeClass);
    assertDeepEquals(PROTECTED, blaAttribute.getModifier());
    assertOptionalOf(Integer.class, blaAttribute.getMCType());

    ASTCDAttribute extraAtt = getAttributeBy("extraAttribute", scopeClass);
    assertDeepEquals(PROTECTED, extraAtt.getModifier());
    assertBoolean(extraAtt.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(99, scopeClass.getCDMethodList().size());
  }

  @Test
  public void testRemoveSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("remove", scopeClass);

    assertEquals(5, methodList.size());

    ASTCDMethod automatonRemove = methodList.get(0);
    assertDeepEquals(PUBLIC, automatonRemove.getModifier());
    assertTrue(automatonRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, automatonRemove.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL),
        automatonRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", automatonRemove.getCDParameter(0).getName());

    ASTCDMethod stateRemove = methodList.get(1);
    assertDeepEquals(PUBLIC, stateRemove.getModifier());
    assertTrue(stateRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, stateRemove.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(STATE_SYMBOL),
        stateRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", stateRemove.getCDParameter(0).getName());

    ASTCDMethod qualifiedNameRemove = methodList.get(2);
    assertDeepEquals(PUBLIC, qualifiedNameRemove.getModifier());
    assertTrue(qualifiedNameRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, qualifiedNameRemove.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(QUALIFIED_NAME_SYMBOL),
        qualifiedNameRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", qualifiedNameRemove.getCDParameter(0).getName());

    ASTCDMethod unknownRemove = methodList.get(3);
    assertDeepEquals(PUBLIC, unknownRemove.getModifier());
    assertTrue(unknownRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, unknownRemove.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(UNKNOWN_SYMBOL),
        unknownRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", unknownRemove.getCDParameter(0).getName());

    ASTCDMethod fooRemove = methodList.get(4);
    assertDeepEquals(PUBLIC, fooRemove.getModifier());
    assertTrue(fooRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, fooRemove.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(FOO_SYMBOL),
        fooRemove.getCDParameter(0).getMCType());
    assertEquals("symbol", fooRemove.getCDParameter(0).getName());
  }

  @Test
  public void testAddSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("add", scopeClass);

    assertEquals(5, methodList.size());

    ASTCDMethod automatonAdd = methodList.get(0);
    assertDeepEquals(PUBLIC, automatonAdd.getModifier());
    assertTrue(automatonAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, automatonAdd.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL),
        automatonAdd.getCDParameter(0).getMCType());
    assertEquals("symbol", automatonAdd.getCDParameter(0).getName());

    ASTCDMethod stateAdd = methodList.get(1);
    assertDeepEquals(PUBLIC, stateAdd.getModifier());
    assertTrue(stateAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, stateAdd.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(STATE_SYMBOL),
        stateAdd.getCDParameter(0).getMCType());
    assertEquals("symbol", stateAdd.getCDParameter(0).getName());

    ASTCDMethod qualifiedNameAdd = methodList.get(2);
    assertDeepEquals(PUBLIC, qualifiedNameAdd.getModifier());
    assertTrue(qualifiedNameAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, qualifiedNameAdd.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(QUALIFIED_NAME_SYMBOL),
        qualifiedNameAdd.getCDParameter(0).getMCType());
    assertEquals("symbol", qualifiedNameAdd.getCDParameter(0).getName());

    ASTCDMethod unknownAdd = methodList.get(3);
    assertDeepEquals(PUBLIC, unknownAdd.getModifier());
    assertTrue(unknownAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, unknownAdd.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(UNKNOWN_SYMBOL),
        unknownAdd.getCDParameter(0).getMCType());
    assertEquals("symbol", unknownAdd.getCDParameter(0).getName());

    ASTCDMethod fooAdd = methodList.get(4);
    assertDeepEquals(PUBLIC, fooAdd.getModifier());
    assertTrue(fooAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, fooAdd.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(FOO_SYMBOL),
        fooAdd.getCDParameter(0).getMCType());
    assertEquals("symbol", fooAdd.getCDParameter(0).getName());
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
  public void testGetSpanningSymbolMethod() {
    ASTCDMethod method = getMethodBy("getSpanningSymbol", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_SCOPE_SPANNING_SYMBOL, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testisPresentSpanningSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentSpanningSymbol", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetSpanningSymbolMethod() {
    ASTCDMethod method = getMethodBy("setSpanningSymbol", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(I_SCOPE_SPANNING_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("spanningSymbol", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetSpanningSymbolAbsentMethod() {
    ASTCDMethod method = getMethodBy("setSpanningSymbolAbsent", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAddSubScopeAutomatonMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("addSubScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("subScope", method.get().getCDParameter(0).getName());
  }

  @Test
  public void testRemoveSubScopeAutomatonMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("removeSubScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("subScope", method.get().getCDParameter(0).getName());
  }


  @Test
  public void testSetSubScopesAutomatonMethod() {
    ASTCDMethod method = getMethodBy("setSubScopes", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertListOf(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("subScopes", method.getCDParameter(0).getName());
  }


  @Test
  public void testGetSubScopessMethod() {
    ASTCDMethod method = getMethodBy("getSubScopes", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAddSubScopeLexicalsMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("addSubScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_LEXICAS_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_LEXICAS_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("subScope", method.get().getCDParameter(0).getName());
  }

  @Test
  public void testRemoveSubScopeLexicalsMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("removeSubScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_LEXICAS_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_LEXICAS_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("subScope", method.get().getCDParameter(0).getName());
  }

  @Test
  public void testSetEnclosingScopeAutomatonMethod() {

    List<ASTCDMethod> methodList = getMethodsBy("setEnclosingScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.get().getCDParameter(0).getName());
  }

  @Test
  public void testSetEnclosingScopeLexicalsMethod() {

    List<ASTCDMethod> methodList = getMethodsBy("setEnclosingScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_LEXICAS_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_LEXICAS_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("newEnclosingScope", method.get().getCDParameter(0).getName());
  }

  @Test
  public void testIsExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("isExtraAttribute", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("setExtraAttribute", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("extraAttribute", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetFooListMethod() {
    ASTCDMethod method = getMethodBy("getFooList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetFooListMethod() {
    ASTCDMethod method = getMethodBy("setFooList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertListOf(String.class, method.getCDParameter(0).getMCType());
    assertEquals("foo", method.getCDParameter(0).getName());
  }

  @Test
  public void testScopeRuleMethod() {
    ASTCDMethod method = getMethodBy("toString", scopeClass);

    assertTrue(method.getModifier().isPublic());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
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
}
