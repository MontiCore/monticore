/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DelegatorVisitorDecoratorTest extends DecoratorTestCase {

  private CDTypeFacade cdTypeFacade;

  private ASTCDClass visitorClass;

  private GlobalExtensionManagement glex;

  private static final String VISITOR_FULL_NAME = "de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor";

  private static final String AST_TRANSITION = "de.monticore.codegen.ast.automaton._ast.ASTTransition";

  private static final String AST_NODE = "de.monticore.ast.ASTNode";

  private static final String AST_AUTOMATON_NODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));


    DelegatorVisitorDecorator decorator = new DelegatorVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit));
    this.visitorClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonDelegatorVisitor", visitorClass.getName());
  }

  @Test
  public void testAttributesCount() {
    assertEquals(3, visitorClass.sizeCDAttributes());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(1, visitorClass.sizeInterfaces());
  }

  @Test
  public void testInterface() {
    assertDeepEquals("AutomatonInheritanceVisitor", visitorClass.getInterface(0));
  }

  @Test
  public void testRealThisAttribute() {
    ASTCDAttribute automatonVisitor = getAttributeBy("realThis", visitorClass);
    assertTrue(automatonVisitor.isPresentModifier());
    assertDeepEquals(PRIVATE, automatonVisitor.getModifier());
    assertDeepEquals("AutomatonDelegatorVisitor", automatonVisitor.getMCType());
  }

  @Test
  public void testAutomatonVisitorAttribute() {
    ASTCDAttribute automatonVisitor = getAttributeBy("automatonVisitor", visitorClass);
    assertTrue(automatonVisitor.isPresentModifier());
    assertDeepEquals(PRIVATE, automatonVisitor.getModifier());
    assertDeepEquals("Optional<" + VISITOR_FULL_NAME + ">", automatonVisitor.getMCType());
  }

  @Test
  public void testLexicalsVisitorAttribute() {
    ASTCDAttribute automatonVisitor = getAttributeBy("lexicalsVisitor", visitorClass);
    assertTrue(automatonVisitor.isPresentModifier());
    assertDeepEquals(PRIVATE, automatonVisitor.getModifier());
    assertDeepEquals("Optional<de.monticore.codegen.ast.lexicals._visitor.LexicalsVisitor>", automatonVisitor.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(34, visitorClass.sizeCDMethods());
  }

  @Test
  public void testGetRealThis() {
    ASTCDMethod method = getMethodBy("getRealThis", visitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("AutomatonDelegatorVisitor");
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetRealThis() {
    ASTCDMethod method = getMethodBy("setRealThis", visitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(VISITOR_FULL_NAME);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("realThis", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetAutomatonVisitor() {
    ASTCDMethod method = getMethodBy("getAutomatonVisitor", visitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("Optional<"+VISITOR_FULL_NAME+">");
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetAutomatonVisitor() {
    ASTCDMethod method = getMethodBy("setAutomatonVisitor", visitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(VISITOR_FULL_NAME);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("automatonVisitor", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetLexicalsVisitor() {
    ASTCDMethod method = getMethodBy("getLexicalsVisitor", visitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("Optional<de.monticore.codegen.ast.lexicals._visitor.LexicalsVisitor>");
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetLexicalsVisitor() {
    ASTCDMethod method = getMethodBy("setLexicalsVisitor", visitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("de.monticore.codegen.ast.lexicals._visitor.LexicalsVisitor");
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("lexicalsVisitor", method.getCDParameter(0).getName());
  }

  @Test
  public void tesVisitASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesVisitASTName() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("de.monticore.codegen.ast.lexicals._ast.ASTName");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTName() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("de.monticore.codegen.ast.lexicals._ast.ASTName");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTName() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("de.monticore.codegen.ast.lexicals._ast.ASTName");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseASTName() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("de.monticore.codegen.ast.lexicals._ast.ASTName");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesVisitASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_AUTOMATON_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_AUTOMATON_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesVisitASTLexicalsNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("de.monticore.codegen.ast.lexicals._ast.ASTLexicalsNode");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTLexicalsNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("de.monticore.codegen.ast.lexicals._ast.ASTLexicalsNode");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTLexicalsNode() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition("de.monticore.codegen.ast.lexicals._ast.ASTLexicalsNode");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitASTNode() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitASTNode() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorClass);
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_NODE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, visitorClass, visitorClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
