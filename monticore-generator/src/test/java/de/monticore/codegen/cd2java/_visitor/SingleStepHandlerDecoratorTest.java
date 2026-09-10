/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SingleStepHandlerDecoratorTest extends DecoratorTestCase {

  private static final String AUTOMATON_TRAVERSER = "de.monticore.codegen.ast.automaton._visitor.AutomatonTraverser";

  private MCTypeFacade mcTypeFacade;

  private ASTCDClass handlerClass;

  private GlobalExtensionManagement glex;

  private GeneratorSetup generatorSetup;

  private static final String AST_AUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  private static final String AST_TRANSITION = "de.monticore.codegen.ast.automaton._ast.ASTTransition";

  private static final String AST_STATE = "de.monticore.codegen.ast.automaton._ast.ASTState";

  private static final String AST_AUTOMATON_NODE = "de.monticore.codegen.ast.automaton._ast.ASTAutomatonNode";

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @BeforeEach
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.mcTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);

    generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    CD4C.init(generatorSetup);

    SingleStepHandlerDecorator decorator = new SingleStepHandlerDecorator(this.glex, methodDecorator,
        visitorService, symbolTableService);
    this.handlerClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonSingleStepHandler", handlerClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(0, handlerClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(8, handlerClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(0, handlerClass.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperclassCount() {
    assertEquals(1, handlerClass.getSuperclassList().size());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterface() {
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonInheritanceHandler", handlerClass.getSuperclassList().getFirst());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverseASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, handlerClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverseASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, handlerClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_STATE);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testTraverseASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, handlerClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverseASTAutomatonNode() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, handlerClass);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AST_AUTOMATON_NODE);
    assertTrue(methodList.stream().noneMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.INTERFACE, handlerClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult<CompilationUnit> parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
