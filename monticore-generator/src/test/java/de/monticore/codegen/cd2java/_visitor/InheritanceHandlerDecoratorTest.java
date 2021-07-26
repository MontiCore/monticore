/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InheritanceHandlerDecoratorTest extends DecoratorTestCase {

  private static final String AUTOMATON_TRAVERSER = "de.monticore.codegen.ast.automaton._visitor.AutomatonTraverser";

  private MCTypeFacade mcTypeFacade;

  private ASTCDClass handlerClass;

  private GlobalExtensionManagement glex;

  private static final String AST_AUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  private static final String AST_TRANSITION = "de.monticore.codegen.ast.automaton._ast.ASTTransition";

  private static final String AST_STATE = "de.monticore.codegen.ast.automaton._ast.ASTState";

  private static final String AST_AUTOMATON_NODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    this.mcTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);

    InheritanceHandlerDecorator decorator = new InheritanceHandlerDecorator(this.glex, methodDecorator,
        visitorService, symbolTableService);
    this.handlerClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonInheritanceHandler", handlerClass.getName());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(1, handlerClass.getCDAttributeList().size());
  }

  @Test
  public void testMethodCount() {
    assertEquals(11, handlerClass.getCDMethodList().size());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(1, handlerClass.getInterfaceList().size());
  }

  @Test
  public void testInterface() {
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonHandler", handlerClass.getCDInterfaceUsage().getInterface(0));
  }

  @Test
  public void tesHandleASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, handlerClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverserAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("traverser", handlerClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_TRAVERSER, astcdAttribute.getMCType());
  }

  @Test
  public void tesHandleASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, handlerClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void tesHandleASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, handlerClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void tesHandleASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, handlerClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_AUTOMATON_NODE);
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
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, handlerClass, handlerClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  /**
   * for a AST with a super class
   */
  @Test
  public void testSuperClassHandleMethods() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    GlobalExtensionManagement glex = new GlobalExtensionManagement();

    ASTCDCompilationUnit decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "_ast_emf", "Automata");

    glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);

    InheritanceHandlerDecorator decorator = new InheritanceHandlerDecorator(this.glex, methodDecorator,
        visitorService, symbolTableService);
    ASTCDClass handlerClass = decorator.decorate(decoratedCompilationUnit);

    assertEquals(13, handlerClass.getCDMethodList().size());
    List<ASTCDMethod> handleMethods = getMethodsBy("handle", 1, handlerClass);

    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen._ast_emf.automata._ast.ASTTransitionWithAction");
    assertTrue(handleMethods.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, handleMethods.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = handleMethods.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }
}
