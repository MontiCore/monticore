package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
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
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTReferencedSymbolDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private CDTypeFactory cdTypeFactory = CDTypeFactory.getInstance();

  private static final String NAME_SYMBOL = "de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol";

  private static final String NAME_DEFINITION = "de.monticore.codegen.ast.referencedsymbol._ast.ASTFoo";

  @Before
  public void setup() {
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    ASTReferencedSymbolDecorator decorator = new ASTReferencedSymbolDecorator(this.glex, new MethodDecorator(glex));
    ASTCDClass clazz = getClassBy("ASTBar", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClass() {
    assertEquals("ASTBar", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(2, astClass.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute nameAttribute = getAttributeBy("name", astClass);
    assertTrue( nameAttribute.getModifier().isProtected());
    assertTrue(nameAttribute.getModifier().isPresentStereotype());
    assertEquals(1, nameAttribute.getModifier().getStereotype().sizeValues());
    assertEquals("referencedSymbol", nameAttribute.getModifier().getStereotype().getValue(0).getName());
    assertTrue(nameAttribute.getModifier().getStereotype().getValue(0).isPresentValue());
    assertEquals("de.monticore.codegen.ast.referencedSymbol.FooSymbol", nameAttribute.getModifier().getStereotype().getValue(0).getValue());
    assertDeepEquals(cdTypeFactory.createTypeByDefinition("String"), nameAttribute.getType());
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("nameSymbol", astClass);
    assertDeepEquals(PRIVATE, symbolAttribute.getModifier());
    assertOptionalOf(NAME_SYMBOL, symbolAttribute.getType());
  }

  @Test
  public void testMethods() {
    assertEquals(6, astClass.getCDMethodList().size());
  }


  @Test
  public void testGetNameSymbolMethod() {
    ASTCDMethod method = getMethodBy("getNameSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFactory.createTypeByDefinition(NAME_SYMBOL);
    assertDeepEquals(astType, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetNameSymbolOptMethod() {
    ASTCDMethod method = getMethodBy("getNameSymbolOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertOptionalOf(NAME_SYMBOL, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentNameSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentNameSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
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
