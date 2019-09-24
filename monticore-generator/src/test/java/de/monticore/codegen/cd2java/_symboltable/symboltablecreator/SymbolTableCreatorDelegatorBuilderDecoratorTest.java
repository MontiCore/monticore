package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

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
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class SymbolTableCreatorDelegatorBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass builderClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private CDTypeFacade cdTypeFacade;

  private static final String I_A_SCOPE = "IAScope";

  private static final String I_A_GLOBAL_SCOPE = "IAGlobalScope";

  private static final String BUILDER_NAME = "ASymbolTableCreatorDelegatorBuilder";

  private static final String DEQUE_TYPE = "Deque<ISubSubAutScope>";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "cdForBuilder","SymbolTableCreatorDelegator_Builder");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    SymbolTableCreatorDelegatorBuilderDecorator decorator = new SymbolTableCreatorDelegatorBuilderDecorator(this.glex,
        new BuilderDecorator(glex, new AccessorDecorator(glex), symbolTableService));
    ASTCDClass cdClass = getClassBy("ASymbolTableCreatorDelegator", decoratedCompilationUnit);

    builderClass = decorator.decorate(cdClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("ASymbolTableCreatorDelegatorBuilder", builderClass.getName());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertTrue( builderClass.isEmptyInterfaces());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(builderClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, builderClass.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = builderClass.getCDConstructor(0);
    assertDeepEquals(PROTECTED, cdConstructor.getModifier());
    assertEquals("ASymbolTableCreatorDelegatorBuilder", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertTrue(cdConstructor.isEmptyExceptions());
  }
  @Test
  public void testAttributeSize() {
    assertEquals(3, builderClass.sizeCDAttributes());
  }

  @Test
  public void testScopeStackAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("scopeStack", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Deque<ISubSubAutScope>", astcdAttribute.getMCType());
  }

  @Test
  public void testRealThisAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("realBuilder", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("ASymbolTableCreatorDelegatorBuilder", astcdAttribute.getMCType());
  }

  @Test
  public void testGlobalScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("globalScope", builderClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("IAGlobalScope", astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(6, builderClass.getCDMethodList().size());
  }

  @Test
  public void testSetScopeStackMethod() {
    ASTCDMethod method = getMethodBy("setScopeStack", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(BUILDER_NAME, method.getMCReturnType().getMCType());

    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(DEQUE_TYPE);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("scopeStack", method.getCDParameter(0).getName());
  }

  @Test
  public void tesGetScopeStackMethod() {
    ASTCDMethod method = getMethodBy("getScopeStack", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(DEQUE_TYPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetGlobalScopeMethod() {
    ASTCDMethod method = getMethodBy("setGlobalScope", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(BUILDER_NAME, method.getMCReturnType().getMCType());

    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(I_A_GLOBAL_SCOPE);
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(astType, method.getCDParameter(0).getMCType());
    assertEquals("globalScope", method.getCDParameter(0).getName());
  }

  @Test
  public void tesGetGlobalScopeMethod() {
    ASTCDMethod method = getMethodBy("getGlobalScope", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(I_A_GLOBAL_SCOPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod method = getMethodBy("build", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType("ASymbolTableCreatorDelegator"), method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }
  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, builderClass, builderClass);
    StaticJavaParser.parse(sb.toString());
  }
}
