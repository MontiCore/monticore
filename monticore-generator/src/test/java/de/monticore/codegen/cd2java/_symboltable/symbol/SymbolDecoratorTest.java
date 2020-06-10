/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
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
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class SymbolDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolClassAutomaton;

  private ASTCDClass symbolClassState;

  private ASTCDClass symbolClassFoo;

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String ENCLOSING_SCOPE_TYPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope";

  private static final String A_NODE_TYPE_OPT = "Optional<de.monticore.codegen.symboltable.automatonsymbolcd._ast.ASTAutomaton>";

  private static final String A_NODE_TYPE = "de.monticore.codegen.symboltable.automatonsymbolcd._ast.ASTAutomaton";

  private static final String ACCESS_MODIFIER_TYPE = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope";

  private static final String AUTOMATON_VISITOR = "de.monticore.codegen.symboltable.automatonsymbolcd._visitor.AutomatonSymbolCDVisitor";

  @Before
  public void setUp() {
    Log.init();
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));


    SymbolDecorator decorator = new SymbolDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));
    //creates ScopeSpanningSymbol
    ASTCDClass automatonClass = getClassBy("Automaton", decoratedCompilationUnit);
    this.symbolClassAutomaton = decorator.decorate(automatonClass);

    //creates normal Symbol with symbolRules
    ASTCDClass fooClass = getClassBy("Foo", decoratedCompilationUnit);
    this.symbolClassFoo = decorator.decorate(fooClass);

    //creates normal Symbol with top class
    SymbolDecorator mockDecorator = Mockito.spy(new SymbolDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit))));
    Mockito.doReturn(true).when(mockDecorator).isSymbolTop();
    ASTCDClass stateClass = getClassBy("State", decoratedCompilationUnit);
    this.symbolClassState = mockDecorator.decorate(stateClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbol", symbolClassAutomaton.getName());
  }

  @Test
  public void testSuperInterfacesCountAutomatonSymbol() {
    assertEquals(2, symbolClassAutomaton.sizeInterfaces());
  }

  @Test
  public void testSuperInterfacesAutomatonSymbol() {
    assertDeepEquals("de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.ICommonAutomatonSymbolCDSymbol", symbolClassAutomaton.getInterface(0));
    assertDeepEquals("de.monticore.symboltable.IScopeSpanningSymbol", symbolClassAutomaton.getInterface(1));
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(symbolClassAutomaton.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symbolClassAutomaton.sizeCDConstructors());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = symbolClassAutomaton.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbol", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(String.class, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("name", cdConstructor.getCDParameter(0).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(7, symbolClassAutomaton.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("name", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testFullNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fullName", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("enclosingScope", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE),
        astcdAttribute.getMCType());
  }

  @Test
  public void testASTNodeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("astNode", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(A_NODE_TYPE_OPT), astcdAttribute.getMCType());
  }

  @Test
  public void testPackageNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("packageName", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testAccessModifierAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("accessModifier", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE), astcdAttribute.getMCType());
  }

  @Test
  public void testSpannedScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("spannedScope", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE), astcdAttribute.getMCType());
  }

  @Test
  public void testSymbolRuleAttributes() {
    ASTCDAttribute fooAttribute = getAttributeBy("foo", symbolClassFoo);
    assertDeepEquals(PROTECTED, fooAttribute.getModifier());
    assertListOf(String.class, fooAttribute.getMCType());

    ASTCDAttribute blaAttribute = getAttributeBy("bla", symbolClassFoo);
    assertDeepEquals(PROTECTED, blaAttribute.getModifier());
    assertOptionalOf(Integer.class, blaAttribute.getMCType());

    ASTCDAttribute extraAtt = getAttributeBy("extraAttribute", symbolClassFoo);
    assertDeepEquals(PROTECTED, extraAtt.getModifier());
    assertBoolean(extraAtt.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(19, symbolClassAutomaton.getCDMethodList().size());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodBy("accept", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_VISITOR),
        method.getCDParameter(0).getMCType());
    assertEquals("visitor", method.getCDParameter(0).getName());
  }

  @Test
  public void testDetermineFullNameMethod() {
    ASTCDMethod method = getMethodBy("determineFullName", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testDeterminePackageNameMethod() {
    ASTCDMethod method = getMethodBy("determinePackageName", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetFullNameMethod() {
    ASTCDMethod method = getMethodBy("getFullName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetEnclosingScopeNameMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetSpannedScopeNameMethod() {
    ASTCDMethod method = getMethodBy("getSpannedScope", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTNodeMethod() {
    ASTCDMethod method = getMethodBy("getAstNode", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(A_NODE_TYPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testisPresentASTNodeMethod() {
    ASTCDMethod method = getMethodBy("isPresentAstNode", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGetAccessModifierNameMethod() {
    ASTCDMethod method = getMethodBy("getAccessModifier", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetNameMethod() {
    ASTCDMethod method = getMethodBy("setName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetFullNameMethod() {
    ASTCDMethod method = getMethodBy("setFullName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("fullName", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("setPackageName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("packageName", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetASTNodeMethod() {
    ASTCDMethod method = getMethodBy("setAstNode", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(A_NODE_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("astNode", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetASTNodeAbsentMethod() {
    ASTCDMethod method = getMethodBy("setAstNodeAbsent", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetAccessModifierMethod() {
    ASTCDMethod method = getMethodBy("setAccessModifier", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("accessModifier", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetSpannedScopeMethod() {
    ASTCDMethod method = getMethodBy("setSpannedScope", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCodeAutomaton() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symbolClassAutomaton, symbolClassAutomaton);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  //normal Symbol

  @Test
  public void testClassNameStateSymbol() {
    assertEquals("StateSymbol", symbolClassState.getName());
  }

  @Test
  public void testSuperInterfacesCountStateSymbol() {
    assertEquals(1, symbolClassState.sizeInterfaces());
  }

  @Test
  public void testSuperInterfacesStateSymbol() {
    assertDeepEquals("de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.ICommonAutomatonSymbolCDSymbol", symbolClassState.getInterface(0));
  }

  @Test
  public void testAttributeCountStateSymbol() {
    assertEquals(6, symbolClassState.sizeCDAttributes());
  }

  @Test(expected = AssertionError.class)
  public void testSpannedScopeAttributeStateSymbol() {
    getAttributeBy("spannedScope", symbolClassState);
  }

  @Test
  public void testMethodsStateSymbol() {
    assertEquals(17, symbolClassState.getCDMethodList().size());
  }

  @Test(expected = AssertionError.class)
  public void testGetSpannedScopeMethodStateSymbol() {
    getMethodBy("getSpannedScope", symbolClassState);
  }

  @Test(expected = AssertionError.class)
  public void testSetSpannedScopeMethodStateSymbol() {
    getMethodBy("setSpannedScope", symbolClassState);
  }


  @Test
  public void testFooSymbolSuperClass() {
    assertTrue(symbolClassFoo.isPresentSuperclass());
    assertDeepEquals("de.monticore.Foo2", symbolClassFoo.getSuperclass());
  }

  @Test
  public void testFooSymbolInterfaces() {
    assertEquals(2, symbolClassFoo.sizeInterfaces());
    assertDeepEquals("de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.ICommonAutomatonSymbolCDSymbol",
        symbolClassFoo.getInterface(0));
    assertDeepEquals("de.monticore.Foo3", symbolClassFoo.getInterface(1));

  }

  @Test
  public void testIsExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("isExtraAttribute", symbolClassFoo);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("setExtraAttribute", symbolClassFoo);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("extraAttribute", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetFooListMethod() {
    ASTCDMethod method = getMethodBy("getFooList", symbolClassFoo);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetFooListMethod() {
    ASTCDMethod method = getMethodBy("setFooList", symbolClassFoo);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertListOf(String.class, method.getCDParameter(0).getMCType());
    assertEquals("foo", method.getCDParameter(0).getName());
  }

  @Test
  public void testScopeRuleMethod() {
    ASTCDMethod method = getMethodBy("toString", symbolClassFoo);

    assertTrue(method.getModifier().isPublic());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symbolClassState, symbolClassState);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
