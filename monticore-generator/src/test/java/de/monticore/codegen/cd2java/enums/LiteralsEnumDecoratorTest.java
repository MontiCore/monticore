package de.monticore.codegen.cd2java.enums;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertInt;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getEnumBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LiteralsEnumDecoratorTest extends DecoratorTestCase {

  private ASTCDEnum cdEnum;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    EnumDecorator decorator = new EnumDecorator(this.glex, new AccessorDecorator(glex), new ASTService(compilationUnit));
    this.cdEnum = decorator.decorate(getEnumBy("AutomatonLiterals", compilationUnit));
  }

  @Test
  public void testEnumName(){
    assertEquals("AutomatonLiterals", cdEnum.getName());
  }

  @Test
  public void testAttributeCount() {
  assertEquals(1, cdEnum.sizeCDAttributes());
  }

  @Test
  public void testIntValueAttribute() {
    ASTCDAttribute intValueAttribute = cdEnum.getCDAttribute(0);
    assertEquals("intValue", intValueAttribute.getName());
    assertDeepEquals(CDModifier.PROTECTED, intValueAttribute.getModifier());
    assertInt(intValueAttribute.getType());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, cdEnum.sizeCDConstructors());
  }

  @Test
  public void testLiteralsConstructor() {
    ASTCDConstructor constructor = cdEnum.getCDConstructor(0);
    assertDeepEquals(CDModifier.PRIVATE, constructor.getModifier());
    assertEquals("AutomatonLiterals", constructor.getName());
    assertEquals(1, constructor.sizeCDParameters());
    assertInt(constructor.getCDParameter(0).getType());
    assertEquals("intValue", constructor.getCDParameter(0).getName());
  }

  @Test
  public void testMethodCount() {
    assertEquals(1, cdEnum.sizeCDMethods());
  }

  @Test
  public void testIntValueMethod() {
    ASTCDMethod method = cdEnum.getCDMethod(0);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals("getIntValue", method.getName());
    assertInt(method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.ENUM, cdEnum, cdEnum);
    System.out.println(sb.toString());
  }
}
