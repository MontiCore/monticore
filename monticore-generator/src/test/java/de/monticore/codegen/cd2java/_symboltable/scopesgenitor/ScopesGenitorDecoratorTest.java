/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scopesgenitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractService;
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
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class ScopesGenitorDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopesGenitorClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private MCTypeFacade mcTypeFacade;

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String I_AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope";

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

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonScopesGenitor", scopesGenitorClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, scopesGenitorClass.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterface() {
    assertDeepEquals(AUTOMATON_VISITOR, scopesGenitorClass.getCDInterfaceUsage().getInterface(0));
    assertDeepEquals(AUTOMATON_HANDLER, scopesGenitorClass.getCDInterfaceUsage().getInterface(1));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(scopesGenitorClass.isPresentCDExtendUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, scopesGenitorClass.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testZeroArgsConstructor(){
    ASTCDConstructor constructor = scopesGenitorClass.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, constructor.getModifier());
    assertEquals("AutomatonScopesGenitor", constructor.getName());
    assertTrue(constructor.isEmptyCDParameters());
    assertFalse(constructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testAttributeSize() {
    assertEquals(3, scopesGenitorClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeStackAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("scopeStack", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Deque<de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope>", astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverserAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("traverser", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_TRAVERSER , astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFirstCreatedScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("firstCreatedScope", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    assertEquals(29, scopesGenitorClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateFromASTMethod() {
    ASTCDMethod method = getMethodBy("createFromAST", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
    assertEquals("rootNode", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testGetFirstCreatedScopeMethod() {
    ASTCDMethod method = getMethodBy("getFirstCreatedScope", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPutOnStackMethod() {
    ASTCDMethod method = getMethodBy("putOnStack", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testGetTraverserMethod() {
    ASTCDMethod method = getMethodBy("getTraverser", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_TRAVERSER, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetTraverserMethod() {
    ASTCDMethod method = getMethodBy("setTraverser", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(AUTOMATON_TRAVERSER, method.getCDParameter(0).getMCType());
    assertEquals("traverser", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetScopeStack() {
    ASTCDMethod method = getMethodBy("setScopeStack", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("Deque<? extends " + I_AUTOMATON_SCOPE + ">", method.getCDParameter(0).getMCType());
    assertEquals("scopeStack", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetCurrentScopeThis() {
    ASTCDMethod method = getMethodBy("getCurrentScope", scopesGenitorClass);
    assertDeepEquals(PUBLIC_FINAL, method.getModifier());
    ASTMCType astType = this.mcTypeFacade.createOptionalTypeOf(I_AUTOMATON_SCOPE);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRemoveCurrentScopeThis() {
    ASTCDMethod method = getMethodBy("removeCurrentScope", scopesGenitorClass);
    assertDeepEquals(PUBLIC_FINAL, method.getModifier());
    ASTMCType astType = this.mcTypeFacade.createOptionalTypeOf(I_AUTOMATON_SCOPE);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddToScopeStackMethod(){
    ASTCDMethod method = getMethodBy("addToScopeStack", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitScopeHP1Method(){
    ASTCDMethod method = getMethodBy("initScopeHP1", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitScopeHP2Method(){
    ASTCDMethod method = getMethodBy("initScopeHP2", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitArtifactScopeHP1Method(){
    ASTCDMethod method = getMethodBy("initArtifactScopeHP1", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_ARTIFACT_SCOPE, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitArtifactScopeHP2Method(){
    ASTCDMethod method = getMethodBy("initArtifactScopeHP2", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("scope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_ARTIFACT_SCOPE, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitStateHP1Method(){
    ASTCDMethod method = getMethodBy("initStateHP1", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("symbol", method.getCDParameter(0).getName());
    assertDeepEquals(STATE_SYMBOL, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitStateHP2Method(){
    ASTCDMethod method = getMethodBy("initStateHP2", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("symbol", method.getCDParameter(0).getName());
    assertDeepEquals(STATE_SYMBOL, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitAutomatonHP1Method(){
    ASTCDMethod method = getMethodBy("initAutomatonHP1", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("symbol", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInitAutomatonHP2Method(){
    ASTCDMethod method = getMethodBy("initAutomatonHP2", scopesGenitorClass);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("symbol", method.getCDParameter(0).getName());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, scopesGenitorClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
