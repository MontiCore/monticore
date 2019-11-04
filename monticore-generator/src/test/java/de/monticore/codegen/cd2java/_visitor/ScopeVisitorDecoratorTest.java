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
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.factories.MCTypeFacade;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScopeVisitorDecoratorTest extends DecoratorTestCase {
  private ASTCDInterface visitorInterface;

  private MCTypeFacade mcTypeFacade;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private static final String I_SYMBOL = "de.monticore.symboltable.ISymbol";

  @Before
  public void setUp() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "SymbolTest");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("genHelper", new DecorationHelper());
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ScopeVisitorDecorator decorator = new ScopeVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit), new SymbolTableService(decoratedCompilationUnit));
    this.visitorInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("SymbolTestScopeVisitor", visitorInterface.getName());
  }

  @Test
  public void testAttributesEmpty() {
    assertTrue(visitorInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(15, visitorInterface.sizeCDMethods());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(2, visitorInterface.sizeInterfaces());
  }

  @Test
  public void testInterface() {
    assertDeepEquals("SymbolTestSymbolVisitor", visitorInterface.getInterface(0));
    assertDeepEquals("de.monticore.codegen.ast.lexicals._visitor.LexicalsScopeVisitor", visitorInterface.getInterface(1));
  }

  @Test
  public void testGetRealThis() {
    ASTCDMethod method = getMethodBy("getRealThis", visitorInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("SymbolTestScopeVisitor", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetRealThis() {
    ASTCDMethod method = getMethodBy("setRealThis", visitorInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("SymbolTestScopeVisitor", method.getCDParameter(0).getMCType());
    assertEquals("realThis", method.getCDParameter(0).getName());
  }

  @Test
  public void testVisitIScope() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(I_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitIScope() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(I_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
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
  public void testEndHandleISymbol() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(I_SYMBOL);
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testVisitAutomatonScope() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.symboltest._symboltable.SymbolTestScope");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitAutomatonScope() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.symboltest._symboltable.SymbolTestScope");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndHandleAutomatonScope() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.symboltest._symboltable.SymbolTestScope");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndTraverseAutomatonScope() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.symboltest._symboltable.SymbolTestScope");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }


  @Test
  public void testVisitAutomatonArtifactScope() {
    List<ASTCDMethod> methodList = getMethodsBy("visit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.symboltest._symboltable.SymbolTestArtifactScope");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndVisitAutomatonArtifactScope() {
    List<ASTCDMethod> methodList = getMethodsBy("endVisit", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.symboltest._symboltable.SymbolTestArtifactScope");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndHandleAutomatonArtifactScope() {
    List<ASTCDMethod> methodList = getMethodsBy("handle", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.symboltest._symboltable.SymbolTestArtifactScope");
    assertTrue(methodList.stream().anyMatch(m -> astType.deepEquals(m.getCDParameter(0).getMCType())));
    assertEquals(1, methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).count());
    ASTCDMethod method = methodList.stream().filter(m -> astType.deepEquals(m.getCDParameter(0).getMCType())).findFirst().get();

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testEndTraverseAutomatonArtifactScope() {
    List<ASTCDMethod> methodList = getMethodsBy("traverse", 1, visitorInterface);
    ASTMCType astType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.symboltest._symboltable.SymbolTestArtifactScope");
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
