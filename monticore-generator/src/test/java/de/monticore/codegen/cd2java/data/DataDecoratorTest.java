package de.monticore.codegen.cd2java.data;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class DataDecoratorTest extends DecoratorTestCase {
  
  private GlobalExtensionManagement glex = new GlobalExtensionManagement();
  
  private ASTCDClass dataClass;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "data", "Data");
    ASTCDClass clazz = getClassBy("A", cd);
    this.glex.setGlobalValue("service", new AbstractService(cd));
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    MethodDecorator methodDecorator = new MethodDecorator(glex);
    DataDecorator dataDecorator = new DataDecorator(this.glex, methodDecorator, new ASTService(cd), new DataDecoratorUtil());
    this.dataClass = dataDecorator.decorate(clazz);

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
  }

  @Test
  public void testClassSignature() {
    assertEquals("A", dataClass.getName());
  }

  @Test
  public void testAttributes() {
    assertEquals(5, dataClass.sizeCDAttributes());
  }

  @Test
  public void testPrimitiveAttribute() {
    ASTCDAttribute attribute = getAttributeBy("i", dataClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testMandatoryAttribute() {
    ASTCDAttribute attribute = getAttributeBy("s", dataClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());
  }

  @Test
  public void testOptionalAttribute() {
    ASTCDAttribute attribute = getAttributeBy("opt", dataClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertOptionalOf(String.class, attribute.getMCType());
  }

  @Test
  public void testListAttribute() {
    ASTCDAttribute attribute = getAttributeBy("list", dataClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertListOf(String.class, attribute.getMCType());
  }

  @Test
  public void testBAttribute() {
    ASTCDAttribute attribute = getAttributeBy("b", dataClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("de.monticore.codegen.data.ASTB", attribute.getMCType());
  }

  @Test
  public void testConstructors() {
    assertFalse(dataClass.isEmptyCDConstructors());
    assertEquals(2, dataClass.sizeCDConstructors());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor defaultConstructor = dataClass.getCDConstructor(0);
    assertDeepEquals(PROTECTED, defaultConstructor.getModifier());
    assertTrue(defaultConstructor.isEmptyCDParameters());
  }

  @Test
  public void testFullConstructor() {
    ASTCDConstructor fullConstructor = dataClass.getCDConstructor(1);
    assertDeepEquals(PROTECTED, fullConstructor.getModifier());
    assertFalse(fullConstructor.isEmptyCDParameters());
    assertEquals(5, fullConstructor.sizeCDParameters());

    ASTCDParameter parameter = fullConstructor.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("i", parameter.getName());

    parameter = fullConstructor.getCDParameter(1);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("s", parameter.getName());

    parameter = fullConstructor.getCDParameter(2);
    assertOptionalOf(String.class, parameter.getMCType());
    assertEquals("opt", parameter.getName());

    parameter = fullConstructor.getCDParameter(3);
    assertListOf(String.class, parameter.getMCType());
    assertEquals("list", parameter.getName());

    parameter = fullConstructor.getCDParameter(4);
    assertDeepEquals("de.monticore.codegen.data.ASTB", parameter.getMCType());
    assertEquals("b", parameter.getName());
  }

  @Test
  public void testMethods() {
    assertFalse(dataClass.isEmptyCDMethods());
    assertEquals(54, dataClass.sizeCDMethods());
  }

  @Test
  public void testDeepEquals() {
    ASTCDMethod method = getMethodBy("deepEquals", 1, dataClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepEqualsForceSameOrder() {
    ASTCDMethod method = getMethodBy("deepEquals", 2, dataClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());

    parameter = method.getCDParameter(1);
    assertBoolean(parameter.getMCType());
    assertEquals("forceSameOrder", parameter.getName());
  }

  @Test
  public void testDeepEqualsWithComments() {
    ASTCDMethod method = getMethodBy("deepEqualsWithComments", 1, dataClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepEqualsWithCommentsForceSameOrder() {
    ASTCDMethod method = getMethodBy("deepEqualsWithComments", 2, dataClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());

    parameter = method.getCDParameter(1);
    assertBoolean(parameter.getMCType());
    assertEquals("forceSameOrder", parameter.getName());
  }

  @Test
  public void testEqualAttributes() {
    ASTCDMethod method = getMethodBy("equalAttributes", dataClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testEqualsWithComments() {
    ASTCDMethod method = getMethodBy("equalsWithComments", dataClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testDeepClone() {
    ASTCDMethod method = getMethodBy("deepClone", 0, dataClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(dataClass.getName(), method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testDeepCloneWithResult() {
    ASTCDMethod method = getMethodBy("deepClone", 1, dataClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(dataClass.getName(), method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(dataClass.getName(), parameter.getMCType());
    assertEquals("result", parameter.getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, dataClass, dataClass);
    // TODO Check System.out.println(sb.toString());
  }


  @Test
  public void testGeneratedAutomatonCode() {
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "ast", "Automaton");

    ASTCDClass clazz = getClassBy("ASTAutomaton", cd);

    MethodDecorator methodDecorator = new MethodDecorator(glex);
    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, new ASTService(cd), new DataDecoratorUtil());
    clazz = dataDecorator.decorate(clazz);

    glex.setGlobalValue("astHelper", new DecorationHelper());
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, clazz, clazz);
    // TODO Check System.out.println(sb.toString());
  }
}
