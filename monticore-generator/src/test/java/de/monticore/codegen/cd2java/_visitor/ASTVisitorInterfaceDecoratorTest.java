/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.factories.MCTypeFacade;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTVisitorInterfaceDecoratorTest extends DecoratorTestCase {

  private MCTypeFacade MCTypeFacade;

  private ASTCDInterface visitorInterface;

  private GlobalExtensionManagement glex;

  private static final String VISITOR_FULL_NAME = "de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor";

  private static final String AST_AUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  private static final String AST_NODE = "de.monticore.ast.ASTNode";

  private static final String AST_TRANSITION = "de.monticore.codegen.ast.automaton._ast.ASTTransition";

  private static final String AST_STATE = "de.monticore.codegen.ast.automaton._ast.ASTState";

  private static final String AST_AUTOMATON_NODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  private static final String AUTOMATON_LITERALS = "de.monticore.codegen.ast.automaton._ast.AutomatonLiterals";

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    this.MCTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));

    this.glex.setGlobalValue("genHelper", new DecorationHelper());
    ASTVisitorDecorator decorator = new ASTVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit));
    this.visitorInterface = decorator.decorate(decoratedCompilationUnit);
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonVisitor", visitorInterface.getName());
  }

  @Test
  public void testAttributesEmpty() {
    assertTrue(visitorInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(19, visitorInterface.sizeCDMethods());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(1, visitorInterface.sizeInterfaces());
  }

  @Test
  public void testInterface() {
    assertDeepEquals("de.monticore.codegen.ast.lexicals._visitor.LexicalsVisitor", visitorInterface.getInterface(0));
  }

  @Test
  public void testGetRealThis() {
    ASTCDMethod method = getMethodBy("getRealThis", visitorInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(VISITOR_FULL_NAME);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetRealThis() {
    ASTCDMethod method = getMethodBy("setRealThis", visitorInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(VISITOR_FULL_NAME);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("realThis", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitASTNode() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesVisitASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void tesVisitASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void tesVisitASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesVisitASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_AUTOMATON_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_AUTOMATON_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testNoAutomatonLiteralsVisitorMethods() {
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AUTOMATON_LITERALS);

    List<ASTCDMethod> traversMethodList = getMethodsBy("traverse", 1, visitorInterface);
    assertFalse(traversMethodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));

    List<ASTCDMethod> handleMethodList = getMethodsBy("handle", 1, visitorInterface);
    assertFalse(handleMethodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));

    List<ASTCDMethod> visitMethodList = getMethodsBy("visit", 1, visitorInterface);
    assertFalse(visitMethodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));

    List<ASTCDMethod> endVisitMethodList = getMethodsBy("endVisit", 1, visitorInterface);
    assertFalse(endVisitMethodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, visitorInterface, visitorInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
