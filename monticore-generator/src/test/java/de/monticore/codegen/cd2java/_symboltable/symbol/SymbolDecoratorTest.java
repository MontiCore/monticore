/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertListOf;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SymbolDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolClassAutomaton;

  private ASTCDClass symbolClassState;

  private ASTCDClass symbolClassFoo;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String ENCLOSING_SCOPE_TYPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope";

  private static final String A_NODE_TYPE_OPT = "Optional<de.monticore.codegen.symboltable.automatonsymbolcd._ast.ASTAutomaton>";

  private static final String A_NODE_TYPE = "de.monticore.codegen.symboltable.automatonsymbolcd._ast.ASTAutomaton";

  private static final String ACCESS_MODIFIER_TYPE = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope";

  private static final String AUTOMATON_TRAVERSER = "de.monticore.codegen.symboltable.automatonsymbolcd._visitor.AutomatonSymbolCDTraverser";

  @Before
  public void setUp() {
    this.mcTypeFacade = MCTypeFacade.getInstance();

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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbol", symbolClassAutomaton.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesCountAutomatonSymbol() {
    assertEquals(2, symbolClassAutomaton.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesAutomatonSymbol() {
    assertDeepEquals("de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.ICommonAutomatonSymbolCDSymbol", symbolClassAutomaton.getCDInterfaceUsage().getInterface(0));
    assertDeepEquals("de.monticore.symboltable.IScopeSpanningSymbol", symbolClassAutomaton.getCDInterfaceUsage().getInterface(1));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(symbolClassAutomaton.isPresentCDExtendUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symbolClassAutomaton.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = symbolClassAutomaton.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbol", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(String.class, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("name", cdConstructor.getCDParameter(0).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(7, symbolClassAutomaton.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("enclosingScope", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE),
        astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTNodeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("astNode", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(A_NODE_TYPE_OPT), astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackageNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("packageName", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAccessModifierAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("accessModifier", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE), astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSpannedScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("spannedScope", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE), astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    assertEquals(20, symbolClassAutomaton.getCDMethodList().size());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodsBy("accept", symbolClassAutomaton).get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(AUTOMATON_TRAVERSER),
        method.getCDParameter(0).getMCType());
    assertEquals("visitor", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAcceptSuperMethod() {
    List<ASTCDMethod> methods = getMethodsBy("accept", 1, symbolClassAutomaton);
    ASTMCType visitorType = mcTypeFacade.createQualifiedType("de.monticore.visitor.ITraverser");

    methods = methods.stream().filter(m -> visitorType.deepEquals(m.getCDParameter(0).getMCType())).collect(Collectors.toList());
    assertEquals(1, methods.size());

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(visitorType, parameter.getMCType());
    assertEquals("visitor", parameter.getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDetermineFullNameMethod() {
    ASTCDMethod method = getMethodBy("determineFullName", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeterminePackageNameMethod() {
    ASTCDMethod method = getMethodBy("determinePackageName", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFullNameMethod() {
    ASTCDMethod method = getMethodBy("getFullName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetEnclosingScopeNameMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetSpannedScopeNameMethod() {
    ASTCDMethod method = getMethodBy("getSpannedScope", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTNodeMethod() {
    ASTCDMethod method = getMethodBy("getAstNode", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(A_NODE_TYPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testisPresentASTNodeMethod() {
    ASTCDMethod method = getMethodBy("isPresentAstNode", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testGetAccessModifierNameMethod() {
    ASTCDMethod method = getMethodBy("getAccessModifier", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetNameMethod() {
    ASTCDMethod method = getMethodBy("setName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetFullNameMethod() {
    ASTCDMethod method = getMethodBy("setFullName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("fullName", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("setPackageName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("packageName", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetASTNodeAbsentMethod() {
    ASTCDMethod method = getMethodBy("setAstNodeAbsent", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCodeAutomaton() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, symbolClassAutomaton, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  //normal Symbol

  @Test
  public void testClassNameStateSymbol() {
    assertEquals("StateSymbol", symbolClassState.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesCountStateSymbol() {
    assertEquals(1, symbolClassState.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesStateSymbol() {
    assertDeepEquals("de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.ICommonAutomatonSymbolCDSymbol", symbolClassState.getCDInterfaceUsage().getInterface(0));
    
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCountStateSymbol() {
    assertEquals(6, symbolClassState.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test(expected = AssertionError.class)
  public void testSpannedScopeAttributeStateSymbol() {
    getAttributeBy("spannedScope", symbolClassState);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodsStateSymbol() {
    assertEquals(18, symbolClassState.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test(expected = AssertionError.class)
  public void testGetSpannedScopeMethodStateSymbol() {
    getMethodBy("getSpannedScope", symbolClassState);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test(expected = AssertionError.class)
  public void testSetSpannedScopeMethodStateSymbol() {
    getMethodBy("setSpannedScope", symbolClassState);
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testFooSymbolSuperClass() {
    assertTrue(symbolClassFoo.isPresentCDExtendUsage());
    assertDeepEquals("NotASymbol", symbolClassFoo.getCDExtendUsage().getSuperclass(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFooSymbolInterfaces() {
    assertEquals(2, symbolClassFoo.getInterfaceList().size());
    assertDeepEquals("de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.ICommonAutomatonSymbolCDSymbol",
        symbolClassFoo.getCDInterfaceUsage().getInterface(0));
    assertDeepEquals("de.monticore.codegen.ast.Lexicals.ASTLexicalsNode", symbolClassFoo.getCDInterfaceUsage().getInterface(1));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("isExtraAttribute", symbolClassFoo);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("setExtraAttribute", symbolClassFoo);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("extraAttribute", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFooListMethod() {
    ASTCDMethod method = getMethodBy("getFooList", symbolClassFoo);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetFooListMethod() {
    ASTCDMethod method = getMethodBy("setFooList", symbolClassFoo);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertListOf(String.class, method.getCDParameter(0).getMCType());
    assertEquals("foo", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeRuleMethod() {
    ASTCDMethod method = getMethodBy("toString", symbolClassFoo);

    assertTrue(method.getModifier().isPublic());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, symbolClassState, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
