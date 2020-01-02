package de.monticore.codegen.cd2java._symboltable.modelloader;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class ModelLoaderBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass builderClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "cdForBuilder", "ModelLoader_Builder");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex), new SymbolTableService(decoratedCompilationUnit));
    ASTCDClass cdClass = getClassBy("AModelLoader", decoratedCompilationUnit);

    ModelLoaderBuilderDecorator decorator = new ModelLoaderBuilderDecorator(this.glex, builderDecorator);

    //creates normal Symbol
    this.builderClass = decorator.decorate(cdClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AModelLoaderBuilder", builderClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertTrue(builderClass.isEmptyInterfaces());
  }

  @Test
  public void testSuperClassCount() {
    assertFalse(builderClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, builderClass.sizeCDConstructors());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = builderClass.getCDConstructor(0);
    assertDeepEquals(PROTECTED, cdConstructor.getModifier());
    assertEquals("AModelLoaderBuilder", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(2, builderClass.sizeCDAttributes());
  }


  @Test
  public void testLanguageAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("modelingLanguage", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("ALanguage", astcdAttribute.getMCType());
  }

  @Test
  public void testRealBuilderAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("realBuilder", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("AModelLoaderBuilder", astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(4, builderClass.getCDMethodList().size());
  }

  @Test
  public void testGetModelPathMethod() {
    ASTCDMethod method = getMethodBy("getModelingLanguage", builderClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("ALanguage", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetAutomatonLanguageMethod() {
    ASTCDMethod method = getMethodBy("setModelingLanguage", builderClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AModelLoaderBuilder", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("ALanguage", method.getCDParameter(0).getMCType());
    assertEquals("modelingLanguage", method.getCDParameter(0).getName());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod method = getMethodBy("build", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AModelLoader", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, builderClass, builderClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
