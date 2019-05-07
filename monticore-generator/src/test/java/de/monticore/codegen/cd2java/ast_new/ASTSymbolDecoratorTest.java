package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTSymbolDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private CDTypeFacade cdTypeFacade = CDTypeFacade.getInstance();

  private static final String AST_SYMBOL = "de.monticore.codegen.ast.ast._symboltable.ASymbol";

  @Before
  public void setup() {
    this.cdTypeFacade = CDTypeFacade.getInstance();
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("service", new AbstractService(ast));

    ASTSymbolDecorator decorator = new ASTSymbolDecorator(this.glex, new MethodDecorator(glex), new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("A", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClass() {
    assertEquals("A", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(1, astClass.sizeCDAttributes());
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("aSymbol", astClass);
    assertDeepEquals(PROTECTED, symbolAttribute.getModifier());
    assertOptionalOf("de.monticore.codegen.ast.ast._symboltable.ASymbol", symbolAttribute.getType());
  }

  @Test
  public void testMethods() {
    assertEquals(6, astClass.getCDMethodList().size());
  }
  
  @Test
  public void testGetSymbolMethod() {
    ASTCDMethod method = getMethodBy("getASymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_SYMBOL);
    assertDeepEquals(astType, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetSymbolOptMethod() {
    ASTCDMethod method = getMethodBy("getASymbolOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertOptionalOf(AST_SYMBOL, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentASymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsSetSymbolMethod() {
    ASTCDMethod method = getMethodBy("setASymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("aSymbol", method.getCDParameter(0).getName());
    ASTType astType = this.cdTypeFacade.createTypeByDefinition(AST_SYMBOL);
    assertDeepEquals(astType, method.getCDParameter(0).getType());
  }


  @Test
  public void testIsSetSymbolOptMethod() {
    ASTCDMethod method = getMethodBy("setASymbolOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("aSymbol", method.getCDParameter(0).getName());
    assertOptionalOf(AST_SYMBOL, method.getCDParameter(0).getType());
  }


  @Test
  public void testIsSetSymbolAbsentMethod() {
    ASTCDMethod method = getMethodBy("setASymbolAbsent", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertVoid(method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }
  
  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astClass, astClass);
    System.out.println(sb.toString());
  }
}
