package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class CoCoCheckerDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass cocoChecker;

  private CDTypeFactory cdTypeFactory = CDTypeFactory.getInstance();

  private static final String COCO_CHECKER = "de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor";

  private static final String AUTOMATON_COCO_CHECKER = "de.monticore.codegen.ast.automaton._coco.AutomatonCoCoChecker";

  private static final String LEXICALS_COCO_CHECKER = "de.monticore.codegen.ast.lexicals._coco.LexicalsCoCoChecker";

  private static final String AUTOMATON_COCO = "de.monticore.codegen.ast.automaton._coco.AutomatonASTASTAutomatonCoCo";

  private static final String AUTOMATON_NODE_COCO = "de.monticore.codegen.ast.automaton._coco.AutomatonASTAutomatonNodeCoCo";

  private static final String STATE_COCO = "de.monticore.codegen.ast.automaton._coco.AutomatonASTASTStateCoCo";

  private static final String TRANSITION_COCO = "de.monticore.codegen.ast.automaton._coco.AutomatonASTASTTransitionCoCo";

  private static final String LEXICALS_NODE_COCO = "de.monticore.codegen.ast.lexicals._coco.LexicalsASTASTLexicalsNodeCoCo";

  private static final String AUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTASTAutomaton";

  private static final String AUTOMATON_NODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  private static final String STATE = "de.monticore.codegen.ast.automaton._ast.ASTASTState";

  private static final String TRANSITION = "de.monticore.codegen.ast.automaton._ast.ASTASTTransition";

  private static final String LEXICALS_NODE = "de.monticore.codegen.ast.lexicals._ast.ASTLexicalsNode";

  @Before
  public void setup() {
    LogStub.init();
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "ast", "Automaton");
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    CoCoCheckerDecorator coCoCheckerDecorator = new CoCoCheckerDecorator(glex, methodDecorator, new CoCoService(ast), new VisitorService(ast));
    this.cocoChecker = coCoCheckerDecorator.decorate(ast);
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, cocoChecker, cocoChecker);
    System.out.println(sb.toString());
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonCoCoChecker", cocoChecker.getName());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(8, cocoChecker.getCDAttributeList().size());
  }

  @Test
  public void testRealThisAttribute() { // schlaegt fehl, da das realThis-Attribut noch protected ist
    ASTCDAttribute attribute = getAttributeBy("realThis", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testAutomatonCoCoCheckerAttribute() {
    //TODO test type
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__coco_AutomatonCoCoChecker", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testLexicalsCoCoCheckerAttribute() {
    //TODO test type
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_lexicals__coco_LexicalsCoCoChecker", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testAutomatonNodeCoCosAttribute() {
    //TODO test type
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__ast_ASTAutomatonNodeCoCos", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testStateCoCosAttribute() {
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__ast_ASTASTStateCoCos", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testTransitionCoCosAttribute() {
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__ast_ASTASTTransitionCoCos", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testAutomatonCoCosAttribute() {
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__ast_ASTASTAutomatonCoCos", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testConstructorCount() {
    assertFalse(cocoChecker.getCDConstructorList().isEmpty());
    assertEquals(1, cocoChecker.getCDConstructorList().size());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor defaultConstructor = cocoChecker.getCDConstructor(0);
    assertDeepEquals(PUBLIC, defaultConstructor.getModifier());
    assertTrue(defaultConstructor.isEmptyCDParameters());
  }

  @Test
  public void testMethodCount() {
    assertEquals(18, cocoChecker.getCDMethodList().size());
  }

  @Test
  public void testSetRealThisMethod() {
    ASTCDMethod method = getMethodBy("setRealThis", cocoChecker);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(COCO_CHECKER);
    assertVoid(method.getReturnType());
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("realThis", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetRealThisMethod() {
    ASTCDMethod method = getMethodBy("getRealThis", cocoChecker);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(COCO_CHECKER);
    assertDeepEquals(astType, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAddCheckerAutomatonCoCoMethod() {
    List<ASTCDMethod> list = getMethodsBy("addChecker", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(AUTOMATON_COCO_CHECKER);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("checker", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCheckerLexicalsCoCoMethod() {
    List<ASTCDMethod> list = getMethodsBy("addChecker", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(LEXICALS_COCO_CHECKER);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("checker", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoAutomatonMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(AUTOMATON_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoAutomatonNodeMethod() { // schlaegt fehl, da das Attribut vom Typ ASTAutomatonNode noch fehlt
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(AUTOMATON_NODE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoStateMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(STATE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoTransitionMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(TRANSITION_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoLexicalsNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(LEXICALS_NODE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitAutomatonMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(AUTOMATON);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitAutomatonNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(AUTOMATON_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitStateMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(STATE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitTransitionMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(TRANSITION);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitLexicalsNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(LEXICALS_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testCheckAllAutomatonNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("checkAll", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(AUTOMATON_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testCheckAllLexicalsNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("checkAll", 1, cocoChecker);
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(LEXICALS_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

}
