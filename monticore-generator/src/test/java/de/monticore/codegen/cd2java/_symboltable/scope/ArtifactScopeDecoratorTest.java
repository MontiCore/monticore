package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.PATH;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArtifactScopeDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private CDTypeFacade cdTypeFacade;

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.AutomatonScope";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  public static final String IMPORT_STATEMENT = "de.monticore.symboltable.ImportStatement";

  public static final String QUALIFIED_NAMES_CALCULATOR = "de.monticore.symboltable.names.QualifiedNamesCalculator";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol";

  public static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  public static final String PREDICATE = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  public static final String PREDICATE_QUALIFIED_NAME = "java.util.function.Predicate<de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol>";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ArtifactScopeDecorator decorator = new ArtifactScopeDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), new MethodDecorator(glex));

    //creates normal Symbol
    this.scopeClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonArtifactScope", scopeClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertTrue(scopeClass.isEmptyInterfaces());
  }

  @Test
  public void testSuperClassCount() {
    assertTrue(scopeClass.isPresentSuperclass());
  }

  @Test
  public void testSuperClass() {
    assertDeepEquals(AUTOMATON_SCOPE, scopeClass.getSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(2, scopeClass.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonArtifactScope", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(String.class, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("packageName", cdConstructor.getCDParameter(0).getName());

    assertDeepEquals("List<de.monticore.symboltable.ImportStatement>", cdConstructor.getCDParameter(1).getMCType());
    assertEquals("imports", cdConstructor.getCDParameter(1).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }


  @Test
  public void testConstructorWithEnclosingScope() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructor(1);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonArtifactScope", cdConstructor.getName());

    assertEquals(3, cdConstructor.sizeCDParameters());

    assertDeepEquals("Optional<" + I_AUTOMATON_SCOPE + ">", cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());

    assertDeepEquals(String.class, cdConstructor.getCDParameter(1).getMCType());
    assertEquals("packageName", cdConstructor.getCDParameter(1).getName());

    assertDeepEquals("List<de.monticore.symboltable.ImportStatement>", cdConstructor.getCDParameter(2).getMCType());
    assertEquals("imports", cdConstructor.getCDParameter(2).getName());


    assertTrue(cdConstructor.isEmptyExceptions());
  }


  @Test
  public void testAttributeSize() {
    assertEquals(3, scopeClass.sizeCDAttributes());
  }

  @Test
  public void testPackageNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("packageName", scopeClass);
    assertDeepEquals(CDModifier.PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testImportsAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("imports", scopeClass);
    assertDeepEquals(CDModifier.PRIVATE, astcdAttribute.getModifier());
    assertListOf(IMPORT_STATEMENT, astcdAttribute.getMCType());
  }

  @Test
  public void testQualifiedNamesCalculatorAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("qualifiedNamesCalculator", scopeClass);
    assertDeepEquals(CDModifier.PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals(QUALIFIED_NAMES_CALCULATOR, astcdAttribute.getMCType());
  }

  @Test
  public void testMethodCount() {
    assertEquals(45, scopeClass.getCDMethodList().size());
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
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("packageName", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetQualifiedNamesCalculatorMethod() {
    ASTCDMethod method = getMethodBy("setQualifiedNamesCalculator", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(QUALIFIED_NAMES_CALCULATOR, method.getCDParameter(0).getMCType());
    assertEquals("qualifiedNamesCalculator", method.getCDParameter(0).getName());
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
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getCDParameter(0).getMCType());
    assertEquals("imports", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetNameOptMethod() {
    ASTCDMethod method = getMethodBy("getNameOpt", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertOptionalOf(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetTopLevelSymbolMethod() {
    ASTCDMethod method = getMethodBy("getTopLevelSymbol", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertOptionalOf("de.monticore.symboltable.ISymbol", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCheckIfContinueAsSubScopeMethod() {
    ASTCDMethod method = getMethodBy("checkIfContinueAsSubScope", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("symbolName", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetRemainingNameForResolveDownMethod() {
    ASTCDMethod method = getMethodBy("getRemainingNameForResolveDown", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("symbolName", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetFilePathMethod() {
    ASTCDMethod method = getMethodBy("getFilePath", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(PATH, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.AutomatonLanguage", method.getCDParameter(0).getMCType());
    assertEquals("lang", method.getCDParameter(0).getName());
  }

  @Test
  public void testContinueWithEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("continueAutomatonWithEnclosingScope", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createCollectionTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }

  @Test
  public void testContinueWithEnclosingScopeSuperSymbolMethod() {
    ASTCDMethod method = getMethodBy("continueQualifiedNameWithEnclosingScope", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createCollectionTypeOf(QUALIFIED_NAME_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
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
