package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.factories.MCTypeFacade;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class SymbolTableCreatorBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symTabCreatorClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private MCTypeFacade MCTypeFacade;

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String DEQUE_TYPE = "Deque<de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope>";

  private static final String BUILDER_NAME = "AutomatonSymbolTableCreatorBuilder";


  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.MCTypeFacade = MCTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymbolTableCreatorBuilderDecorator decorator = new SymbolTableCreatorBuilderDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit));

    //creates normal Symbol
    symTabCreatorClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonSymbolTableCreatorBuilder", symTabCreatorClass.getName());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertTrue(symTabCreatorClass.isEmptyInterfaces());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(symTabCreatorClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symTabCreatorClass.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = symTabCreatorClass.getCDConstructor(0);
    assertDeepEquals(PROTECTED, cdConstructor.getModifier());
    assertEquals("AutomatonSymbolTableCreatorBuilder", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(1, symTabCreatorClass.sizeCDAttributes());
  }

  @Test
  public void testScopeStackAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("scopeStack", symTabCreatorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(DEQUE_TYPE, astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(5, symTabCreatorClass.getCDMethodList().size());
  }

  @Test
  public void testSetScopeStackMethod() {
    ASTCDMethod method = getMethodBy("setScopeStack", symTabCreatorClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(BUILDER_NAME, method.getMCReturnType().getMCType());

    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(DEQUE_TYPE);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("scopeStack", method.getCDParameter(0).getName());
  }

  @Test
  public void testAddToScopeStackMethod() {
    ASTCDMethod method = getMethodBy("addToScopeStack", symTabCreatorClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(BUILDER_NAME, method.getMCReturnType().getMCType());

    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(I_AUTOMATON_SCOPE);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());
  }

  @Test
  public void testRemoveFromScopeStackMethod() {
    ASTCDMethod method = getMethodBy("removeFromScopeStack", symTabCreatorClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(BUILDER_NAME, method.getMCReturnType().getMCType());

    ASTMCType astType = this.MCTypeFacade.createTypeByDefinition(I_AUTOMATON_SCOPE);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("scope", method.getCDParameter(0).getName());
  }

  @Test
  public void tesGetScopeStackMethod() {
    ASTCDMethod method = getMethodBy("getScopeStack", symTabCreatorClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(DEQUE_TYPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod method = getMethodBy("build", symTabCreatorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(MCTypeFacade.createQualifiedType("AutomatonSymbolTableCreator"), method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symTabCreatorClass, symTabCreatorClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
