package de.monticore.codegen.cd2java._visitor;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.visitor_interface.ASTVisitorDecorator;
import de.monticore.codegen.cd2java._visitor.visitor_interface.VisitorInterfaceDecorator;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertVoid;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ASTVisitorInterfaceDecoratorTest extends DecoratorTestCase {

  private CDTypeFacade cdTypeFacade;

  private ASTCDInterface visitorInterface;

  private GlobalExtensionManagement glex;

  private static final String VISITOR_FULL_NAME = "de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor";

  private static final String AST_AUTOMATON = "automaton._ast.ASTAutomaton";

  private static final String AST_NODE = "de.monticore.ast.ASTNode";

  private static final String AST_TRANSITION = "automaton._ast.ASTTransition";

  private static final String AST_STATE = "automaton._ast.ASTState";

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    this.glex.setGlobalValue("genHelper", new DecorationHelper());
    ASTVisitorDecorator decorator = new ASTVisitorDecorator(this.glex,
        new VisitorInterfaceDecorator(this.glex, new VisitorService(decoratedCompilationUnit)),
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
  public void testGetRealThis() {
    ASTCDMethod method = getMethodBy("getRealThis", visitorInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(VISITOR_FULL_NAME);
    assertDeepEquals(astType, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetRealThis() {
    ASTCDMethod method = getMethodBy("setRealThis", visitorInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(VISITOR_FULL_NAME);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("realThis", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitASTNode() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void testEndVisitASTNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void tesVisitASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void testEndVisitASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void tesHandleASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void testTraverseASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }


  @Test
  public void tesVisitASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void testEndVisitASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void tesHandleASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void testTraverseASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }


  @Test
  public void tesVisitASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void testEndVisitASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void tesHandleASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void testTraverseASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, visitorInterface, visitorInterface);
    System.out.println(sb.toString());
  }
}
