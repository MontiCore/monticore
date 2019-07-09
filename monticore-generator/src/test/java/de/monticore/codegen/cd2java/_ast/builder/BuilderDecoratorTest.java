package de.monticore.codegen.cd2java._ast.builder;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java._ast.builder.BuilderDecorator.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class BuilderDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass originalClass;

  private ASTCDClass builderClass;

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "builder", "Builder");
    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    originalClass = getClassBy("A", ast);

    AccessorDecorator methodDecorator = new AccessorDecorator(glex);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, methodDecorator);
    this.builderClass = builderDecorator.decorate(originalClass);
  }

  @Test
  public void testCopy() {
    assertNotEquals(originalClass, builderClass);
  }

  @Test
  public void testClassName() {
    assertEquals("ABuilder", builderClass.getName());
  }

  @Test
  public void testSuperClassName() {
    assertFalse(builderClass.getSuperclassOpt().isPresent());
  }

  @Test
  public void testConstructor() {
    List<ASTCDConstructor> constructors = builderClass.getCDConstructorList();
    assertEquals(1, constructors.size());
    ASTCDConstructor constructor = constructors.get(0);
    assertDeepEquals(PROTECTED, constructor.getModifier());
    assertTrue(constructor.getCDParameterList().isEmpty());
  }

  @Test
  public void testAttributes() {
    assertEquals(5, builderClass.getCDAttributeList().size());

    ASTCDAttribute attribute = getAttributeBy("i", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertInt(attribute.getMCType());

    attribute = getAttributeBy("s", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());

    attribute = getAttributeBy("opt", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertOptionalOf(String.class, attribute.getMCType());

    attribute = getAttributeBy("list", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertListOf(String.class, attribute.getMCType());

    attribute = getAttributeBy(REAL_BUILDER, builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(builderClass.getName(), attribute.getMCType());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod build = getMethodBy(BUILD_METHOD, builderClass);
    assertDeepEquals(originalClass.getName(), build.getMCReturnType());
    assertDeepEquals(PUBLIC, build.getModifier());
    assertTrue(build.getCDParameterList().isEmpty());
  }

  @Test
  public void testIsValidMethod() {
    ASTCDMethod isValid = getMethodBy(IS_VALID, builderClass);
    assertBoolean(isValid.getMCReturnType());
    assertDeepEquals(PUBLIC, isValid.getModifier());
    assertTrue(isValid.getCDParameterList().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, builderClass, builderClass);
    System.out.println(sb.toString());
  }
}
