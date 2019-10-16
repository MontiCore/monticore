/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.MCTypeFacade;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PACKAGE_PRIVATE_FINAL;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParentAwareVisitorDecoratorTest extends DecoratorTestCase {

  private MCTypeFacade MCTypeFacade;

  private ASTCDClass visitorClass;

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
    this.MCTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    ParentAwareVisitorDecorator decorator = new ParentAwareVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit));
    this.visitorClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("AutomatonParentAwareVisitor", visitorClass.getName());
  }

  @Test
  public void testAttributesEmpty() {
    assertEquals(1, visitorClass.sizeCDAttributes());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(1, visitorClass.sizeInterfaces());
  }

  @Test
  public void testInterface() {
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor", visitorClass.getInterface(0));
  }

  @Test
  public void testParentsAttribute() {
    ASTCDAttribute parents = getAttributeBy("parents", visitorClass);
    assertTrue(parents.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE_FINAL, parents.getModifier());
    assertDeepEquals("java.util.Stack<" + AST_AUTOMATON_NODE + ">", parents.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(5, visitorClass.sizeCDMethods());
  }

  @Test
  public void testGetParent() {
    ASTCDMethod getParentMethod = getMethodBy("getParent", visitorClass);

    assertDeepEquals(PUBLIC, getParentMethod.getModifier());
    ASTMCType type = MCTypeFacade.createOptionalTypeOf(AST_AUTOMATON_NODE);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    assertDeepEquals(returnType, getParentMethod.getMCReturnType());
    assertTrue(getParentMethod.isEmptyCDParameters());
  }

  @Test
  public void testGetParents() {
    ASTCDMethod getParentsMethod = getMethodBy("getParents", visitorClass);

    assertDeepEquals(PUBLIC, getParentsMethod.getModifier());
    ASTMCType type = MCTypeFacade.createListTypeOf(AST_AUTOMATON_NODE);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    assertDeepEquals(returnType, getParentsMethod.getMCReturnType());
    assertTrue(getParentsMethod.isEmptyCDParameters());
  }



  @Test
  public void testTraverseASTAutomaton() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorClass);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_AUTOMATON);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseASTTransition() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorClass);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_TRANSITION);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseASTState() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorClass);
    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(AST_STATE);
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
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, visitorClass, visitorClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

}
