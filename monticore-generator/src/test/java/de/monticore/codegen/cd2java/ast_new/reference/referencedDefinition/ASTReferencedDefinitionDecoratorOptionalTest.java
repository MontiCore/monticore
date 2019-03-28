package de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition.referencedDefinitionMethodDecorator.ReferencedDefinitionAccessorDecorator;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ASTReferencedDefinitionDecoratorOptionalTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private CDTypeFactory cdTypeFactory = CDTypeFactory.getInstance();

  private static final String NAME_DEFINITION = "de.monticore.codegen.ast.referencedsymbol._ast.ASTFoo";

  @Before
  public void setup() {
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    SymbolTableService symbolTableService = new SymbolTableService(ast);
    ASTReferencedDefinitionDecorator decorator = new ASTReferencedDefinitionDecorator(this.glex, new ReferencedDefinitionAccessorDecorator(glex, symbolTableService), symbolTableService);
    ASTCDClass clazz = getClassBy("ASTBarOpt", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClass() {
    assertEquals("ASTBarOpt", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(1, astClass.sizeCDAttributes());
  }

  @Test
  public void testMethods() {
    assertEquals(3, astClass.getCDMethodList().size());
  }

  @Test
  public void testGetNameDefinitionMethod() {
    ASTCDMethod method = getMethodBy("getNameDefinition", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFactory.createTypeByDefinition(NAME_DEFINITION);
    assertDeepEquals(astType, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetNameDefinitionOptMethod() {
    ASTCDMethod method = getMethodBy("getNameDefinitionOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertOptionalOf(NAME_DEFINITION, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentNameDefinitionMethod() {
    ASTCDMethod method = getMethodBy("isPresentNameDefinition", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getReturnType());
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
