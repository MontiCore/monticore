/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class ArtifactScopeInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface scopeInterface;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private de.monticore.types.MCTypeFacade MCTypeFacade;

  private static final String IMPORT_STATEMENT = "de.monticore.symboltable.ImportStatement";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol";

  private static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String PREDICATE = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  private static final String PREDICATE_QUALIFIED_NAME = "java.util.function.Predicate<de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol>";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.MCTypeFacade = MCTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ArtifactScopeInterfaceDecorator decorator = new ArtifactScopeInterfaceDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit),new VisitorService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));

    //creates normal Symbol
    this.scopeInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testInterfaceName() {
    assertEquals("IAutomatonArtifactScope", scopeInterface.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, scopeInterface.sizeInterface());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(0, scopeInterface.sizeCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(43, scopeInterface.getCDMethodsList().size());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("setPackageName", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameters(0).getMCType());
    assertEquals("packageName", method.getCDParameters(0).getName());
  }

  @Test
  public void testGetImportListMethod() {
    ASTCDMethod method = getMethodBy("getImportsList", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testSetImportsListMethod() {
    ASTCDMethod method = getMethodBy("setImportsList", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(MCTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getCDParameters(0).getMCType());
    assertEquals("imports", method.getCDParameters(0).getName());
  }

  @Test
  public void testGetTopLevelSymbolMethod() {
    ASTCDMethod method = getMethodBy("getTopLevelSymbol", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertOptionalOf("de.monticore.symboltable.ISymbol", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCheckIfContinueAsSubScopeMethod() {
    ASTCDMethod method = getMethodBy("checkIfContinueAsSubScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameters(0).getMCType());
    assertEquals("symbolName", method.getCDParameters(0).getName());
  }

  @Test
  public void testGetRemainingNameForResolveDownMethod() {
    ASTCDMethod method = getMethodBy("getRemainingNameForResolveDown", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameters(0).getMCType());
    assertEquals("symbolName", method.getCDParameters(0).getName());
  }

  @Test
  public void testContinueWithEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("continueAutomatonWithEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameters(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameters(0).getName());
    assertDeepEquals(String.class, method.getCDParameters(1).getMCType());
    assertEquals("name", method.getCDParameters(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameters(2).getMCType());
    assertEquals("modifier", method.getCDParameters(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameters(3).getMCType());
    assertEquals("predicate", method.getCDParameters(3).getName());
  }

  @Test
  public void testContinueWithEnclosingScopeSuperSymbolMethod() {
    ASTCDMethod method = getMethodBy("continueQualifiedNameWithEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(QUALIFIED_NAME_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameters(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameters(0).getName());
    assertDeepEquals(String.class, method.getCDParameters(1).getMCType());
    assertEquals("name", method.getCDParameters(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameters(2).getMCType());
    assertEquals("modifier", method.getCDParameters(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameters(3).getMCType());
    assertEquals("predicate", method.getCDParameters(3).getName());
  }

  @Test
  public void testGetFullNameMethod(){
    ASTCDMethod method = getMethodBy("getFullName", scopeInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.isEmptyCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, scopeInterface, scopeInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
