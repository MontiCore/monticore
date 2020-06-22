/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class SymbolLoaderBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass builderClass;

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  @Before
  public void setUp() {
    Log.init();
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));


    SymbolLoaderBuilderDecorator decorator = new SymbolLoaderBuilderDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit),
        new AccessorDecorator(glex, new SymbolTableService(decoratedCompilationUnit)));
    //creates ScopeSpanningSymbol
    ASTCDClass automatonClass = getClassBy("ASTAutomaton", decoratedCompilationUnit);
    this.builderClass = decorator.decorate(automatonClass);

    //creates normal Symbol
    ASTCDClass stateClass = getClassBy("ASTState", decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbolLoaderBuilder", builderClass.getName());
  }

  @Test
  public void testSuperNoInterfaces() {
    assertTrue(builderClass.isEmptyInterfaces());
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
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbolLoaderBuilder", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(5, builderClass.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("name", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("enclosingScope", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, astcdAttribute.getMCType());
  }

  @Test
  public void testRealBuilderAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("realBuilder", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("AutomatonSymbolLoaderBuilder", astcdAttribute.getMCType());
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
    assertDeepEquals(mcTypeFacade.createQualifiedType("AutomatonSymbolLoaderBuilder"), method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testGetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("AutomatonSymbolLoaderBuilder"), method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
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
