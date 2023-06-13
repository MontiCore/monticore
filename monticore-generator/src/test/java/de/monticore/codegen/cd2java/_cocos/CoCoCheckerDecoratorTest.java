/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CoCoCheckerDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass cocoChecker;

  private MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  private static final String COCO_TRAVERSER = "de.monticore.codegen.ast.automaton._visitor.AutomatonTraverser";

  private static final String AUTOMATON_COCO_CHECKER = "de.monticore.codegen.ast.automaton._cocos.AutomatonCoCoChecker";

  private static final String LEXICALS_COCO_CHECKER = "de.monticore.codegen.ast.lexicals._cocos.LexicalsCoCoChecker";

  private static final String AUTOMATON_COCO = "de.monticore.codegen.ast.automaton._cocos.AutomatonASTAutomatonCoCo";

  private static final String AUTOMATON_NODE_COCO = "de.monticore.codegen.ast.automaton._cocos.AutomatonASTAutomatonNodeCoCo";

  private static final String STATE_COCO = "de.monticore.codegen.ast.automaton._cocos.AutomatonASTStateCoCo";

  private static final String TRANSITION_COCO = "de.monticore.codegen.ast.automaton._cocos.AutomatonASTTransitionCoCo";

  private static final String LEXICALS_NODE_COCO = "de.monticore.codegen.ast.lexicals._cocos.LexicalsASTLexicalsNodeCoCo";

  private static final String AUTOMATON_NODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  private static final String LEXICALS_NODE = "de.monticore.codegen.ast.lexicals._ast.ASTLexicalsNode";

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "ast", "Automaton");
    ICD4CodeGlobalScope gs = CD4CodeMill.globalScope();
    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    MethodDecorator methodDecorator = new MethodDecorator(glex, new CoCoService(ast));
    CoCoCheckerDecorator coCoCheckerDecorator = new CoCoCheckerDecorator(glex, methodDecorator, new CoCoService(ast), new VisitorService(ast));
    this.cocoChecker = coCoCheckerDecorator.decorate(ast);
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, cocoChecker, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
    
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonCoCoChecker", cocoChecker.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(1, cocoChecker.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverserAttribute() {
    ASTCDAttribute attribute = getAttributeBy("traverser", cocoChecker);
    assertDeepEquals(PROTECTED, attribute.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertFalse(cocoChecker.getCDConstructorList().isEmpty());
    assertEquals(1, cocoChecker.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor defaultConstructor = cocoChecker.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, defaultConstructor.getModifier());
    assertTrue(defaultConstructor.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(14, cocoChecker.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetTraverserMethod() {
    ASTCDMethod method = getMethodBy("setTraverser", cocoChecker);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(COCO_TRAVERSER);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("traverser", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetTraverserMethod() {
    ASTCDMethod method = getMethodBy("getTraverser", cocoChecker);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(COCO_TRAVERSER);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddCheckerAutomatonCoCoMethod() {
    List<ASTCDMethod> list = getMethodsBy("addChecker", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_COCO_CHECKER);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("checker", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddCheckerLexicalsCoCoMethod() {
    List<ASTCDMethod> list = getMethodsBy("addChecker", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(LEXICALS_COCO_CHECKER);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("checker", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddCoCoAutomatonMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    Assert.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddCoCoAutomatonNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_NODE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddCoCoStateMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(STATE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddCoCoTransitionMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(TRANSITION_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddCoCoLexicalsNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("addCoCo", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(LEXICALS_NODE_COCO);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("coco", method.getCDParameter(0).getName());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCheckAllAutomatonNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("checkAll", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCheckAllLexicalsNodeMethod() {
    List<ASTCDMethod> list = getMethodsBy("checkAll", 1, cocoChecker);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(LEXICALS_NODE);
    assertTrue(list.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = list.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("node", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
