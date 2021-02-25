/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scopesgenitor;

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
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static org.junit.Assert.*;

public class ScopesGenitorDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopesGenitorClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private MCTypeFacade mcTypeFacade;

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol";

  private static final String STATE_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol";

  private static final String AUTOMATON_VISITOR = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonVisitor2";

  private static final String AUTOMATON_HANDLER = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonHandler";

  private static final String AUTOMATON_TRAVERSER = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonTraverser";

  private static final String AST_AUTOMATON = "de.monticore.codegen.symboltable.automaton._ast.ASTAutomaton";

  private static final String AST_INHERITED_SYMBOL = "de.monticore.codegen.symboltable.automaton._ast.ASTInheritedSymbolClass";

  private static final String INHERITED_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.SymbolInterfaceSymbol";

  private static final String AST_STATE = "de.monticore.codegen.symboltable.automaton._ast.ASTState";

  private static final String AST_TRANSITION = "de.monticore.codegen.symboltable.automaton._ast.ASTTransition";

  private static final String AST_SCOPE = "de.monticore.codegen.symboltable.automaton._ast.ASTScope";

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    this.glex = new GlobalExtensionManagement();
    this.mcTypeFacade = MCTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ScopesGenitorDecorator decorator = new ScopesGenitorDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));

    //creates normal Symbol
    Optional<ASTCDClass> optScopeSkeletonCreator = decorator.decorate(decoratedCompilationUnit);
    assertTrue(optScopeSkeletonCreator.isPresent());
    this.scopesGenitorClass = optScopeSkeletonCreator.get();
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonScopesGenitor", scopesGenitorClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, scopesGenitorClass.getInterfaceList().size());
  }

  @Test
  public void testSuperInterface() {
    assertDeepEquals(AUTOMATON_VISITOR, scopesGenitorClass.getCDInterfaceUsage().getInterface(0));
    assertDeepEquals(AUTOMATON_HANDLER, scopesGenitorClass.getCDInterfaceUsage().getInterface(1));
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(scopesGenitorClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(3, scopesGenitorClass.getCDConstructorList().size());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = scopesGenitorClass.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScopesGenitor", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());


    assertTrue(cdConstructor.getCDThrowsDeclaration().isEmptyException());
  }


  @Test
  public void testConstructorWithEnclosingScope() {
    ASTCDConstructor cdConstructor = scopesGenitorClass.getCDConstructorList().get(1);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScopesGenitor", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());

    assertDeepEquals("Deque<? extends " + I_AUTOMATON_SCOPE + ">", cdConstructor.getCDParameter(0).getMCType());
    assertEquals("scopeStack", cdConstructor.getCDParameter(0).getName());

    assertTrue(cdConstructor.getCDThrowsDeclaration().isEmptyException());
  }

  @Test
  public void testZeroArgsConstructor(){
    ASTCDConstructor constructor = scopesGenitorClass.getCDConstructorList().get(2);
    assertDeepEquals(PUBLIC, constructor.getModifier());
    assertEquals("AutomatonScopesGenitor", constructor.getName());
    assertTrue(constructor.isEmptyCDParameters());
    assertTrue(constructor.getCDThrowsDeclaration().isEmptyException());
  }


  @Test
  public void testAttributeSize() {
    assertEquals(3, scopesGenitorClass.getCDAttributeList().size());
  }

  @Test
  public void testScopeStackAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("scopeStack", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Deque<de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope>", astcdAttribute.getMCType());
  }

  @Test
  public void testTraverserAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("traverser", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_TRAVERSER , astcdAttribute.getMCType());
  }

  @Test
  public void testFirstCreatedScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("firstCreatedScope", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(35, scopesGenitorClass.getCDMethodList().size());
  }

  @Test
  public void testCreateFromASTMethod() {
    ASTCDMethod method = getMethodBy("createFromAST", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
    assertEquals("rootNode", method.getCDParameter(0).getName());
  }


  @Test
  public void testGetFirstCreatedScopeMethod() {
    ASTCDMethod method = getMethodBy("getFirstCreatedScope", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testPutOnStackMethod() {
    ASTCDMethod method = getMethodBy("putOnStack", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());
  }


  @Test
  public void testGetTraverserMethod() {
    ASTCDMethod method = getMethodBy("getTraverser", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_TRAVERSER, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetTraverserMethod() {
    ASTCDMethod method = getMethodBy("setTraverser", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(AUTOMATON_TRAVERSER, method.getCDParameter(0).getMCType());
    assertEquals("traverser", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetAutomatonScopeStack() {
    ASTCDMethod method = getMethodBy("setAutomatonScopeStack", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("Deque<" + I_AUTOMATON_SCOPE + ">", method.getCDParameter(0).getMCType());
    assertEquals("scopeStack", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetCurrentScopeThis() {
    ASTCDMethod method = getMethodBy("getCurrentScope", scopesGenitorClass);
    assertDeepEquals(PUBLIC_FINAL, method.getModifier());
    ASTMCType astType = this.mcTypeFacade.createOptionalTypeOf(I_AUTOMATON_SCOPE);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testRemoveCurrentScopeThis() {
    ASTCDMethod method = getMethodBy("removeCurrentScope", scopesGenitorClass);
    assertDeepEquals(PUBLIC_FINAL, method.getModifier());
    ASTMCType astType = this.mcTypeFacade.createOptionalTypeOf(I_AUTOMATON_SCOPE);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCreateScopeThis() {
    ASTCDMethod method = getMethodBy("createScope", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("shadowing", method.getCDParameter(0).getName());
  }

  @Test
  public void testEndVisitASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testEndVisitASTStateNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitASTStateNode() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testCreate_ASTAutomatonNode() {
    ASTCDMethod method = getMethodBy("create_Automaton", scopesGenitorClass);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_SYMBOL + BUILDER_SUFFIX, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());

    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
    assertEquals("ast", method.getCDParameter(0).getName());
  }

  @Test
  public void testCreate_ASTStateNode() {
    ASTCDMethod method = getMethodBy("create_State", scopesGenitorClass);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(STATE_SYMBOL + BUILDER_SUFFIX, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());

    assertDeepEquals(AST_STATE, method.getCDParameter(0).getMCType());
    assertEquals("ast", method.getCDParameter(0).getName());
  }

  @Test
  public void testCreate_ASTInheritedSymbolClass() {
    ASTCDMethod method = getMethodBy("create_InheritedSymbolClass", scopesGenitorClass);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(INHERITED_SYMBOL + BUILDER_SUFFIX, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());

    assertDeepEquals(AST_INHERITED_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("ast", method.getCDParameter(0).getName());
  }

  @Test
  public void testEndVisitASTInheritedSymbolClassNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_INHERITED_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitASTInheritedSymbolClassNode() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_INHERITED_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testAddToScopeAndLinkWithNodeAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("addToScopeAndLinkWithNode", 2, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("symbol", method.getCDParameter(0).getName());

    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(1).getName());
  }

  @Test
  public void testAddToScopeAndLinkWithNodeStateNode() {
    List<ASTCDMethod> methodList = getMethodsBy("addToScopeAndLinkWithNode", 2, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(STATE_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(STATE_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("symbol", method.getCDParameter(0).getName());

    assertDeepEquals(AST_STATE, method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(1).getName());
  }

  @Test
  public void testSetLinkBetweenSymbolAndNodeAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("setLinkBetweenSymbolAndNode", 2, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("symbol", method.getCDParameter(0).getName());

    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(1).getName());
  }

  @Test
  public void testSetLinkBetweenSymbolAndNodeStateNode() {
    List<ASTCDMethod> methodList = getMethodsBy("setLinkBetweenSymbolAndNode", 2, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(STATE_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(STATE_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("symbol", method.getCDParameter(0).getName());

    assertDeepEquals(AST_STATE, method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(1).getName());
  }

  @Test
  public void testSetLinkBetweenSymbolAndNodeInheritedSymbolClassNode() {
    List<ASTCDMethod> methodList = getMethodsBy("setLinkBetweenSymbolAndNode", 2, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(INHERITED_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(INHERITED_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("symbol", method.getCDParameter(0).getName());

    assertDeepEquals(AST_INHERITED_SYMBOL, method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(1).getName());
  }

  @Test
  public void testSetLinkBetweenSpannedScopeAndNodeAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("setLinkBetweenSpannedScopeAndNode", 2, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(1).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(1).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(1).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());

    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(1).getName());
  }


  @Test
  public void testVisitASTTransitionNode() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitASTScopeNode() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testSetLinkBetweenSpannedScopeAndNodeScopeNode() {
    List<ASTCDMethod> methodList = getMethodsBy("setLinkBetweenSpannedScopeAndNode", 2, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_SCOPE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(1).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(1).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(1).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());

    assertDeepEquals(AST_SCOPE, method.getCDParameter(1).getMCType());
    assertEquals("ast", method.getCDParameter(1).getName());
  }


  @Test
  public void testCreate_ASTScopeNode() {
    ASTCDMethod method = getMethodBy("create_Scope", scopesGenitorClass);
    assertDeepEquals(PROTECTED, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());

    assertDeepEquals(AST_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("ast", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddToScopeAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("addToScope", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testAddToScopeStateNode() {
    List<ASTCDMethod> methodList = getMethodsBy("addToScope", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(STATE_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testAddToScopeQualifiedNameNode() {
    List<ASTCDMethod> methodList = getMethodsBy("addToScope", 1, scopesGenitorClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(QUALIFIED_NAME_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testAddToScopeStackMethod(){
    ASTCDMethod method = getMethodBy("addToScopeStack", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopesGenitorClass, scopesGenitorClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  @Test
  public void testNoStartProd() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    GlobalExtensionManagement glex = new GlobalExtensionManagement();

    glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    glex.setGlobalValue("service", new AbstractService(cd));

    SymbolTableService mockService = Mockito.spy(new SymbolTableService(cd));
    Mockito.doReturn(Optional.empty()).when(mockService).getStartProdASTFullName(Mockito.any(ASTCDDefinition.class));

    ScopesGenitorDecorator decorator = new ScopesGenitorDecorator(glex,
        mockService, new VisitorService(cd), new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));

    //create non present SymbolTableCreator
    Optional<ASTCDClass> optScopeSkeletonCreator = decorator.decorate(cd);
    assertFalse(optScopeSkeletonCreator.isPresent());
  }
}
