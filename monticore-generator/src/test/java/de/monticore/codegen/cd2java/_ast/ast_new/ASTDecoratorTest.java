package de.monticore.codegen.cd2java._ast.ast_new;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private CDTypeFacade cdTypeFacade = CDTypeFacade.getInstance();

  private ASTCDClass astClass;

  private static final String AST_SCOPE = "de.monticore.codegen.ast.ast._symboltable.ASTScope";

  private static final String AST_SYMBOL = "de.monticore.codegen.ast.ast._symboltable.ASymbol";

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");

    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    SymbolTableService symbolTableService = new SymbolTableService(ast);
    ASTDecorator decorator = new ASTDecorator(this.glex, new ASTService(ast), new VisitorService(ast), new NodeFactoryService(ast),
        new ASTSymbolDecorator(glex, symbolTableService), new ASTScopeDecorator(glex, symbolTableService), new MethodDecorator(glex),
        new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("A", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClassName() {
    assertEquals("A", astClass.getName());
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
    assertEquals(5, astClass.getCDAttributeList().size());
  }

  @Test
  public void testEmptyConstructors() {
    assertEquals(0, astClass.getCDConstructorList().size());
  }

  @Test
  public void testMethods() {
    assertFalse(astClass.getCDMethodList().isEmpty());
    assertEquals(31, astClass.getCDMethodList().size());
  }

  @Test
  public void testAcceptMethod() {
    List<ASTCDMethod> methods = getMethodsBy("accept", 1, astClass);
    ASTMCType visitorType = this.cdTypeFacade.createQualifiedType("de.monticore.codegen.ast.ast._visitor.ASTVisitor");

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
    ASTMCType visitorType = this.cdTypeFacade.createQualifiedType("de.monticore.codegen.ast.super._visitor.SuperVisitor");

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

    ASTMCType returnType = this.cdTypeFacade.createCollectionTypeOf("de.monticore.ast.ASTNode");
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(returnType, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testConstructMethod() {
    ASTCDMethod method = getMethodBy("_construct", astClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createQualifiedType(astClass.getName());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astClass, astClass);
    // TODOO: Check einführen System.out.println(sb.toString());
  }

  @Test
  public void testGetScopeMethod() {
    ASTCDMethod method = getMethodBy("getSpannedASTScope", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_SCOPE);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetScopeOptMethod() {
    ASTCDMethod method = getMethodBy("getSpannedASTScopeOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_SCOPE, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentScopeMethod() {
    ASTCDMethod method = getMethodBy("isPresentSpannedASTScope", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsSetScopeMethod() {
    ASTCDMethod method = getMethodBy("setSpannedASTScope", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("spannedASTScope", method.getCDParameter(0).getName());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_SCOPE);
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
  }


  @Test
  public void testIsSetScopeOptMethod() {
    ASTCDMethod method = getMethodBy("setSpannedASTScopeOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("spannedASTScope", method.getCDParameter(0).getName());
    assertOptionalOf(AST_SCOPE, method.getCDParameter(0).getMCType());
  }

  @Test
  public void testGetSymbolMethod() {
    ASTCDMethod method = getMethodBy("getASymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_SYMBOL);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetSymbolOptMethod() {
    ASTCDMethod method = getMethodBy("getASymbolOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertOptionalOf(AST_SYMBOL, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentASymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsSetSymbolMethod() {
    ASTCDMethod method = getMethodBy("setASymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("aSymbol", method.getCDParameter(0).getName());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(AST_SYMBOL);
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
  }


  @Test
  public void testIsSetSymbolOptMethod() {
    ASTCDMethod method = getMethodBy("setASymbolOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("aSymbol", method.getCDParameter(0).getName());
    assertOptionalOf(AST_SYMBOL, method.getCDParameter(0).getMCType());
  }


  @Test
  public void testIsSetSymbolAbsentMethod() {
    ASTCDMethod method = getMethodBy("setASymbolAbsent", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testIsSetScopeAbsentMethod() {
    ASTCDMethod method = getMethodBy("setSpannedASTScopeAbsent", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  }

}
