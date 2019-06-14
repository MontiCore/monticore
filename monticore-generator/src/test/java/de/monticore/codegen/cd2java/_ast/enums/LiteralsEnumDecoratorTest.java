package de.monticore.codegen.cd2java._ast.enums;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
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

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit= decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    EnumDecorator decorator = new EnumDecorator(this.glex, new AccessorDecorator(glex), new ASTService(decoratedCompilationUnit));
    this.cdEnum = decorator.decorate(getEnumBy("AutomatonLiterals", decoratedCompilationUnit));
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
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
    assertInt(intValueAttribute.getMCType());
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
    assertInt(constructor.getCDParameter(0).getMCType());
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
    assertInt(method.getMCReturnType());
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
