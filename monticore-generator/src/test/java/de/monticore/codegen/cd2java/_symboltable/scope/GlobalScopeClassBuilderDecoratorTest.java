package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
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

public class GlobalScopeClassBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String MODEL_PATH = "de.monticore.io.paths.ModelPath";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable","cdForBuilder", "GlobalScope_Builder");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex), new SymbolTableService(decoratedCompilationUnit));
    ASTCDClass cdClass = getClassBy("AGlobalScope", decoratedCompilationUnit);

    GlobalScopeClassBuilderDecorator decorator = new GlobalScopeClassBuilderDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit),
        builderDecorator);

    //creates normal Symbol
    this.scopeClass = decorator.decorate(cdClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AGlobalScopeBuilder", scopeClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertTrue(scopeClass.isEmptyInterfaces());
  }

  @Test
  public void testSuperClassCount() {
    assertFalse(scopeClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1,scopeClass.sizeCDConstructors());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(0);
    assertDeepEquals(PROTECTED, cdConstructor.getModifier());
    assertEquals("AGlobalScopeBuilder", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, scopeClass.sizeCDAttributes());
  }

  @Test
  public void testModelPathAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("modelPath", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(MODEL_PATH, astcdAttribute.getMCType());
  }

  @Test
  public void testLanguageAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("aLanguage", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("ALanguage", astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(6, scopeClass.getCDMethodList().size());
  }

  @Test
  public void testGetModelPathMethod() {
    ASTCDMethod method = getMethodBy("getModelPath", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MODEL_PATH, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGetAutomatonLanguageMethod() {
    ASTCDMethod method = getMethodBy("getALanguage", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("ALanguage", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetAutomatonLanguageMethod() {
    ASTCDMethod method = getMethodBy("setALanguage", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AGlobalScopeBuilder",method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("ALanguage", method.getCDParameter(0).getMCType());
    assertEquals("aLanguage", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetModelPathMethod() {
    ASTCDMethod method = getMethodBy("setModelPath", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AGlobalScopeBuilder",method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(MODEL_PATH, method.getCDParameter(0).getMCType());
    assertEquals("modelPath", method.getCDParameter(0).getName());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod method = getMethodBy("build", scopeClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AGlobalScope", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeClass, scopeClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

}
