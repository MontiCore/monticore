/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CoCoCheckerDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass cocoChecker;

  private CDTypeFacade cdTypeFacade = CDTypeFacade.getInstance();

  private static final String COCO_CHECKER = "de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor";

  private static final String AUTOMATON_COCO_CHECKER = "de.monticore.codegen.ast.automaton._cocos.AutomatonCoCoChecker";

  private static final String LEXICALS_COCO_CHECKER = "de.monticore.codegen.ast.lexicals._cocos.LexicalsCoCoChecker";

  private static final String AUTOMATON_COCO = "de.monticore.codegen.ast.automaton._cocos.AutomatonASTASTAutomatonCoCo";

  private static final String AUTOMATON_NODE_COCO = "de.monticore.codegen.ast.automaton._cocos.AutomatonASTAutomatonNodeCoCo";

  private static final String STATE_COCO = "de.monticore.codegen.ast.automaton._cocos.AutomatonASTASTStateCoCo";

  private static final String TRANSITION_COCO = "de.monticore.codegen.ast.automaton._cocos.AutomatonASTASTTransitionCoCo";

  private static final String LEXICALS_NODE_COCO = "de.monticore.codegen.ast.lexicals._cocos.LexicalsASTASTLexicalsNodeCoCo";

  private static final String AUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTASTAutomaton";

  private static final String AUTOMATON_NODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  private static final String STATE = "de.monticore.codegen.ast.automaton._ast.ASTASTState";

  private static final String TRANSITION = "de.monticore.codegen.ast.automaton._ast.ASTASTTransition";

  private static final String LEXICALS_NODE = "de.monticore.codegen.ast.lexicals._ast.ASTLexicalsNode";

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "ast", "Automaton");
    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

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
    // TODO Check System.out.println(sb.toString());
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
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__cocos_AutomatonCoCoChecker", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testLexicalsCoCoCheckerAttribute() {
    //TODO test type
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_lexicals__cocos_LexicalsCoCoChecker", cocoChecker);
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
    assertEquals(20, cocoChecker.getCDMethodList().size());
  }

  @Test
  public void testSetRealThisMethod() {
    ASTCDMethod method = getMethodBy("setRealThis", cocoChecker);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(COCO_CHECKER);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("realThis", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetRealThisMethod() {
    ASTCDMethod method = getMethodBy("getRealThis", cocoChecker);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(COCO_CHECKER);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAddCheckerAutomatonCoCoMethod() {
    List<ASTCDMethod> list = getMethodsBy("addChecker", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(AUTOMATON_COCO_CHECKER);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("checker", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCheckerLexicalsCoCoMethod() {
    List<ASTCDMethod> list = getMethodsBy("addChecker", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(LEXICALS_COCO_CHECKER);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("checker", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoAutomatonMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(AUTOMATON_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoAutomatonNodeMethod() { // schlaegt fehl, da das Attribut vom Typ ASTAutomatonNode noch fehlt
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(AUTOMATON_NODE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoStateMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(STATE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoTransitionMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(TRANSITION_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddCoCoLexicalsNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(LEXICALS_NODE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitAutomatonMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(AUTOMATON);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitAutomatonNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(AUTOMATON_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitStateMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(STATE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitTransitionMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(TRANSITION);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitLexicalsNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("visit", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(LEXICALS_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testCheckAllAutomatonNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("checkAll", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(AUTOMATON_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

  @Test
  public void testCheckAllLexicalsNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("checkAll", 1, cocoChecker);
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(LEXICALS_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  }

}
