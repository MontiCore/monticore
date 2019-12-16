/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolVisitorDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface visitorInterface;

  private MCTypeFacade mcTypeFacade;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.symboltest._symboltable.AutomatonSymbol";

  private static final String FOO_SYMBOL = "de.monticore.codegen.ast.symboltest._symboltable.FooSymbol";

  private static final String I_SYMBOL = "de.monticore.symboltable.ISymbol";

  @Before
  public void setUp() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "SymbolTest");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("genHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymbolVisitorDecorator decorator = new SymbolVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit), new SymbolTableService(decoratedCompilationUnit));
    this.visitorInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("SymbolTestSymbolVisitor", visitorInterface.getName());
  }

  @Test
  public void testAttributesEmpty() {
    assertTrue(visitorInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(13, visitorInterface.sizeCDMethods());
  }

  @Test
  public void testInterfaceEmpty() {
    assertTrue(visitorInterface.isEmptyInterfaces());
  }

  @Test
  public void testGetRealThis() {
    ASTCDMethod method = getMethodBy("getRealThis", visitorInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("SymbolTestSymbolVisitor", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetRealThis() {
    ASTCDMethod method = getMethodBy("setRealThis", visitorInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("SymbolTestSymbolVisitor", method.getCDParameter(0).getMCType());
    assertEquals("realThis", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitISymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(I_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitISymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(I_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitAutomatonSymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitAutomatonSymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testHandleAutomatonSymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseAutomatonSymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(AUTOMATON_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitFooSymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(FOO_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitFooSymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(FOO_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testHandleFooSymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(FOO_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testTraverseFooSymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(FOO_SYMBOL);
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
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, visitorInterface, visitorInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
