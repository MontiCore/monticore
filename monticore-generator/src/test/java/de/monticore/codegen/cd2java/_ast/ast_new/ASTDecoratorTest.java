/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
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
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class ASTDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  private ASTCDClass astClass;

  private static final String AST_SCOPE = "de.monticore.codegen.ast.ast._symboltable.IASTScope";

  private static final String AST_SYMBOL = "de.monticore.codegen.ast.ast._symboltable.ASymbol";

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");

    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    SymbolTableService symbolTableService = new SymbolTableService(ast);
    ASTDecorator decorator = new ASTDecorator(this.glex, new ASTService(ast), new VisitorService(ast), new NodeFactoryService(ast),
        new ASTSymbolDecorator(glex, symbolTableService), new ASTScopeDecorator(glex, symbolTableService), new MethodDecorator(glex, symbolTableService),
        new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("A", ast);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.astClass = decorator.decorate(clazz, changedClass);
  }

  @Test
  public void testClassName() {
    assertEquals("A", astClass.getName());
  }

  @Test
  public void testClassModifier() {
    // because it defines a symbol but has no name attribute or a getName method
    assertTrue(astClass.getModifier().isAbstract());
  }

  @Test
  public void testSuperClass() {
    assertEquals("ASTCNode", astClass.printSuperClass());
  }

  @Test
  public void testBaseInterface() {
    assertEquals(1, astClass.sizeInterfaces());
    assertEquals("de.monticore.codegen.ast.ast._ast.ASTASTNode", astClass.printInterfaces());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, astClass.getCDAttributeList().size());
  }

  @Test
  public void testEmptyConstructors() {
    assertEquals(0, astClass.getCDConstructorList().size());
  }

  @Test
  public void testMethods() {
    assertFalse(astClass.getCDMethodList().isEmpty());
    assertEquals(14, astClass.getCDMethodList().size());
  }

  /**
   * abstract method generated because A is a symbol but has no name
   */
  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", astClass);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testAcceptMethod() {
    List<ASTCDMethod> methods = getMethodsBy("accept", 1, astClass);
    ASTMCType visitorType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.ast._visitor.ASTVisitor");

    methods = methods.stream().filter(m -> visitorType.deepEquals(m.getCDParameter(0).getMCType())).collect(Collectors.toList());
    assertEquals(1, methods.size());

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);

    assertDeepEquals(visitorType, parameter.getMCType());
    assertEquals("visitor", parameter.getName());
  }

  @Test
  public void testAcceptSuperMethod() {
    List<ASTCDMethod> methods = getMethodsBy("accept", 1, astClass);
    ASTMCType visitorType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.supercd._visitor.SuperCDVisitor");

    methods = methods.stream().filter(m -> visitorType.deepEquals(m.getCDParameter(0).getMCType())).collect(Collectors.toList());
    assertEquals(1, methods.size());

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(visitorType, parameter.getMCType());
    assertEquals("visitor", parameter.getName());
  }

  @Test
  public void testGetChildrenMethod() {
    ASTCDMethod method = getMethodBy("get_Children", astClass);

    assertDeepEquals(PUBLIC, method.getModifier());

    ASTMCType returnType = this.mcTypeFacade.createCollectionTypeOf("de.monticore.ast.ASTNode");
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testConstructMethod() {
    ASTCDMethod method = getMethodBy("_construct", astClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(astClass.getName());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astClass, astClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

  @Test
  public void testGetScopeMethod() {
    ASTCDMethod method = getMethodBy("getSpannedScope", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AST_SCOPE, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsSetScopeMethod() {
    ASTCDMethod method = getMethodBy("setSpannedScope", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("spannedScope", method.getCDParameter(0).getName());
    assertDeepEquals(AST_SCOPE, method.getCDParameter(0).getMCType());
  }

  @Test
  public void testGetSymbolMethod() {
    ASTCDMethod method = getMethodBy("getSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AST_SYMBOL, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsSetSymbolMethod() {
    ASTCDMethod method = getMethodBy("setSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("symbol", method.getCDParameter(0).getName());
    assertDeepEquals(AST_SYMBOL, method.getCDParameter(0).getMCType());
  }

  @Test
  public void testIsSetSymbolAbsentMethod() {
    ASTCDMethod method = getMethodBy("setSymbolAbsent", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  }

}
