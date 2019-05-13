package de.monticore.codegen.cd2java.data;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FullConstructorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass subBClass;

  private ASTCDClass subAClass;

  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "data", "SupData");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    DataDecorator dataDecorator = new DataDecorator(this.glex, new MethodDecorator(glex), new ASTService(ast), new DataDecoratorUtil());
    ASTCDClass clazz = getClassBy("SupB", ast);
    this.subBClass = dataDecorator.decorate(clazz);
    clazz = getClassBy("SupA", ast);
    this.subAClass = dataDecorator.decorate(clazz);
  }

  @Test
  public void testClassName() {
    assertEquals("SupB", subBClass.getName());
  }

  @Test
  public void testAttributesCount() {
    assertEquals(2, subBClass.getCDAttributeList().size());
  }

  @Test
  public void testPrimitiveAttribute() {
    ASTCDAttribute attribute = getAttributeBy("i", subBClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testConstructorsCount() {
    assertEquals(2, subBClass.getCDConstructorList().size());
  }

  @Test
  public void testFullConstructor() {
    ASTCDConstructor fullConstructor = subBClass.getCDConstructor(1);
    assertDeepEquals(PROTECTED, fullConstructor.getModifier());
    assertFalse(fullConstructor.isEmptyCDParameters());
    assertEquals(2, fullConstructor.sizeCDParameters());

    ASTCDParameter parameter = fullConstructor.getCDParameter(0);
    assertInt(parameter.getType());
    assertEquals("i", parameter.getName());

    parameter = fullConstructor.getCDParameter(1);
    assertDeepEquals(String.class, parameter.getType());
    assertEquals("s", parameter.getName());
  }

  @Test
  public void testFullConstructorSubA() {
    //test for inheritance over more layers
    ASTCDConstructor fullConstructor = subAClass.getCDConstructor(1);
    assertDeepEquals(PROTECTED, fullConstructor.getModifier());
    assertFalse(fullConstructor.isEmptyCDParameters());
    assertEquals(3, fullConstructor.sizeCDParameters());

    ASTCDParameter parameter = fullConstructor.getCDParameter(0);
    assertInt(parameter.getType());
    assertEquals("i", parameter.getName());

    parameter = fullConstructor.getCDParameter(1);
    assertBoolean(parameter.getType());
    assertEquals("b", parameter.getName());

    parameter = fullConstructor.getCDParameter(2);
    assertDeepEquals(String.class, parameter.getType());
    assertEquals("s", parameter.getName());
  }

  @Test
  public void testGeneratedCodeSubA() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, subAClass, subAClass);
    System.out.println(sb.toString());
  }


  @Test
  public void testGeneratedCodeSubB() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, subBClass, subBClass);
    System.out.println(sb.toString());
  }
}
