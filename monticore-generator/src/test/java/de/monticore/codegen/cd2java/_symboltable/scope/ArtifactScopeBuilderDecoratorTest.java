package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ArtifactScopeBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private CDTypeFacade cdTypeFacade;

  private static final String ENCLOSING_SCOPE = "de.monticore.codegen.symboltable.cdforbuilder.artifactscope_builder._symboltable.IArtifactScope_BuilderScope";

  public static final String IMPORT_STATEMENT = "de.monticore.symboltable.ImportStatement";

  public static final String ARTIFACT_SCOPE_BUILDER = "AArtifactScopeBuilder";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable","cdForBuilder", "ArtifactScope_Builder");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    Log.init();
    ASTCDClass cdClass = getClassBy("AArtifactScope", decoratedCompilationUnit);

    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex), new SymbolTableService(decoratedCompilationUnit));
    ArtifactScopeBuilderDecorator decorator = new ArtifactScopeBuilderDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), builderDecorator, new AccessorDecorator(glex));

    //creates normal Symbol
    this.scopeClass = decorator.decorate(cdClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AArtifactScopeBuilder", scopeClass.getName());
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
    assertEquals(1, scopeClass.sizeCDConstructors());
  }


  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(0);
    assertDeepEquals(PROTECTED, cdConstructor.getModifier());
    assertEquals("AArtifactScopeBuilder", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributes() {
    assertEquals(5, scopeClass.getCDAttributeList().size());
  }

  @Test
  public void testPackageNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("packageName", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testImportsAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("imports", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertListOf(IMPORT_STATEMENT, astcdAttribute.getMCType());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("enclosingScope", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(ENCLOSING_SCOPE, astcdAttribute.getMCType());
  }

  @Test
  public void testRealBuilderAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("realBuilder", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(ARTIFACT_SCOPE_BUILDER, astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(46, scopeClass.getCDMethodList().size());
  }


  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("setPackageName", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(ARTIFACT_SCOPE_BUILDER, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("packageName", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetImportListMethod() {
    ASTCDMethod method = getMethodBy("getImportList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testSetImportsListMethod() {
    ASTCDMethod method = getMethodBy("setImportList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(ARTIFACT_SCOPE_BUILDER, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getCDParameter(0).getMCType());
    assertEquals("imports", method.getCDParameter(0).getName());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod method = getMethodBy("build", scopeClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("AArtifactScope", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeClass, scopeClass);
    StaticJavaParser.parse(sb.toString());
  }

}
