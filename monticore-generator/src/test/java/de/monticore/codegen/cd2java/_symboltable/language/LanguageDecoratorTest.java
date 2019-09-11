package de.monticore.codegen.cd2java._symboltable.language;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class LanguageDecoratorTest extends DecoratorTestCase {
  private ASTCDClass languageClass;

  private ASTCDClass languageClassTop;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private IterablePath iterablePath;

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    LanguageDecorator decorator = new LanguageDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), new ParserService(decoratedCompilationUnit), new AccessorDecorator(glex));

    //creates normal Symbol
    decorator.setLanguageTop(false);
    this.languageClass = decorator.decorate(decoratedCompilationUnit);

    decorator.setLanguageTop(true);
    this.languageClassTop = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonLanguage", languageClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(1, languageClass.sizeInterfaces());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals("de.monticore.IModelingLanguage<AutomatonModelLoader>",  languageClass.getInterface(0));
  }

  @Test
  public void testSuperClassNotPresent() {
    assertFalse(languageClass.isPresentSuperclass());
  }


  @Test
  public void testConstructorCount() {
    assertEquals(1, languageClass.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = languageClass.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonLanguage", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(String.class, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("langName", cdConstructor.getCDParameter(0).getName());

    assertDeepEquals(String.class, cdConstructor.getCDParameter(1).getMCType());
    assertEquals("fileEnding", cdConstructor.getCDParameter(1).getName());
    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, languageClass.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("name", languageClass);
    assertDeepEquals(CDModifier.PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testModelLoaderAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("modelLoader", languageClass);
    assertDeepEquals(CDModifier.PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonModelLoader", astcdAttribute.getMCType());
  }

  @Test
  public void testFileExtensionAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fileExtension", languageClass);
    assertDeepEquals(CDModifier.PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(9, languageClass.getCDMethodList().size());
  }

  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", languageClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }
  @Test
  public void testGetModelLoaderMethod() {
    ASTCDMethod method = getMethodBy("getModelLoader", languageClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AutomatonModelLoader", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetFileExtensionMethod() {
    ASTCDMethod method = getMethodBy("getFileExtension", languageClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetParserMethod() {
    ASTCDMethod method = getMethodBy("getParser", languageClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.ast.automaton._parser.AutomatonParser", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetSymbolTableCreatorMethod() {
    ASTCDMethod method = getMethodBy("getSymbolTableCreator", languageClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbolTableCreatorDelegator", method.getMCReturnType().getMCType());

    assertEquals(1,method.sizeCDParameters());
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.IAutomatonGlobalScope", method.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
  }

  @Test
  public void testProvideModelLoaderWithHandCodedLanguageMethod() {
    ASTCDMethod method = getMethodBy("provideModelLoader", languageClassTop);

    assertDeepEquals(PROTECTED_ABSTRACT, method.getModifier());
    assertDeepEquals("AutomatonModelLoader", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testProvideModelLoaderWithoutHandCodedLanguageMethod() {
    ASTCDMethod method = getMethodBy("provideModelLoader", languageClass);

    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals("AutomatonModelLoader", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCalculateModelNamesForStateMethod() {
    ASTCDMethod method = getMethodBy("calculateModelNamesForState", languageClass);

    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals("Set<String>", method.getMCReturnType().getMCType());

    assertEquals(1,method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testCalculateModelNamesForAutomatonMethod() {
    ASTCDMethod method = getMethodBy("calculateModelNamesForAutomaton", languageClass);

    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals("Set<String>", method.getMCReturnType().getMCType());

    assertEquals(1,method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testCalculateModelNamesForQualifiedNameMethod() {
    ASTCDMethod method = getMethodBy("calculateModelNamesForQualifiedName", languageClass);

    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals("Set<String>", method.getMCReturnType().getMCType());

    assertEquals(1,method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, languageClass, languageClass);
    System.out.println(sb.toString());
    StaticJavaParser.parse(sb.toString());
  }

}
