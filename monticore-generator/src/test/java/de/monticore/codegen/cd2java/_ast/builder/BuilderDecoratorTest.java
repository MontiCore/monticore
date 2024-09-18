/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder;

import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.*;
import static org.junit.Assert.*;

public class BuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass originalClass;

  private ASTCDClass builderClass;

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "builder", "Builder");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    originalClass = getClassBy("A", ast);

    AccessorDecorator methodDecorator = new AccessorDecorator(glex, new ASTService(ast));
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, methodDecorator, new ASTService(ast));
    this.builderClass = builderDecorator.decorate(originalClass);
  }

  @Test
  public void testCopy() {
    assertNotEquals(originalClass, builderClass);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName() {
    assertEquals("ABuilder", builderClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperClassName() {
    assertFalse(builderClass.isPresentCDExtendUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructor() {
    List<ASTCDConstructor> constructors = builderClass.getCDConstructorList();
    assertEquals(1, constructors.size());
    ASTCDConstructor constructor = constructors.get(0);
    assertDeepEquals(PUBLIC, constructor.getModifier());
    assertTrue(constructor.getCDParameterList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod build = getMethodBy(BUILD_METHOD, builderClass);
    assertTrue(build.getMCReturnType().isPresentMCType());
    assertDeepEquals(originalClass.getName(), build.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, build.getModifier());
    assertTrue(build.getCDParameterList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsValidMethod() {
    ASTCDMethod isValid = getMethodBy(IS_VALID, builderClass);
    assertTrue(isValid.getMCReturnType().isPresentMCType());
    assertBoolean(isValid.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, isValid.getModifier());
    assertTrue(isValid.getCDParameterList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, builderClass, packageDir);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedSetterNoGetter(){
    ASTCDMethod setF = getMethodBy("setF", builderClass);
    assertTrue(setF.getMCReturnType().isPresentMCType());
    assertEquals(builderClass.getName(), CD4CodeMill.prettyPrint(setF.getMCReturnType(), false));
    assertDeepEquals(PUBLIC, setF.getModifier());
    assertEquals(1, setF.getCDParameterList().size());

    assertTrue(builderClass.getCDMethodList().stream().noneMatch(m -> m.getName().equals("getF")));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
