/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.enums;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;

import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.codegen.cd2java.*;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
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

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit= decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    EnumDecorator decorator = new EnumDecorator(this.glex, new AccessorDecorator(glex, new ASTService(decoratedCompilationUnit)), new ASTService(decoratedCompilationUnit));
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
  assertEquals(1, cdEnum.getCDAttributeList().size());
  }

  @Test
  public void testIntValueAttribute() {
    ASTCDAttribute intValueAttribute = cdEnum.getCDAttributeList().get(0);
    assertEquals("intValue", intValueAttribute.getName());
    assertDeepEquals(CDModifier.PROTECTED, intValueAttribute.getModifier());
    assertInt(intValueAttribute.getMCType());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, cdEnum.getCDConstructorList().size());
  }

  @Test
  public void testLiteralsConstructor() {
    ASTCDConstructor constructor = cdEnum.getCDConstructorList().get(0);
    assertDeepEquals(CDModifier.PRIVATE, constructor.getModifier());
    assertEquals("AutomatonLiterals", constructor.getName());
    assertEquals(1, constructor.sizeCDParameters());
    assertInt(constructor.getCDParameter(0).getMCType());
    assertEquals("intValue", constructor.getCDParameter(0).getName());
  }

  @Test
  public void testMethodCount() {
    assertEquals(1, cdEnum.getCDMethodList().size());
  }

  @Test
  public void testIntValueMethod() {
    ASTCDMethod method = cdEnum.getCDMethodList().get(0);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals("getIntValue", method.getName());
    assertTrue((method.getMCReturnType().isPresentMCType()));
    assertInt(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.ENUM, cdEnum, cdEnum);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
