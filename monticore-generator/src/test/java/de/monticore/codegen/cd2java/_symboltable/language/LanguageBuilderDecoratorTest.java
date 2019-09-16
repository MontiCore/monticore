package de.monticore.codegen.cd2java._symboltable.language;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class LanguageBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass builderClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable","cdForBuilder", "Language_Builder");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    LanguageBuilderDecorator decorator = new LanguageBuilderDecorator(this.glex,
        new BuilderDecorator(glex, new AccessorDecorator(glex), new SymbolTableService(decoratedCompilationUnit)));
    ASTCDClass cdClass = getClassBy("ALanguage", decoratedCompilationUnit);
    //creates normal Symbol
    this.builderClass = decorator.decorate(cdClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("ALanguageBuilder", builderClass.getName());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertTrue( builderClass.isEmptyInterfaces());
  }

  @Test
  public void testSuperClassNotPresent() {
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
    assertEquals("ALanguageBuilder", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, builderClass.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("name", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testFileEndingAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fileEnding", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testRealBuilderAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("realBuilder", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("ALanguageBuilder", astcdAttribute.getMCType());
  }


  @Test
  public void testMethodCount() {
    assertEquals(6, builderClass.getCDMethodList().size());
  }


  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetNameMethod() {
    ASTCDMethod method = getMethodBy("setName", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("ALanguageBuilder", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetFileEndingMethod() {
    ASTCDMethod method = getMethodBy("getFileEnding", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetFileEndingMethod() {
    ASTCDMethod method = getMethodBy("setFileEnding", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("ALanguageBuilder", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("fileEnding", method.getCDParameter(0).getName());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod method = getMethodBy("build", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("ALanguage", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, builderClass, builderClass);
    System.out.println(sb.toString());
    StaticJavaParser.parse(sb.toString());
  }

}
