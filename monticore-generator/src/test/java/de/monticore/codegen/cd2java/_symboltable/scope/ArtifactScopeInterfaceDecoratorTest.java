/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
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
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
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
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceName() {
    assertEquals("IAutomatonArtifactScope", scopeInterface.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, scopeInterface.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(0, scopeInterface.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(41, scopeInterface.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetImportListMethod() {
    ASTCDMethod method = getMethodBy("getImportsList", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testSetImportsListMethod() {
    ASTCDMethod method = getMethodBy("setImportsList", scopeInterface);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(MCTypeFacade.createListTypeOf(IMPORT_STATEMENT), method.getCDParameter(0).getMCType());
    assertEquals("imports", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetTopLevelSymbolMethod() {
    ASTCDMethod method = getMethodBy("getTopLevelSymbol", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertOptionalOf("de.monticore.symboltable.ISymbol", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCheckIfContinueAsSubScopeMethod() {
    ASTCDMethod method = getMethodBy("checkIfContinueAsSubScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("symbolName", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetRemainingNameForResolveDownMethod() {
    ASTCDMethod method = getMethodBy("getRemainingNameForResolveDown", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("symbolName", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFullNameMethod(){
    ASTCDMethod method = getMethodBy("getFullName", scopeInterface);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.isEmptyCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testContinueWithEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("continueAutomatonWithEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testContinueWithEnclosingScopeSuperSymbolMethod() {
    ASTCDMethod method = getMethodBy("continueQualifiedNameWithEnclosingScope", scopeInterface);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createListTypeOf(QUALIFIED_NAME_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE_QUALIFIED_NAME, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
