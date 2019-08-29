package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
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
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class GlobalScopeClassBuilderDecortorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  public static final String MODEL_PATH = "de.monticore.io.paths.ModelPath";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    GlobalScopeClassBuilderDecorator decorator = new GlobalScopeClassBuilderDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit),
        new AccessorDecorator(glex));

    //creates normal Symbol
    this.scopeClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonGlobalScopeBuilder", scopeClass.getName());
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
    assertTrue(scopeClass.isEmptyCDConstructors());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(2, scopeClass.sizeCDAttributes());
  }

  @Test
  public void testModelPathAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("modelPath", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(MODEL_PATH, astcdAttribute.getMCType());
  }

  @Test
  public void testLanguageAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("language", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.AutomatonLanguage", astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(5, scopeClass.getCDMethodList().size());
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
    ASTCDMethod method = getMethodBy("getLanguage", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.AutomatonLanguage", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetAutomatonLanguageMethod() {
    ASTCDMethod method = getMethodBy("setLanguage", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AutomatonGlobalScopeBuilder",method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.AutomatonLanguage", method.getCDParameter(0).getMCType());
    assertEquals("language", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetModelPathMethod() {
    ASTCDMethod method = getMethodBy("setModelPath", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AutomatonGlobalScopeBuilder",method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(MODEL_PATH, method.getCDParameter(0).getMCType());
    assertEquals("modelPath", method.getCDParameter(0).getName());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod method = getMethodBy("build", scopeClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AutomatonGlobalScope", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeClass, scopeClass);
    System.out.println(sb.toString());
    StaticJavaParser.parse(sb.toString());
  }

}
